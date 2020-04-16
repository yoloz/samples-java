
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.sources.CreatableRelationProvider;
import org.apache.spark.sql.sources.Filter;
import org.apache.spark.sql.sources.PrunedFilteredScan;
import org.apache.spark.sql.sources.RelationProvider;
import org.apache.spark.sql.sources.SchemaRelationProvider;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.immutable.Map;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Spark-sql读取本地txt文件,即创建表(对应已经存在的文件结构),然后读取表即可读取文件中的内容
 * <p>
 * 更多功能丰富可以参考官网
 * http://spark.apache.org/docs/latest/sql-programming-guide.html#sql
 * <p>
 *
 */
public class TextSources implements RelationProvider, SchemaRelationProvider, CreatableRelationProvider {

    private final String defaultP = "/tmp/external_sql/testSql";
    private final String schema_suffix = "schema";
    private final String data_suffix = "data";

    public BaseRelation createRelation(SQLContext sqlContext, Map<String, String> parameters) {
        return null;
    }

    public BaseRelation createRelation(SQLContext sqlContext, Map<String, String> parameters, StructType schema) {
        String path = defaultP;
        if (parameters.contains("path")) {
            path = parameters.get("path").get();
        }
        Path tableSchema = Paths.get(path, schema_suffix);
        if (Files.notExists(tableSchema)) {
            try {
                Files.createDirectories(Paths.get(path));
                StringBuilder buf = new StringBuilder();
                for (String s : schema.fieldNames()) {
                    buf.append(s).append(",");
                }
                Files.write(tableSchema,
                        buf.substring(0, buf.length() - 1).getBytes("UTF-8"),
                        StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("first relation=============");
        return new TextRelation(sqlContext, path, schema);
    }

    public BaseRelation createRelation(SQLContext sqlContext, SaveMode mode, Map<String, String> parameters, Dataset<Row> data) {
        return null;
    }

    private class TextRelation extends BaseRelation implements PrunedFilteredScan {

        private SQLContext sqlContext;
        private String path;
        private StructType[] userSchema;

        TextRelation(SQLContext sqlContext, String path, StructType... userSchema) {
            this.sqlContext = sqlContext;
            this.path = path;
            this.userSchema = userSchema;
        }

        @Override
        public RDD<Row> buildScan(String[] requiredColumns, Filter[] filters) {
            if (Files.exists(Paths.get(path, data_suffix))) {
                JavaRDD<String> stringJavaRDD = sqlContext.sparkContext()
                        .textFile(Paths.get(path, data_suffix).toString(), 1)
                        .toJavaRDD();
                JavaRDD<Row> rowRDD = stringJavaRDD.map((Function<String, Row>) record -> {
                    String[] attributes = record.split(",");
                    return RowFactory.create(attributes[0], attributes[1].trim());
                });
                return JavaRDD.toRDD(rowRDD);
            }
            return null;
        }

        @Override
        public SQLContext sqlContext() {
            return sqlContext;
        }

        @Override
        public StructType schema() {
            if (userSchema != null && userSchema.length > 0) {
                System.out.println(userSchema[0]);
                return userSchema[0];
            } else {
                try {
                    String schema_ = Files
                            .readAllLines(Paths.get(path, schema_suffix), Charset.forName("UTF-8")).get(0);
                    List<StructField> fields = new ArrayList<>();
                    for (String fieldName : schema_.split(",")) {
                        StructField field = DataTypes.
                                createStructField(fieldName, DataTypes.StringType, true);
                        fields.add(field);
                    }
                    return DataTypes.createStructType(fields);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}

