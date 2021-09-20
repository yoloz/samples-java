package com.yoloz.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 审计返回的结果列表的具体信息
 * User: lil
 * Date: 2011-1-14
 * Time: 15:26:44
 */
public class AuditResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private String serviceId;	                     //服务编号
    private String result;    	                     //结果：“ok”是成功，“error”是失败
    private String desc; 	                         //结果如果是失败，则返回失败的代码，见附录《错误类型》

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
