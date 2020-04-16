package client.restful;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 获取所有index信息含有状态,健康度,存储大小等(health,status,size .etc)
 * Response indices = restClient.performRequest(Get, "/_cat/indices?v")
 * 返回如下：
 * health status index               uuid                   pri rep docs.count docs.deleted store.size pri.store.size
 * green  open   customer            Ll7xgoCgS_qTEzvIBsXIng   5   1          2            0     14.6kb          7.3kb
 * <p>
 * 获取所有index和其中的aliases,下文的例子使用这种方式
 * Response indices = restClient.performRequest(Get, "/_all/_aliases")
 * 返回如下：
 * {"my_index2":{"aliases":{}},"employs":{"aliases":{}},"flow_i":{"aliases":{"流数据":{}}}}
 * <p>
 * mapping简单样例如下：
 * <p>
 * my_index
 * {
 * "mappings": {
 * "user": {
 * "_all":       { "enabled": false  },
 * "properties": {
 * "title":    {"type": "text","fields": {"english": {"type":"text","analyzer":"english"}}
 * },
 * "name":     { "type": "text"  },
 * "age":      { "type": "integer" }
 * }
 * },
 * "blogpost": {
 * "_all":       { "enabled": false  },
 * "properties": {
 * "title":    { "type": "text"  },
 * "body":     { "type": "text"  },
 * "user_id":  {
 * "type":   "keyword"
 * },
 * "created":  {
 * "type":   "date",
 * "format": "strict_date_optional_time||epoch_millis"
 * }
 * }
 * }
 * }
 * }
 */
public class IndexInfoDemo {

    private final static String GAP = ":";
    private final static String Get = "GET";
    private final static String mappings = "mappings";
    private final static String PROPERTIES = "properties";
    private final static String TYPE = "type";


    public static void main(String[] args) throws IOException {
        try (RestClient restClient = RestClient.builder(
                new HttpHost("10.68.23.186", 9200, "http"),
                new HttpHost("10.68.23.187", 9200, "http"),
                new HttpHost("10.68.23.188", 9200, "http"))
                .build()) {

            List<String> indexes = Lists.newArrayList();
            Map<String, Map<String, String>> allInfo = Maps.newHashMap();

            //获取所有index信息
            Response indices = restClient.performRequest(Get, "/_all/_aliases");
            //获取具体index的mapping信息
            String indicesStr = EntityUtils.toString(indices.getEntity());
            Map<String, Object> indexMap = new Gson().fromJson(indicesStr,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
            indexMap.forEach((k, v) -> {
                indexes.add(k);

                try {
                    Response mapping = restClient.performRequest(Get, "/" + k + "/_mapping");
                    JsonObject index = new JsonParser()
                            .parse(EntityUtils.toString(mapping.getEntity()))
                            .getAsJsonObject();
                    JsonObject types = index.getAsJsonObject(k).getAsJsonObject(mappings);
                    types.entrySet().forEach(type -> {
                        String name = k + GAP + type.getKey();
                        allInfo.put(name, Maps.newHashMap());
                        JsonObject properties = type.getValue().getAsJsonObject().getAsJsonObject(PROPERTIES);
                        properties.entrySet().forEach(prop -> {
                            Map<String, String> cols = allInfo.get(name);
                            if (!prop.getValue().getAsJsonObject().has(TYPE)) {
                                //todo 自定义对象类型等直接返回value
                                cols.put(prop.getKey(),
                                        prop.getValue().getAsJsonObject().toString());
                            } else {
                                cols.put(prop.getKey(),
                                        prop.getValue().getAsJsonObject().get(TYPE).getAsString());
                            }
                        });

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println(new Gson().toJson(indexes, new TypeToken<List<String>>() {
            }.getType()));

            System.out.println(new Gson().toJson(allInfo, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType()));
        }
    }
}
