package indi.yolo.sample;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author yolo
 */
public class TestBean implements Serializable {

    private String a;
    private String b;
    private String c;

    public TestBean() {
        this(null, null, null);
    }

    public TestBean(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBean testBean = (TestBean) o;
        return Objects.equals(a, testBean.a) && Objects.equals(b, testBean.b) && Objects.equals(c, testBean.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }
}
