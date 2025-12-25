package indi.yoloz.sample.mybatis.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UaDepartment implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private String code;

    private Long companyId;

    private String companyName;

    private String name;

    private String businessCode;

    private String parentBusinessCode;

    private Long superiorLeader;

    private Integer parentDeptId;

    private String treeCode;

    private Long chargeCustId;

    private String chargeCustName;

    private Long delegateCustId;

    private String delegateCustName;

    private Integer departmentType;

    private String status;

    private Date gmtCreated;

    private Date gmtModified;

    private String creator;

    private String modifier;

    private String isDeleted;

    private String localeCode;

    private String extraInfo;

    /**
     * 机构级别
     */
    private String level;
    /**
     * 上级机构编码
     */
    private String parentCode;
    /**
     * 财务机构代码
     */
    private String financeDeptCode;

    /**
     * 地址中文名称
     */
    private String addressCName;
    /**
     * 邮编
     */
    private String postCode;
    /**
     * 电话
     */
    private String phoneNumber;
    /**
     * 是否是核算单位
     */
    private String centerFlag;
    /**
     * 网址
     */
    private String webAddress;
    /**
     * 服务电话
     */
    private String servicePhone;
    /**
     * 核算机构性质：N农险、F非农险
     */
    private String comFlag;
    /**
     * 行政区域名称
     */
    private String districtName;
    /**
     * 农险平台行政区划代码
     */
    private String districtPlatCode;
    /**
     * 保单归属地市
     */
    private String policyCityCode;
    /**
     * 保单归属地县
     */
    private String policyAreaCode;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 单证签章参数
     */
    private String pdfSealRuleNum;


    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }


    @JsonIgnore
    private List<UaDepartment> children = new ArrayList<>();

    public void addChild(UaDepartment child) {
        children.add(child);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getSuperiorLeader() {
        return superiorLeader;
    }

    public void setSuperiorLeader(Long superiorLeader) {
        this.superiorLeader = superiorLeader;
    }

    public Integer getParentDeptId() {
        return parentDeptId;
    }

    public void setParentDeptId(Integer parentDeptId) {
        this.parentDeptId = parentDeptId;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode == null ? null : treeCode.trim();
    }

    public Long getChargeCustId() {
        return chargeCustId;
    }

    public void setChargeCustId(Long chargeCustId) {
        this.chargeCustId = chargeCustId;
    }

    public Long getDelegateCustId() {
        return delegateCustId;
    }

    public void setDelegateCustId(Long delegateCustId) {
        this.delegateCustId = delegateCustId;
    }

    public Integer getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(Integer departmentType) {
        this.departmentType = departmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted == null ? null : isDeleted.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getParentBusinessCode() {
        return parentBusinessCode;
    }

    public void setParentBusinessCode(String parentBusinessCode) {
        this.parentBusinessCode = parentBusinessCode;
    }

    public String getChargeCustName() {
        return chargeCustName;
    }

    public void setChargeCustName(String chargeCustName) {
        this.chargeCustName = chargeCustName;
    }

    public String getDelegateCustName() {
        return delegateCustName;
    }

    public void setDelegateCustName(String delegateCustName) {
        this.delegateCustName = delegateCustName;
    }

    public List<UaDepartment> getChildren() {
        return children;
    }

    public void setChildren(List<UaDepartment> children) {
        this.children = children;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParentCode() {
        return parentCode;
    }

    public String getFinanceDeptCode() {
        return financeDeptCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void setFinanceDeptCode(String financeDeptCode) {
        this.financeDeptCode = financeDeptCode;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAddressCName() {
        return addressCName;
    }

    public void setAddressCName(String addressCName) {
        this.addressCName = addressCName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCenterFlag() {
        return centerFlag;
    }

    public void setCenterFlag(String centerFlag) {
        this.centerFlag = centerFlag;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getComFlag() {
        return comFlag;
    }

    public void setComFlag(String comFlag) {
        this.comFlag = comFlag;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictPlatCode() {
        return districtPlatCode;
    }

    public void setDistrictPlatCode(String districtPlatCode) {
        this.districtPlatCode = districtPlatCode;
    }

    public String getPolicyCityCode() {
        return policyCityCode;
    }

    public void setPolicyCityCode(String policyCityCode) {
        this.policyCityCode = policyCityCode;
    }

    public String getPolicyAreaCode() {
        return policyAreaCode;
    }

    public void setPolicyAreaCode(String policyAreaCode) {
        this.policyAreaCode = policyAreaCode;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPdfSealRuleNum() {
        return pdfSealRuleNum;
    }

    public void setPdfSealRuleNum(String pdfSealRuleNum) {
        this.pdfSealRuleNum = pdfSealRuleNum;
    }


    @Override
    public String toString() {
        return "UaDepartment{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", parentBusinessCode='" + parentBusinessCode + '\'' +
                ", superiorLeader=" + superiorLeader +
                ", parentDeptId=" + parentDeptId +
                ", treeCode='" + treeCode + '\'' +
                ", chargeCustId=" + chargeCustId +
                ", chargeCustName='" + chargeCustName + '\'' +
                ", delegateCustId=" + delegateCustId +
                ", delegateCustName='" + delegateCustName + '\'' +
                ", departmentType=" + departmentType +
                ", status='" + status + '\'' +
                ", gmtCreated=" + gmtCreated +
                ", gmtModified=" + gmtModified +
                ", creator='" + creator + '\'' +
                ", modifier='" + modifier + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", localeCode='" + localeCode + '\'' +
                ", extraInfo='" + extraInfo + '\'' +
                ", level='" + level + '\'' +
                ", parentCode='" + parentCode + '\'' +
                ", financeDeptCode='" + financeDeptCode + '\'' +
                ", addressCName='" + addressCName + '\'' +
                ", postCode='" + postCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", centerFlag='" + centerFlag + '\'' +
                ", webAddress='" + webAddress + '\'' +
                ", servicePhone='" + servicePhone + '\'' +
                ", comFlag='" + comFlag + '\'' +
                ", districtName='" + districtName + '\'' +
                ", districtPlatCode='" + districtPlatCode + '\'' +
                ", policyCityCode='" + policyCityCode + '\'' +
                ", policyAreaCode='" + policyAreaCode + '\'' +
                ", contacts='" + contacts + '\'' +
                ", pdfSealRuleNum='" + pdfSealRuleNum + '\'' +
                ", children=" + children +
                '}';
    }
}