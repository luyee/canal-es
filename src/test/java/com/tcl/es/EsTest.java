package com.tcl.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tcl.es.esclient.ESClient;

public class EsTest {

	public static void main(String[] args) {
		ESClient esClient = new ESClient();
		esClient.init(Constanst.hosts+":"+Constanst.port, "elasticsearch", "hwwork");
		
		try {
			System.out.println("是否存在"+esClient.indexExists("hwwork"));
			HwWorkorder obj = new HwWorkorder();
			obj.setCustomername("zhhonghuixio");;
			esClient.index("hw_workorder_test", "f6e03cc0-a773-4598-a54c-6b378bc8c9e2", (JSONObject) JSON.toJSON(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
