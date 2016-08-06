package com.tcl.es.esclient;

import java.util.Arrays;

/**
 * 
 * @Description  对应es type  主要用于描述关联type 同步时同时更新关联type
 *               ESType类在服务启动后存放在map中做缓存，如有调 用修改map中的estype，会导致其他方法调用的estype被修改
 *               从而导致错误，故设置为不可继承和创建后不允许修改
 * @author golao.deng
 * @date 2016年1月6日 上午11:37:27
 */
public final class ESType
{
    private  String type;
    private  String[] associationTypes;
    public ESType(final String type,final String[] at)
    {
        this.type = type;
        this.associationTypes = at;
    }
    public String getType()
    {
        return type;
    }
   
    public String[] getAssociationTypes()
    {
        return associationTypes;
    }
 
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(associationTypes);
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
        ESType other = (ESType) obj;
        if (!Arrays.equals(associationTypes, other.associationTypes))
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
        return "ESType [type=" + type + ", associationTypes=" + Arrays.toString(associationTypes)
                + "]";
    }
    
    
}
