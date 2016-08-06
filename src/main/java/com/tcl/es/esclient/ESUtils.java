package com.tcl.es.esclient;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 适配es2.2.0
 * @author zhx
 * @date 2016年3月15日 下午4:29:30
 */

public class ESUtils
{
    /**
     * 创建 INDEX，需要注意的是如果存在该 INDEX，则不会再创建新的 INDEX
     * 
     * @param client
     * @param index
     */
    public static void createIndex(Client client, String index, String setting)
    {
        if (!isExistsIndex(client, index))
        {
            if (StringUtils.isNotEmpty(setting))
            {
                client.admin().indices().prepareCreate(index).setSettings(setting).get();
            }
            else
            {
                client.admin().indices().prepareCreate(index).get();
            }
        }
    }

    /**
     * 创建 INDEX，需要注意的是如果存在该 INDEX，则不会再创建新的 INDEX
     * 
     * @param client
     * @param index
     */
    public static void createIndex(Client client, String index)
    {
        createIndex(client, index, null);
    }

    /**
     * 重新创建 INDEX，需要注意的是如果存在该 INDEX，则删除原来的，再创建新的 INDEX
     * 
     * @param client
     * @param index
     */
    public static void reCreateIndex(Client client, String index)
    {
        reCreateIndex(client, index, null);
    }

    /**
     * 重新创建 INDEX，需要注意的是如果存在该 INDEX，则删除原来的，再创建新的 INDEX
     * 
     * @param client
     * @param index
     */
    public static void reCreateIndex(Client client, String index, String setting)
    {
        if (isExistsIndex(client, index))
        {
            deleteIndex(client, index);
        }
        if (StringUtils.isNotEmpty(setting))
        {
            client.admin().indices().prepareCreate(index).setSettings(setting).get();
        }
        else
        {
            client.admin().indices().prepareCreate(index).get();
        }
    }

    public static void deleteIndex(Client client, String index)
    {
        client.admin().indices().prepareDelete(index).get();
    }

    public static boolean isExistsIndex(Client client, String index)
    {
        IndicesExistsResponse res = client.admin().indices().prepareExists(index).execute()
                .actionGet();
        if (res.isExists())
        {
            return true;
        }
        return false;
    }

    /**
     * 创建 MAPPING，如果mapping已存在，会覆盖
     * 
     * @param client
     * @param index
     * @param type
     * @param mapping
     */
    public static void setMapping(Client client, String index, String type, String mapping)
    {
        try
        {
            client.admin().indices().preparePutMapping(index).setType(type).setSource(mapping)
                    .execute().actionGet();
            client.admin().indices().prepareRefresh(index).get();
        }
        catch (Exception e)
        {
            LogUtils.logger().error("创建 mapping 失败！{} / {}", e, LogUtils.logStackTrace(e));
            e.printStackTrace();
        }
    }

    /**
     * @Description 获取mapping
     * @author zhx
     * @param client
     * @param index
     * @param type
     * @return
     */

    public static JSONObject getMapping(Client client, String index, String type)
    {
        JSONObject mapping = new JSONObject();
        try
        {
            IndicesAdminClient indicesAdminClient = client.admin().indices();
            GetMappingsResponse getMappingsResponse = indicesAdminClient.prepareGetMappings(index)
                    .setTypes(type).get();
            JSONObject mappingMetaData = (JSONObject) JSONObject.toJSON(getMappingsResponse
                    .getMappings().get(index).get(type).sourceAsMap());
            JSONObject typeMapping = new JSONObject();
            typeMapping.put(type, mappingMetaData);
            mapping.put(index, typeMapping);

        }
        catch (IOException e)
        {
            LogUtils.logger().error("获取 mapping 失败！{} / {}", e, LogUtils.logStackTrace(e));
            e.printStackTrace();
        }

        return mapping;
    }

    /**
     * 设置 es 数据刷新频率
     * 
     * @Description
     * @author adolph.huang
     * @param client
     * @param index
     * @param interval
     *            -1 ： 为不刷新，1 : 秒刷新一次， 2.3.4...
     */
    public static void setRefreshInterval(Client client, String index, int interval)
    {
        String val = interval == -1 ? -1 + "" : interval + "s";
        client.admin().indices().prepareUpdateSettings(index)
                .setSettings(Settings.builder().put("refresh_interval", val).build()).execute()
                .actionGet();
    }
}
