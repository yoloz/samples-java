package indi.yoloz.sample.mybatis.entity;

/**
 * @author yolo
 */
public class CrmStrategyDTO {

    //    @ApiModelProperty("ID")
    private Long id;
    //    @ApiModelProperty("策略名称")
    private String name;
    //    @ApiModelProperty("自定义策略id(code)")
    private String strategyCode;
    //    @ApiModelProperty("策略描述")
    private String description;
    //    @ApiModelProperty("保险公司code")
    private String orgCode;
    //    @ApiModelProperty("保险产品（预留字段）")
    private String insuranceProduct;
    //    @ApiModelProperty("触达次数")
    private Integer reachNum;
    //    @ApiModelProperty("审核状态（1-提交审核(默认) 2-审核驳回 3-审核成功）")
    private Integer auditStatus;
    //    @ApiModelProperty("投放状态（1-待投放(默认) 2-投放中 3-投放完成 4-投放暂停 5-投放取消）")
    private Integer launchStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getInsuranceProduct() {
        return insuranceProduct;
    }

    public void setInsuranceProduct(String insuranceProduct) {
        this.insuranceProduct = insuranceProduct;
    }

    public Integer getReachNum() {
        return reachNum;
    }

    public void setReachNum(Integer reachNum) {
        this.reachNum = reachNum;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getLaunchStatus() {
        return launchStatus;
    }

    public void setLaunchStatus(Integer launchStatus) {
        this.launchStatus = launchStatus;
    }
}
