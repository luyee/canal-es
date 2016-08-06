package com.tcl.es.esclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.UnmodifiableIterator;

/**
 * @Description 适配es2.2.0
 * @author zhx
 * @date 2016年3月15日 下午2:28:04
 */

public class ESClient
{

    public static long INFO_TIME_MS = 100;

    public static long WARN_TIME_MS = 500;

    public static long ERROR_TIME_MS = 2000;

    private List<String> hosts = new ArrayList<String>();

    private String cluster;

    private String index;

    private ESIndexMeta indexMeta;

    private Settings settings;

    private TransportClient client;

    /**
     * 如果使用到写 oplog，就加上该配置
     */
    private String oplog_index;

    /**
     * 如果使用到写 oplog，就加上该配置
     */
    private String oplog_type;

    public String getIndex()
    {
        return index;
    }

    public ESIndexMeta getIndexMeta()
    {
        return indexMeta;
    }

    public String getOplog_index()
    {
        return oplog_index;
    }

    public void setOplog_index(String oplog_index)
    {
        this.oplog_index = oplog_index;
    }

    public String getOplog_type()
    {
        return oplog_type;
    }

    public void setOplog_type(String oplog_type)
    {
        this.oplog_type = oplog_type;
    }

    public void init(byte[] data) throws Exception
    {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(new ByteArrayInputStream(data));
        init(config);
    }

    public void init(PropertiesConfiguration config)
    {
        String[] hostArray = config.getStringArray("hosts");

        List<String> hosts = new ArrayList<String>();

        for (String h : hostArray)
        {
            hosts.add(h);
        }

        String cluster = config.getString("cluster");
        String index = config.getString("index");
        this.oplog_index = config.getString("oplog_index", null);
        this.oplog_type = config.getString("oplog_type", null);

        init(hosts, cluster, index);
    }

    public void init(String host, String cluster, String index, String oplog_index,
            String oplog_type)
    {
        this.oplog_index = oplog_index;
        this.oplog_type = oplog_type;
        List<String> hosts = new ArrayList<String>();
        hosts.add(host);
        init(hosts, cluster, index);
    }

    public void init(String host, String cluster, String index)
    {
        List<String> hosts = new ArrayList<String>();
        hosts.add(host);
        init(hosts, cluster, index);
    }

