package indi.yolo.sample.hive.udf.generic;

import indi.yolo.sample.hive.udf.Util;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

@Description(name = "Encrypt", value = "Encrypt the Given Column", extended = "SELECT Encrypt('Hello World!', 'Key');")
public class AESEncrypt extends GenericUDF {

    StringObjectInspector key;
    StringObjectInspector col;

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 2) {
            throw new UDFArgumentLengthException("Encrypt only takes 2 arguments: T, String");
        }

        ObjectInspector keyObject = arguments[1];
        ObjectInspector colObject = arguments[0];

        if (!(keyObject instanceof StringObjectInspector)) {
            throw new UDFArgumentException("Error: Key Type is Not String");
        }

        this.key = (StringObjectInspector) keyObject;
        this.col = (StringObjectInspector) colObject;

        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] deferredObjects) throws HiveException {
        String keyString = key.getPrimitiveJavaObject(deferredObjects[1].get());
        String colString = col.getPrimitiveJavaObject(deferredObjects[0].get());
        return Util.AESEncrypt(colString, keyString);
    }

    @Override
    public String getDisplayString(String[] strings) {
        return null;
    }

}
