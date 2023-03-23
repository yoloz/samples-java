package indi.yolo.sample.cxf.dao.enitity;

import java.io.Serializable;

/**
 * 报警的审计信息对象
 * User: lil
 * Date: 2011-1-14
 * Time: 15:37:19
 */
public class AlarmInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identification = "GXPT";       //	身份，见《身份列表》
    private String ip;                              //	ip地址，不要是hostname
    private String version;                        //	版本，如平台4.5就写“4.5”
    private String devId;                          //	设备编号
    private String serviceId;                      //	服务编号
    private String alarmDate;                      //	报警日期，格式“yyyy-mm-dd”
    private String alarmTime;                      //	报警详细时间，格式“yyyy年mm月dd日HH时MM分SS秒”
    private String source;                         //	报警主体，即访问端
    private String action;                         //	动作类型，例如：监听
    private String dest;                           //	报警客体，即被访问端，如果type是服务自身报警，则不填写此项
    private String alarmResult;                   //	结果，即报警主体访问报警客体的结果
    private String type;                           //	报警类别，见《报警类型》
    private String level;                          //	报警等级，见《报警级别》

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

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAlarmResult() {
        return alarmResult;
    }

    public void setAlarmResult(String alarmResult) {
        this.alarmResult = alarmResult;
    }
}
