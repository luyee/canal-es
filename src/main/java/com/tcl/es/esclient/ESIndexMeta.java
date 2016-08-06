package com.tcl.es.esclient;

import java.util.HashMap;
import java.util.Map;

public class ESIndexMeta {
	
	private String name;

	private Map<String, ESTypeMeta> types = new HashMap<String, ESTypeMeta>();
	
	public void put(String type, ESTypeMeta meta)
	{
		types.put(type, meta);
	}
	
	public ESTypeMeta get(String type)
	{
		return types.get(type);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ESTypeMeta> getTypes() {
		return types;
	}

	public void setTypes(Map<String, ESTypeMeta> types) {
		this.types = types;
	}
	
	
}
