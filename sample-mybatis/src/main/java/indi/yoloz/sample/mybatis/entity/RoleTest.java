package indi.yoloz.sample.mybatis.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @since 10/14/25
 */

public class RoleTest {

    private long id;
    private long relationId;
    private String name;
    private String code;
    private String desc;
    private long pid = 0;
    private String service;
    private String userName;
    private String isDeleted;
    private String creator;
    private String modifier;
    private Date gmtCreated;
    private Date gmtModified;

    private String companyId;

    private Integer dataRangePnum = 0;
    private Integer dataRangeCnum = 0;
    private Integer dataRangeDnum = 0;
    /***
     * 用户选择标记
     **/
    private Boolean selected = false;
    private List<RoleTest> children = new ArrayList<>();
//    private List<ResourceVO> resources;

    public RoleTest() {

    }

    public void addChild(RoleTest role) {
        children.add(role);
    }

    @Override
    public String toString() {
        return "RoleTest{" +
                "id=" + id +
                ", relationId=" + relationId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", pid=" + pid +
                ", service='" + service + '\'' +
                ", userName='" + userName + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", creator='" + creator + '\'' +
                ", modifier='" + modifier + '\'' +
                ", gmtCreated=" + gmtCreated +
                ", gmtModified=" + gmtModified +
                ", companyId='" + companyId + '\'' +
                ", dataRangePnum=" + dataRangePnum +
                ", dataRangeCnum=" + dataRangeCnum +
                ", dataRangeDnum=" + dataRangeDnum +
                ", selected=" + selected +
                '}';
    }
}
