package indi.yoloz.sample.mybatis.entity;

import java.time.LocalDateTime;

/**
 * LocalDateTime
 *
 * @author yolo
 */
public class CrmStrategyChild {

    private int id;
    private String name;
    private String strategyCode;
    private Integer reachWays;
    private Long templateId;
    private Integer objective;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String implementCron;
    private Integer status;
    private String beanName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrategyCode() {
        return strategyCode;
    }

    public void setStrategyCode(String strategyCode) {
        this.strategyCode = strategyCode;
    }

    public Integer getReachWays() {
        return reachWays;
    }

    public void setReachWays(Integer reachWays) {
        this.reachWays = reachWays;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getObjective() {
        return objective;
    }

    public void setObjective(Integer objective) {
        this.objective = objective;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getImplementCron() {
        return implementCron;
    }

    public void setImplementCron(String implementCron) {
        this.implementCron = implementCron;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public String toString() {
        return "CrmStrategyChild{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", strategyCode='" + strategyCode + '\'' +
                ", reachWays=" + reachWays +
                ", templateId=" + templateId +
                ", objective=" + objective +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", implementCron='" + implementCron + '\'' +
                ", status=" + status +
                ", beanName='" + beanName + '\'' +
                '}';
    }
}