    public void init(List<String> hosts, String cluster, String index)
    {
        this.hosts = hosts;

        this.cluster = cluster;

        this.index = index;

        String strHsist = "";
        if (hosts != null)
        {
            for (String str : hosts)
            {
                strHsist += str + " ";
            }
        }
        System.out.println("====== ElasticSearch ===================================\n host: "
                + strHsist + "\n cluster: " + cluster + "\n index: " + index);
        LogUtils.logger().info(
                "====== ElasticSearch ===================================\n host: " + strHsist
                        + "\n cluster: " + cluster + "\n index: " + index);

        settings = Settings.settingsBuilder().put("cluster.name", cluster).build();

        client = TransportClient.builder().settings(settings).build();

        for (String h : hosts)
        {
            String[] arr = h.split(":");
            String host = arr[0];
            int port = Integer.parseInt(arr[1]);
            client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host,
                    port)));
        }

        try
        {
            indexMeta = loadMappings(index);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void delete(String type, String id) throws Exception
    {
        try
        {

            DeleteRequestBuilder builder = client.prepareDelete(index, type, id);

            LogUtils.logger().debug("ESRequest: query:\ncurl -XDELETE http://{}/{}/{}/{}",
                    hosts.get(0), index, type, id);

            DeleteResponse res = builder.get();
            LogUtils.logger().debug("ESResponse: {}", JSON.toJSON(res));
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void index(String type, String id, String parent_id, JSONObject obj) throws Exception
    {
        try
        {
            IndexRequestBuilder builder = client.prepareIndex(index, type, id).setSource(
                    JSONUtils.toJSONString(obj));

            if (StringUtils.isNotEmpty(parent_id))
            {
                builder.setParent(parent_id);
            }

            LogUtils.logger().debug("ESRequest: query:\ncurl -XPOST http://{}/{}/{}/{} -d'\n{}'",
                    hosts.get(0), index, type, id, JSONObject.toJSONString(obj, true));

            IndexResponse res = builder.get();

            LogUtils.logger().debug("ESResponse: {}", JSON.toJSON(res));
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public void index(String type, String id, JSONObject obj) throws Exception
    {
        index(type, id, null, obj);
    }

    public void update(String type, String id, JSONObject obj) throws Exception
    {
        update(type, id, null, obj);
    }

    public void update(String type, String id, String parent_id, JSONObject obj) throws Exception
    {
        try
        {
            UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setDoc(
                    JSONUtils.toJSONString(obj));

            if (StringUtils.isNotEmpty(parent_id))
            {
                builder.setParent(parent_id);
            }

            JSONObject body = new JSONObject();

            body.put("doc", obj);

            LogUtils.logger().debug(
                    "ESRequest: query:\ncurl -XPOST http://{}/{}/{}/{}/_update -d'\n{}'",
                    hosts.get(0), index, type, id, JSONObject.toJSONString(body, true));

            UpdateResponse res = builder.get();

            LogUtils.logger().debug("ESResponse: {}", JSON.toJSON(res));

        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public JSONObject get(String type, String id) throws Exception
    {
        return get(type, id, null);
    }

    public JSONObject get(String type, String id, String parentId) throws Exception
    {
        return get(type, id, parentId, null, null);
    }

    public JSONObject get(String type, String id, String[] fieldsInclude, String[] fieldsExclude)
            throws Exception
    {
        return get(type, id, null, fieldsInclude, fieldsExclude);
    }

    public JSONObject get(String type, String id, String parentId, String[] fieldsInclude,
            String[] fieldsExclude) throws Exception
    {
        LogUtils.logger().debug("get document from ES, type:{}, id:{}", type, id);

        try
        {
            GetRequestBuilder builder = client.prepareGet(index, type, id);

            if (fieldsInclude != null || fieldsExclude != null)
            {
                builder.setFetchSource(fieldsInclude, fieldsExclude);
            }

            if (!StringUtils.isEmpty(parentId))
            {
                builder.setParent(parentId);
            }

            LogUtils.logger().debug("ESRequest: query:\ncurl -XGET http://{}/{}/{}/{}",
                    hosts.get(0), index, type, id);

            GetResponse resp = builder.get();

            LogUtils.logger().debug("ESResponse: {}", resp.getSourceAsString());

            if (resp != null)
            {
                String s = resp.getSourceAsString();

                if (!StringUtils.isEmpty(s))
                {
                    JSONObject cat = JSON.parseObject(s);
                    return cat;
                }
            }

            return null;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public List<String> scrollIds(String type, int batchsize, int timeoutms)
    {
        SearchRequestBuilder builder = client.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.SCAN).setScroll(new TimeValue(timeoutms))
                .setFetchSource(false).setSize(batchsize); // 100 hits per shard
                                                           // will be returned
                                                           // for each scroll

        SearchResponse res = builder.get();

        List<String> ids = new ArrayList<String>();

        while (true)
        {
            for (SearchHit hit : res.getHits().getHits())
            {
                ids.add(hit.getId());
            }

            res = client.prepareSearchScroll(res.getScrollId()).setScroll(new TimeValue(timeoutms))
                    .get();

            if (res.getHits().getHits().length == 0)
            {
                break;
            }
        }

        return ids;
    }

    public SearchResponse agg(String index, String type,
            List<AbstractAggregationBuilder> aggregations) throws Exception
    {
        return search(index, type, SearchType.QUERY_THEN_FETCH, null, null, null, null, null, 0, 0,
                aggregations);
    }

    public SearchResponse search(String dataType, QueryBuilder queryBuilder,
            QueryBuilder filterBuilder, String[] fieldsInclude, String[] fieldsExclude,
            List<SortBuilder> sorts, int start, int size,
            List<AbstractAggregationBuilder> aggregations) throws Exception

    {
        return search(index, dataType, SearchType.DFS_QUERY_THEN_FETCH, queryBuilder,
                filterBuilder, fieldsInclude, fieldsExclude, sorts, start, size, aggregations);
    }

    public SearchResponse search(String dataType, SearchType searchType, QueryBuilder queryBuilder,
            QueryBuilder filterBuilder, String[] fieldsInclude, String[] fieldsExclude,
            List<SortBuilder> sorts, int start, int size,
            List<AbstractAggregationBuilder> aggregations) throws Exception
    {
        return search(index, dataType, searchType, queryBuilder, filterBuilder, fieldsInclude,
                fieldsExclude, sorts, start, size, aggregations);
    }

    public SearchResponse search(String index, String dataType, SearchType searchType,
            QueryBuilder queryBuilder, QueryBuilder filterBuilder, String[] fieldsInclude,
            String[] fieldsExclude, List<SortBuilder> sorts, int start, int size,
            List<AbstractAggregationBuilder> aggregations) throws Exception
    {
        SearchRequestBuilder builder = client.prepareSearch(index).setTypes(dataType)
                .setSearchType(searchType).setFrom(start).setSize(size);

        if (queryBuilder == null && filterBuilder == null)
        {
            builder.setQuery(QueryBuilders.matchAllQuery());
        }
        else
        {
            if (queryBuilder == null
                    || (queryBuilder instanceof BoolQueryBuilder && !((BoolQueryBuilder) queryBuilder)
                            .hasClauses()))
            {
                queryBuilder = QueryBuilders.matchAllQuery();
            }

            if (filterBuilder == null
                    || (filterBuilder instanceof BoolQueryBuilder && !((BoolQueryBuilder) filterBuilder)
                            .hasClauses()))
            {
                filterBuilder = QueryBuilders.matchAllQuery();
            }

            builder.setQuery(QueryBuilders.boolQuery().must(queryBuilder).filter(filterBuilder));
        }

        if (sorts != null)
        {
            for (SortBuilder sort : sorts)
            {
                builder.addSort(sort);
            }
        }

        if (fieldsInclude != null || fieldsExclude != null)
        {
            builder.setFetchSource(fieldsInclude, fieldsExclude);
        }

        if (aggregations != null)
        {
            for (AbstractAggregationBuilder aggregation : aggregations)
                builder.addAggregation(aggregation);
        }

        try
        {
            SearchResponse searchResponse = builder.get();

            SearchHits hits = searchResponse.getHits();

            long total = hits.getTotalHits();

            long took = searchResponse.getTookInMillis();

            if (took < INFO_TIME_MS)
            {
                // debug
                LogUtils.logger()
                        .debug("ESTAG  ESRequest: total:{}, tooktime:{} ms query:\ncurl -XPOST http://{}/{}/{}/_search?search_type={} -d'\n{}'",
                                total, took, hosts.get(0), index, dataType, searchType.toString(),
                                builder);
            }
            else if (took < WARN_TIME_MS)
            {
                // info
                LogUtils.logger()
                        .info("ESTAG  ESRequest: total:{}, tooktime:{} ms query:\ncurl -XPOST http://{}/{}/{}/_search?search_type={} -d'\n{}'",
                                total, took, hosts.get(0), index, dataType, searchType.toString(),
                                builder);
            }
            else if (took < ERROR_TIME_MS)
            {
                // warn
                LogUtils.logger()
                        .warn("ESTAG  ESRequest: total:{}, tooktime:{} ms query:\ncurl -XPOST http://{}/{}/{}/_search?search_type={} -d'\n{}'",
                                total, took, hosts.get(0), index, dataType, searchType.toString(),
                                builder);
            }
            else
            {
                LogUtils.logger()
                        .error("ESTAG  ESRequest: total:{}, tooktime:{} ms query:\ncurl -XPOST http://{}/{}/{}/_search?search_type={} -d'\n{}'",
                                total, took, hosts.get(0), index, dataType, searchType.toString(),
                                builder);
            }

            LogUtils.logger().trace("ESTAG ESResponse: {}", searchResponse);

            return searchResponse;
        }
        catch (Exception e)
        {
            LogUtils.logger().error(LogUtils.logStackTrace(e));
            throw e;
        }
    }

    public void mapping(String index, String type, JSONObject mapping) throws Exception
    {

        try
        {
            String mappingSource = mapping.toJSONString();

            PutMappingRequestBuilder mappingBuilder = client.admin().indices()
                    .preparePutMapping(index).setType(type).setSource(mappingSource);

            LogUtils.logger().debug(
                    "ESRequest: query:\ncurl -XPOST http://{}/{}/{}/_mapping -d'\n{}'",
                    hosts.get(0), index, type, JSONObject.toJSONString(mapping, true));

            PutMappingResponse res = mappingBuilder.get();

            LogUtils.logger().debug("ESResponse: {}", JSON.toJSON(res));
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public boolean indexExists(String index)
    {
        IndicesExistsResponse res = client.admin().indices().prepareExists(index).get();
        return res.isExists();
    }

    public static JSONObject response2json(SearchResponse searchResponse, JSONProcess process)
    {
        JSONObject res = new JSONObject();

        SearchHits hits = searchResponse.getHits();

        long total = hits.getTotalHits();

        long took = searchResponse.getTookInMillis();

        res.put("total", total);

        res.put("took", took);

        JSONArray results = new JSONArray();

        res.put("results", results);

        for (SearchHit hit : hits)
        {
            JSONObject obj = new JSONObject(hit.getSource());
            if (process != null)
            {
                obj = process.process(obj);
            }

            results.add(obj);
        }
        return res;
    }

    /**
     * 拼装rangeBuilder，数据间用 “~” 符号隔开，支持负数区间（）兼容之前的形式[0-10]
     * 
     * @Description
     * @author adolph.huang
     * @param field
     * @param range
     * @return
     */
    public static RangeQueryBuilder buildRangFilter(String field, String range)
    {
        boolean includeLower = range.startsWith("[");

        boolean includeUpper = range.endsWith("]");

        int index = range.indexOf("~");

        List<String> list = null;
        if (index > 0)
        {
            list = Splitter.on("~").splitToList(range.substring(1, range.length() - 1));
        }
        else
        {
            list = Splitter.on("-").splitToList(range.substring(1, range.length() - 1));
        }

        // List<String> list = Splitter.on("-").splitToList(range.substring(1,
        // range.length() - 1));

        if (list.size() != 2)
        {
            return null;
        }

        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);

        String low = list.get(0);

        String upper = list.get(1);

        if (!StringUtils.isEmpty(low))
        {
            builder.from(Double.parseDouble(low));
        }
        if (!StringUtils.isEmpty(upper))
        {
            builder.to(Double.parseDouble(upper));
        }

        builder.includeLower(includeLower);

        builder.includeUpper(includeUpper);

        return builder;
    }

    public static RangeQueryBuilder buildTimeRangeFilter(String field, Integer start, Integer end)
    {
        if (start == null && end == null)
            return null;

        RangeQueryBuilder rangeFilter = QueryBuilders.rangeQuery(field);

        if (start != null)
        {
            rangeFilter.from(start.intValue());
        }

        if (end != null)
        {
            rangeFilter.to(end.intValue());
        }

        return rangeFilter;
    }

    private static void parseProperties(String prefix, JSONObject properties,
            Map<String, ESPropertyMeta> propertyMap, Set<String> searchableFields,
            Set<String> allFields)
    {
        for (String property : properties.keySet())
        {
            String propertyName = prefix + property;

            ESPropertyMeta pmeta = new ESPropertyMeta();

            pmeta.setName(propertyName);

            JSONObject propobj = properties.getJSONObject(property);

            JSONObject subproperties = propobj.getJSONObject("properties");

            if (subproperties != null)
            {
                parseProperties(property + ".", subproperties, propertyMap, searchableFields,
                        allFields);
            }
            else
            {
                String property_type = propobj.getString("type");

                pmeta.setType(property_type);

                String property_index = propobj.getString("index");

                if (property_index == null)
                    property_index = "analyzed";

                pmeta.setIndex(property_index);

                propertyMap.put(propertyName, pmeta);

                if (!"no".equals(property_index))
                {
                    searchableFields.add(propertyName);
                }

                allFields.add(propertyName);
            }
        }
    }

    public ESIndexMeta loadMappings(String index) throws IOException
    {
        ClusterState cs = client.admin().cluster().prepareState().setIndices(index).get()
                .getState();

        IndexMetaData imd = cs.getMetaData().indices().valuesIt().next();

        if (imd == null)
        {
            return null;
        }

        ImmutableOpenMap<String, MappingMetaData> mappings = imd.getMappings();

        if (mappings == null)
        {
            return null;
        }

        UnmodifiableIterator<String> it = mappings.keysIt();

        if (indexMeta != null)
        {
            indexMeta.getTypes().clear();

            indexMeta = null;
        }

        ESIndexMeta indexMeta = new ESIndexMeta();

        while (it.hasNext())
        {
            String type = it.next();

            ESTypeMeta typeMeta = new ESTypeMeta();
            typeMeta.setName(type);

            Map<String, ESPropertyMeta> propertyMap = new HashMap<String, ESPropertyMeta>();
            Set<String> searchableFields = new HashSet<String>();
            Set<String> allFields = new HashSet<String>();

            typeMeta.setAllFields(allFields);
            typeMeta.setSearchableFields(searchableFields);
            typeMeta.setProperties(propertyMap);

            MappingMetaData meta = mappings.get(type);
            String json = meta.source().toString();
            JSONObject obj = JSONUtils.parseObject(json);

            JSONObject properties = obj.getJSONObject(type).getJSONObject("properties");

            parseProperties("", properties, propertyMap, searchableFields, allFields);

            indexMeta.put(type, typeMeta);

        }

        // LogUtils.logger().info("load ES mapping:" +
        // JSON.toJSON(indexMeta.getTypes()));

        return indexMeta;
    }

    public static String convert2string(Object value)
    {
        if (value == null)
        {
            return null;
        }
        String str = String.valueOf(value);
        return StringUtils.trim(str);
    }

    public static Object[] convert2estype(Object[] values, String esdtype)
    {
        List<Object> list = new ArrayList<Object>();

        for (Object value : values)
        {
            Object v2 = convert2estype(value, esdtype);
            if (v2 != null)
            {
                list.add(v2);
            }
        }

        return list.toArray();
    }

    public static Object convert2estype(Object value, String esdtype)
    {
        if (value == null)
        {
            return null;
        }

        if (ESDataTypes.STRING.equals(esdtype) && !(value instanceof String))
        {
            String str = convert2string(value);
            return str;
        }
        else if (ESDataTypes.LONG.equals(esdtype) && !(value instanceof Long))
        {
            String str = convert2string(value);
            if (StringUtils.isNumeric(str))
            {
                return Long.valueOf(str);
            }
            return null;
        }
        else if (ESDataTypes.DOUBLE.equals(esdtype) && !(value instanceof Double))
        {
            String str = convert2string(value);
            if (StringUtils.isNumeric(str))
            {
                return Double.valueOf(str);
            }
            return null;
        }
        else if (ESDataTypes.BYTE.equals(esdtype) && !(value instanceof Byte))
        {
            String str = convert2string(value);
            if (StringUtils.isNumeric(str))
            {
                return Byte.valueOf(str);
            }
            return null;
        }
        else if (ESDataTypes.BOOL.equals(esdtype) && !(value instanceof Boolean))
        {
            String str = convert2string(value);
            return Boolean.valueOf(str);
        }

        return value;
    }

    /**
     * 设置 RefreshInterval 刷新频率
     * 
     * @param interval
     */
    public void setRefreshInterval(String interval)
    {
        client.admin().indices().prepareUpdateSettings(index)
                .setSettings(Settings.settingsBuilder().put("refresh_interval", interval).build())
                .get();
    }

    public List<String> getHosts()
    {
        return hosts;
    }

    public String getCluster()
    {
        return cluster;
    }

    public TransportClient getClient()
    {
        return client;
    }

    public JSONObject termVector(String type, String id, String field) throws Exception
    {
        TermVectorsRequestBuilder b = client.prepareTermVectors(index, type, id);

        b.setSelectedFields(field).setOffsets(false).setPayloads(false).setPositions(false)
                .setTermStatistics(true).setFieldStatistics(true);

        TermVectorsResponse response = b.get();
        if (response.isExists())
        {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            response.toXContent(builder, ToXContent.EMPTY_PARAMS);
            builder.endObject();

            String json = builder.string();

            JSONObject obj = JSONUtils.parseObject(json);

            return obj.getJSONObject("term_vectors").getJSONObject(field);
        }

        return null;
    }

    public void writeOperateLog(JSONObject request, List<String> methods)
    {
        try
        {
            String method = request.getString(Keys.INTERFACE);

            if (StringUtils.isEmpty(method) && methods != null)
            {
                return;
            }

            if (methods.contains(method))
            {
                Object args = request.get(Keys.ARGS);
                String sercvie = request.getString(Keys.SERVICE);
                String ip = request.getString("ip");
                int uid = request.getIntValue("uid");

                JSONObject oplog = new JSONObject();
                oplog.put("operatetime", System.currentTimeMillis());
                oplog.put("paraments", args == null ? null : args.toString());
                oplog.put("ip", ip);
                oplog.put("uid", uid);
                oplog.put("usemethod", method);
                oplog.put("useclass", sercvie);

                IndexResponse resp = client.prepareIndex(oplog_index, oplog_type)
                        .setSource(oplog.toJSONString()).get();

                LogUtils.logger().debug("writeOperateLog[{}] : {}", resp.isCreated(),
                        oplog.toJSONString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtils.logger().error(LogUtils.logStackTrace(e));
        }
    }

    public static JSONObject toEsResult(SearchResponse resp)
    {
        JSONObject res = new JSONObject();

        if (resp != null)
        {
            long total = resp.getHits().getTotalHits();

            SearchHit[] hit = resp.getHits().getHits();

            JSONObject response = new JSONObject();
            JSONArray results = new JSONArray();

            for (SearchHit h : hit)
            {
                String json = h.getSourceAsString();

                String id = h.getId();

                JSONObject item = JSONUtils.parseObject(json);

                item.put("id", id);

                results.add(item);
            }

            response.put("total", total);
            response.put("results", results);

            res.put(Keys.SUCC, response);
        }
        else
        {
            res.put(Keys.SUCC, resp);
        }
        return res;
    }

    public static void main(String[] args) throws Exception
    {
        // ESClient c = new ESClient();
        //
        // List<String> hosts = new ArrayList<String>();
        //
        // hosts.add("192.168.1.102:9300");
        //
        // c.init(hosts, "crmtest_241", "filter");

        // c.index("dict", "1", obj); //txt,level

        // 1
        // c.index("dict", "12", obj);

        // 2 scan
        // List<String> ids = c.scrollIds("company", 1000, 60000);

        // 3 term vector
        // JSONObject field = c.termVector("dict", "1", "txt");

        // System.out.println(ids.size());
        // System.out.println(ids);

        System.out.println(buildRangFilter("abc", "(-11~11212]"));
    }

}
