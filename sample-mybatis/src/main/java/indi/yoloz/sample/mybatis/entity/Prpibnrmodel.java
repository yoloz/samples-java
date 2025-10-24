package indi.yoloz.sample.mybatis.entity;

import java.util.Date;

public class Prpibnrmodel {
    /**
     * 任务编号
     */
    private String taskcode;
    /**
     * 记录日期
     */
    private Date insertdate;
    /**
     * 预留1
     */
    private String tcol1;
    /**
     * 预留2
     */
    private String tcol2;
    /**
     * 预留3
     */
    private String tcol3;
    /**
     * 模型
     */
    private String ibnrmodel;

    /**
     * 任务编号
     *
     * @return TASKCODE 任务编号
     */
    public String getTaskcode() {
        return taskcode;
    }

    /**
     * 任务编号
     *
     * @param taskcode 任务编号
     */
    public void setTaskcode(String taskcode) {
        this.taskcode = taskcode == null ? null : taskcode.trim();
    }

    /**
     * 记录日期
     *
     * @return INSERTDATE 记录日期
     */
    public Date getInsertdate() {
        return insertdate;
    }

    /**
     * 记录日期
     *
     * @param insertdate 记录日期
     */
    public void setInsertdate(Date insertdate) {
        this.insertdate = insertdate;
    }

    /**
     * 预留1
     *
     * @return TCOL1 预留1
     */
    public String getTcol1() {
        return tcol1;
    }

    /**
     * 预留1
     *
     * @param tcol1 预留1
     */
    public void setTcol1(String tcol1) {
        this.tcol1 = tcol1 == null ? null : tcol1.trim();
    }

    /**
     * 预留2
     *
     * @return TCOL2 预留2
     */
    public String getTcol2() {
        return tcol2;
    }

    /**
     * 预留2
     *
     * @param tcol2 预留2
     */
    public void setTcol2(String tcol2) {
        this.tcol2 = tcol2 == null ? null : tcol2.trim();
    }

    /**
     * 预留3
     *
     * @return TCOL3 预留3
     */
    public String getTcol3() {
        return tcol3;
    }

    /**
     * 预留3
     *
     * @param tcol3 预留3
     */
    public void setTcol3(String tcol3) {
        this.tcol3 = tcol3 == null ? null : tcol3.trim();
    }

    /**
     * 模型
     *
     * @return IBNRMODEL 模型
     */
    public String getIbnrmodel() {
        return ibnrmodel;
    }

    /**
     * 模型
     *
     * @param ibnrmodel 模型
     */
    public void setIbnrmodel(String ibnrmodel) {
        this.ibnrmodel = ibnrmodel == null ? null : ibnrmodel.trim();
    }


    @Override
    public String toString() {
        return "Prpibnrmodel{" +
                "taskcode='" + taskcode + '\'' +
                ", insertdate=" + insertdate +
                ", tcol1='" + tcol1 + '\'' +
                ", tcol2='" + tcol2 + '\'' +
                ", tcol3='" + tcol3 + '\'' +
                ", ibnrmodel='" + ibnrmodel + '\'' +
                '}';
    }
}