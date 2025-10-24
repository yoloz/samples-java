package indi.yoloz.sample.mybatis.entity;

/**
 * @author yolo
 */
public class IntegerTest {
    private Integer id;   // int
    private Integer charInt;  //char, empty char return 0

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCharInt() {
        return charInt;
    }

    public void setCharInt(Integer charInt) {
        this.charInt = charInt;
    }

    @Override
    public String toString() {
        return "IntegerTest{" +
                "id=" + id +
                ", charInt=" + charInt +
                '}';
    }
}
