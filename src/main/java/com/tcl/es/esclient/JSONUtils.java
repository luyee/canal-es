package com.tcl.es.esclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Splitter;

public class JSONUtils {


	public static List<String> convertJSONArray2StringList(JSONArray array)
	{
		if(array == null)
		{
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		
		for(Object o : array)
			list.add(String.valueOf(o));
		
		return list;
	}
	
	/**
	 * 解析jsonstring
	 * @param jsonstring
	 * @return 返回Object
	 */
	public static Object parse(String jsonstring)
	{
	    if(StringUtils.isEmpty(jsonstring))
	    {
	        return null;
	    }
	    
		Object parsed = null;
		try
		{
			parsed = JSON.parse(jsonstring, Feature.OrderedField);
			
			return parsed;
		}
		catch(Exception e)
		{
			LogUtils.logger().error(LogUtils.logStackTrace(e));
		}
		
		return null;
	}
	

	/**
	 * 解析jsonstring
	 * @param jsonstring
	 * @return 返回JSONObject
	 */
	public static JSONObject parseObject(String jsonstring)
	{
		JSONObject parsed = null;
		try
		{
			parsed = JSON.parseObject(jsonstring);
			
			return parsed;
		}
		catch(Exception e)
		{
			LogUtils.logger().error("JSONUtils::parseObject() error, 非法的请求:{}", jsonstring);
		}
		
		return null;
	}
	
	/**
	 * 解析jsonstring
	 * @param jsonstring
	 * @return 返回JSONArray
	 */
	public static JSONArray parseArray(String jsonstring)
	{
		JSONArray parsed = null;
		try
		{
			parsed = JSON.parseArray(jsonstring);
			return parsed;
		}
		catch(Exception e)
		{
			LogUtils.logger().error("JSONUtils::parseArray() error, 非法的请求:{}", jsonstring);
		}
		
		return null;
	}
	
	/**
	 * 解析jsonstring并转换为Java对象
	 * @param jsonstring
	 * @param clazz
	 * @return
	 */
	public static <T> T parse(String jsonstring, Class<T> clazz)
	{
		try
		{
			return JSON.parseObject(jsonstring, clazz);
		}
		catch(Exception e)
		{
			LogUtils.logger().error("JSONUtils::parse() error, 非法的请求:{}, clazz:{}", jsonstring, clazz);
		}
		return null;
	}
	
	/**
	 * Object转为 JSON String
	 * @param obj
	 * @return
	 */
	public static String toJSONString(Object obj)
	{
//		long t1 = System.currentTimeMillis();
		
		String string = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
		
//		long t2 = System.currentTimeMillis();
//		System.err.println("encode json:" + string + ", time: " + (t2-t1));
		
		return string;
	}
	
	/**
	 * 
	 * @param obj
	 * @param fields field list splitted by ","  for example:"id,name,category"
	 * @return
	 */
	public static JSONObject extractFields(JSONObject obj, String fields, boolean ignoreNull)
	{
		if(!"_ALL_".equals(fields))
		{
			JSONObject extracted = new JSONObject();
			List<String> fieldList = Splitter.on(",").trimResults().splitToList(fields);
			for(String f : fieldList) 
			{
				Object v = obj.get(f);
				if(ignoreNull && v == null)
				{
					continue;
				}
				extracted.put(f, v);
			}
			return extracted;
		}
		else
		{
			return obj;
		}
	}
	
	/**
     * 把 json list 转换成相对应的 map.
     * @param key  -  MAP KEY
     * @param list -  json list
     * @return
     */
    public static Map<String, JSONObject> list2Map(String key, List<JSONObject> list)
    {
        Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        
        if(StringUtils.isNotEmpty(key) && list != null)
        {
            for(JSONObject json : list)
            {
                String keyValue = json.getString(key);
                map.put(keyValue, json);
            }
        }
        
        return map;
    }
}