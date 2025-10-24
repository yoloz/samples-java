package indi.yoloz.sample.mybatis.entity;


/**
 * @author yolo
 */
public class BoolTest {

    private int id;
    private String name;
    private String email;
    private Boolean boolChar;   //char '1','o'
    private Boolean bool;       //tinyint 1,0
    private Boolean boolBit;    //bit b'1' b'0'

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getBoolChar() {
        return boolChar;
    }

    public void setBoolChar(Boolean boolChar) {
        this.boolChar = boolChar;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Boolean getBoolBit() {
        return boolBit;
    }

    public void setBoolBit(Boolean boolBit) {
        this.boolBit = boolBit;
    }

    @Override
    public String toString() {
        return "BoolTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", boolChar=" + boolChar +
                ", bool=" + bool +
                ", boolBit=" + boolBit +
                '}';
    }
}
