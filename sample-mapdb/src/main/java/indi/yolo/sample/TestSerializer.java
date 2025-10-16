package indi.yolo.sample;

import org.jetbrains.annotations.NotNull;
import org.mapdb.DBException;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author yolo
 */
public class TestSerializer<T extends TestBean> implements Serializer<T> {

    @Override
    public void serialize(@NotNull DataOutput2 out, @NotNull T value) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(value);
        objectOutputStream.flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(@NotNull DataInput2 input, int available) throws IOException {
        DataInput2.DataInputToStream input1 = new DataInput2.DataInputToStream(input);
        ObjectInputStream objectInputStream = new ObjectInputStream(input1);
        try {
            Object obj = objectInputStream.readObject();
            return (T) obj;
        } catch (ClassNotFoundException e) {
            // will not happen
            throw new DBException.SerializationError(e);
        }
    }
}
