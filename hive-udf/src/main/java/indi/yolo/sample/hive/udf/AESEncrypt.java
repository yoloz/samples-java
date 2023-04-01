package indi.yolo.sample.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

@Description(name = "Encrypt", value = "Encrypt the Given Column", extended = "SELECT Encrypt('Hello World!', 'Key');")
public class AESEncrypt extends UDF {

    public Object evaluate(Object obj, String key) {
        if (obj == null) {
            return null;
        }
        return Util.AESEncrypt(String.valueOf(obj), key);
    }

}
