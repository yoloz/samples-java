package client.restful;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 *
 */
public class CommonOperations {

    private static void put(RestClient restClient) throws IOException {
        Response response = restClient.performRequest("GET", "/",
                Collections.singletonMap("pretty", "true"));
        System.out.println(EntityUtils.toString(response.getEntity()));

        //index a document
        HttpEntity entity = new NStringEntity(
                "{\n" +
                        "    \"user\" : \"kimchy\",\n" +
                        "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                        "    \"message\" : \"trying out Elasticsearch\"\n" +
                        "}", ContentType.APPLICATION_JSON);

        Response indexResponse = restClient.performRequest(
                "PUT",
                "/twitter/tweet/1",
                Collections.<String, String>emptyMap(),
                entity);
        System.out.println(indexResponse.toString());
    }

    public static void main(String[] args) {
        try (RestClient restClient = RestClient.builder(
                new HttpHost("10.68.23.186", 9200, "http"),
                new HttpHost("10.68.23.187", 9200, "http"),
                new HttpHost("10.68.23.188", 9200, "http"))
                .build()) {
            put(restClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
