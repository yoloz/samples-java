package indi.yolo.sample.cxf.dao.enitity;

import java.util.List;
import java.io.Serializable;

/**
 * 审计的返回信息对象
 * User: lil
 * Date: 2011-1-14
 * Time: 15:17:52
 */
public class AuditResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identification;    //身份，见《身份列表》
    private String ip;                 //ip地址，不要是hostname
    private String version;            //版本，如平台4.5就写“4.5”
    private List auditResults;        //结果列表，类型是AuditResult

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List getAuditResults() {
        return auditResults;
    }

    public void setAuditResults(List auditResults) {
        this.auditResults = auditResults;
    }
}
