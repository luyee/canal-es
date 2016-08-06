package com.tcl.es.esclient;

import java.util.List;

/**
 * 
 * @Description db table 关联 es type
 * @author golao.deng
 * @date 2016年1月6日 下午4:25:43
 */
public final class Schema
{
    private String schema;

    //是否需要进行缓存 TODO
    private boolean isCache;
    
    // one to one
    private ESType type;

    private boolean isSub; // 是否 为es type的子类属性 (该table无对应的es type,仅是type下的一个子属性)

    public Relations relations;

    // private String esSubName; //在 isSub=true 前提下 table 在 type中的属性名
    // 数据库表 schema 的主键
    private String dbId;

    // 关联字段
    private String dbField;

    private String esId;

    // one to many
    private List<Schema> many;

    public Schema(final String table, final ESType type, final boolean isSub,
            final String esSubName,final String dataType,final String relation,
            final String relationField,
            final String dbId, final String dbField, final String esId)
    {
        this.schema = table;
        this.type = type;
        this.isSub = isSub;
        if (this.isSub == true)
        {
            if(dataType != null && relation != null)
            {
                this.relations = new Relations(esSubName,dataType,relation,relationField);
            }else{
                this.relations = new Relations(esSubName,relationField);
            }
            
        }
        this.dbId = dbId;
        this.dbField = dbField;
        this.esId = esId;

    }
  
   public class Relations
    {
        private String esSubName; // type子属性名

        private String dataType = "string"; // 子属性类型 默认为 String

        private String relation = "one"; // 默认为1对1
        
        private String relationField; //关联字段

        public Relations(String esSubName, String dataType, String relation,String relationField)
        {
            this.esSubName = esSubName;
            this.dataType = dataType;
            this.relation = relation;
            this.relationField = relationField;
        }

        public Relations(String esSubName,String relationField)
        {
            this.esSubName = esSubName;
            this.relationField = relationField;
        }

        public String getEsSubName()
        {
            return this.esSubName;
        }

        public String getDataType()
        {
            return this.dataType;
        }

        public String getRelation()
        {
            return this.relation;
        }
        

        public String getRelationField()
        {
            return relationField;
        }

        @Override
        public String toString()
        {
            return "Relations [esSubName=" + esSubName + ", dataType=" + dataType + ", relation="
                    + relation + ", relationField=" + relationField + "]";
        }

     
    }

    public void setMany(final List<Schema> many)
    {
        this.many = many;
    }

    public String getDbField()
    {
        return dbField;
    }

    public String getTable()
    {
        return schema;
    }

    public ESType getType()
    {
        return type;
    }

    public boolean isSub()
    {
        return isSub;
    }

    public String getDbIDFieldName()
    {
        return dbId;
    }

    public String getEsIDName()
    {
        return esId;
    }

    public List<Schema> getMany()
    {
        return many;
    }
    
    public boolean isCache()
    {
        return isCache;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbField == null) ? 0 : dbField.hashCode());
        result = prime * result + ((dbId == null) ? 0 : dbId.hashCode());
        result = prime * result + ((esId == null) ? 0 : esId.hashCode());
        result = prime * result + (isSub ? 1231 : 1237);
        result = prime * result + ((many == null) ? 0 : many.hashCode());
        result = prime * result + ((schema == null) ? 0 : schema.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schema other = (Schema) obj;
        if (dbField == null)
        {
            if (other.dbField != null)
                return false;
        }
        else if (!dbField.equals(other.dbField))
            return false;
        if (dbId == null)
        {
            if (other.dbId != null)
                return false;
        }
        else if (!dbId.equals(other.dbId))
            return false;
        if (esId == null)
        {
            if (other.esId != null)
                return false;
        }
        else if (!esId.equals(other.esId))
            return false;
        if (isSub != other.isSub)
            return false;
        if (many == null)
        {
            if (other.many != null)
                return false;
        }
        else if (!many.equals(other.many))
            return false;
        if (schema == null)
        {
            if (other.schema != null)
                return false;
        }
        else if (!schema.equals(other.schema))
            return false;
        if (type == null)
        {
            if (other.type != null)
                return false;
        }
        else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Schema [schema=" + schema + ", type=" + type + ", isSub=" + isSub + ", relations="
                + relations + ", dbId=" + dbId + ", dbField=" + dbField + ", esId=" + esId
                + ", many=" + many + "]";
    }

   
    
}
