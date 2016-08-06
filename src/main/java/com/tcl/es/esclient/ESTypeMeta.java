package com.tcl.es.esclient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ESTypeMeta {

	private String name;
	
	private Map<String, ESPropertyMeta> properties = new HashMap<String, ESPropertyMeta>();
	
	private Set<String> searchableFields = new HashSet<String>();
	
	private Set<String> allFields = new HashSet<String>();

	public Map<String, ESPropertyMeta> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, ESPropertyMeta> properties) {
		this.properties = properties;
	}

	public Set<String> getSearchableFields() {
		return searchableFields;
	}

	public void setSearchableFields(Set<String> searchableFields) {
		this.searchableFields = searchableFields;
	}

	public Set<String> getAllFields() {
		return allFields;
	}

	public void setAllFields(Set<String> allFields) {
		this.allFields = allFields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
