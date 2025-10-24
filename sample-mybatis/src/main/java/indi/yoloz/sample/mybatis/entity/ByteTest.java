package indi.yoloz.sample.mybatis.entity;


/**
 * @author yolo
 */
public class ByteTest {

    private int id;
    private String name;
    private Byte byte1; //tinyint
    //mysql数据库getObject()可能返回true,dsg-sdk需要支持转成数字
    private Byte byte2;  //tinyint(1)

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

    public Byte getByte1() {
        return byte1;
    }

    public void setByte1(Byte byte1) {
        this.byte1 = byte1;
    }

    public Byte getByte2() {
        return byte2;
    }

    public void setByte2(Byte byte2) {
        this.byte2 = byte2;
    }

    @Override
    public String toString() {
        return "ByteTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", byte1=" + byte1 +
                ", byte2=" + byte2 +
                '}';
    }
}
