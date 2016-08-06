package com.tcl.es;

import java.net.InetSocketAddress;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.protocol.Message;
import com.tcl.es.esclient.ESClient;
import com.tcl.es.esclient.ESUtils;

public class App  {

    public static void main(String args[]) {
        // 创建链接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                                                                                            11111), "example", "", "");
        ESClient esClient = new ESClient();
		esClient.init(Constanst.hosts+":"+Constanst.port, Constanst.cluster, Constanst.index);
		
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println("数据库没有变更empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
//                    ESUtils.setRefreshInterval(esClient.getClient(), esClient.getIndex(), -1);
                    printEntry(message.getEntries(),esClient);
//                    ESUtils.setRefreshInterval(esClient.getClient(), esClient.getIndex(), 1);
                }

                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }

        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<Entry> entrys ,ESClient client) {
    	
        for (Entry entry : entrys) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChage = null;
            try {
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                                           e);
            }

            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                                             entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                                             entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                                             eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                	JSONObject obj =  printColumn(rowData.getAfterColumnsList());
                	try {
//						client.delete(entry.getHeader().getTableName(), obj.getString("id"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                } else if (eventType == EventType.INSERT) {
                 JSONObject obj =  printColumn(rowData.getAfterColumnsList());
                 try {
                	 ESUtils.setRefreshInterval(client.getClient(), client.getIndex(), -1);
					client.index(entry.getHeader().getTableName(), obj.getString("id"), obj);
					 ESUtils.setRefreshInterval(client.getClient(), client.getIndex(), 1);
				} catch (Exception e) {
					e.printStackTrace();
				}
                } else if (eventType == EventType.UPDATE){
                	JSONObject obj =  printColumn(rowData.getAfterColumnsList());
                	try {
                		 ESUtils.setRefreshInterval(client.getClient(), client.getIndex(), -1);
						client.update(entry.getHeader().getTableName(), obj.getString("id"), obj);
						 ESUtils.setRefreshInterval(client.getClient(), client.getIndex(), 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }else {
                    System.out.println("-------> before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------> after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static  JSONObject printColumn(List<Column> columns) {
    	JSONObject jsonObject = new JSONObject();
        for (Column column : columns) {
        	jsonObject.put(column.getName(), column.getValue());
            System.out.println("变更属性"+column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
        return jsonObject;
    }
}
