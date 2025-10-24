package indi.yoloz.sample.mybatis.entity;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @since 10/16/25
 */
public class Date2Timestamp {

    private Integer id;
    private String name;
    private Float aget;
    private LocalDateTime datetime;
    private Date date;
    private Timestamp timestamp;
    private Time time;

    @Override
    public String toString() {
        return "TimestampTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", aget=" + aget +
                ", datetime=" + datetime +
                ", date=" + date +
                ", timestamp=" + timestamp +
                ", time=" + time +
                '}';
    }
}
