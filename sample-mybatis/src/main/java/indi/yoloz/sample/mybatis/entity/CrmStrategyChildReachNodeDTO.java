package indi.yoloz.sample.mybatis.entity;

import java.time.LocalTime;

/**
 * @author yolo
 */
public class CrmStrategyChildReachNodeDTO {

    private Long id;
    //    @ApiModelProperty("子策略主键id crm_strategy_child#id")
    private Long strategyChildId;
    //    @ApiModelProperty("主策略id")
    private Long strategyId;
    //    @ApiModelProperty("模板id crm_template#id")
    private Long templateId;
    //    @ApiModelProperty("触达code，全局唯一")
    private String reachCode;
    //    @ApiModelProperty("模板名称")
    private String templateName;
    //    @ApiModelProperty("触达次数")
    private Integer reachNum;
    //    @ApiModelProperty("限制时长(s)")
    private Integer limitDuration;
    //    @ApiModelProperty("时间类型 #TimeTypeEnum")
    private Integer timeType;
    //    @ApiModelProperty("触达方式")
    private Integer reachWays;
    //    @ApiModelProperty("策略目的  com.nuanwa.app.crm.share.strategy#StrategyObjectiveEnum")
    private Integer objective;
    //    @ApiModelProperty("触发方式，1-定时，2-延时")
    private Integer triggerWay;
    //    @ApiModelProperty("延时时长（s）")
    private Integer delay;
    //    @ApiModelProperty("执行时间周期 cron表达式")
    private String implementCron;
    //    @ApiModelProperty("事件策略可触达时间开始时间")
    private LocalTime reachStartTime;
    //    @ApiModelProperty("事件策略可触达时间结束时间")
    private LocalTime reachEndTime;
    private String childStrategyCode;
    //    @JsonIgnore
    private String nodeType;
    //    @ApiModelProperty("模板信息")
//    private CrmTemplateDetailDTO templateDetail;
//    @ApiModelProperty("企业关联的用户")
//    private List<CrmStrategyChildWorkWechatUserDTO> workWechatUserList;
//    @ApiModelProperty("客群信息")
//    private List<CrmCustomerGroupDTO> groups;
//    private CrmStrategyChildInitNodeDTO initNodeDTO;
//    private OnsCrmNoticeDTO messageContext;
//    private OnsHealthSupplierDTO onsHealthSupplierDTO;
//    private List<CrmGroupDTO> crmGroupDTOList;
//    @ApiModelProperty("策略执行状态")
    private Integer childStrategyStatus;
    //    @ApiModelProperty("事件信息")
//    private CrmStrategyEventDTO event;
    private String eventSource;
//    private CrmStrategyEventSendRecordDO eventSendRecordDO;


    @Override
    public String toString() {
        return "CrmStrategyChildReachNodeDTO{" +
                "id=" + id +
                ", strategyChildId=" + strategyChildId +
                ", strategyId=" + strategyId +
                ", templateId=" + templateId +
                ", reachCode='" + reachCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", reachNum=" + reachNum +
                ", limitDuration=" + limitDuration +
                ", timeType=" + timeType +
                ", reachWays=" + reachWays +
                ", objective=" + objective +
                ", triggerWay=" + triggerWay +
                ", delay=" + delay +
                ", implementCron='" + implementCron + '\'' +
                ", reachStartTime=" + reachStartTime +
                ", reachEndTime=" + reachEndTime +
                ", childStrategyCode='" + childStrategyCode + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", childStrategyStatus=" + childStrategyStatus +
                ", eventSource='" + eventSource + '\'' +
                '}';
    }
}
