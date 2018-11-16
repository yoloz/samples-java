
import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import java.io.IOException;
//import java.net.URI;


public class SequenceFileWriteDemo {

    private static String[] myValue = {"hello world", "bye world", "hello hadoop", "bye hadoop"};

    public static void main(String[] args) throws IOException {

        String uri = "hdfs://cloudera135:9100/sequenceFileDemo";
        Configuration configuration = new Configuration();
//        FileSystem fs = FileSystem.get(URI.create(uri), configuration);
//        Path path = new Path(uri);
        IntWritable key = new IntWritable();
        Text value = new Text();
        SequenceFile.Writer writer = null;
        try {
            writer = SequenceFile.createWriter(configuration,
                    SequenceFile.Writer.file(new Path(uri)),
                    SequenceFile.Writer.keyClass(key.getClass()),
                    SequenceFile.Writer.valueClass(value.getClass())
            );
//            writer = SequenceFile.createWriter(fs, configuration, path, key.getClass(), value.getClass());
            for (int i = 0; i < 500000; i++) {
                key.set(500000 - i);
                value.set(myValue[i % myValue.length]);
                writer.append(key, value);
            }
        } finally {
            IOUtils.closeStream(writer);
        }

    }
}
