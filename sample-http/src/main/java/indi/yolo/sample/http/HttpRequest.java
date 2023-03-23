package indi.yolo.sample.http;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * http://hc.apache.org/httpcomponents-client-ga/quickstart.html
 * <p>
 * // The fluent API relieves the user from having to deal with manual deallocation of system
 * // resources at the cost of having to buffer response content in memory in some cases.
 * <p>
 * Request.Get("http://targethost/homepage").execute().returnContent();
 * <p>
 * Request.Post("http://targethost/login").bodyForm(Form.form().add("username",  "vip").add("password",  "secret").build()).execute().returnContent();
 * ×
 *
 * @author yoloz
 */
public class HttpRequest {

    public String post(String json, String url) {
        String result = "";
        HttpPost post = new HttpPost(url);
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            post.setHeader("Content-Type", "application/json;charset=utf-8");
            StringEntity postingString = new StringEntity(json, StandardCharsets.UTF_8);
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                result = "服务器异常";
            } else {
                try (InputStream in = response.getEntity().getContent();
                     BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        builder.append(line).append('\n');
                    }
                    result = builder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.abort();
        }
        return result;
    }

    public static void main(String[] args) {
        HttpRequest httpRequest = new HttpRequest();
//        String url = "http://127.0.0.1:10001/executeRule";
//        String json = "{\"id\": \"1\", \"taskid\": \"1\", \"key\": \"7d7c0c71A#@!3065\", \"rule\": \"12&13&5&3&15&1&6&2&8&7&10&4&17&11&16&9&14\", \"limit\": 81, \"matchlow\": 0.5, \"matchbig\": 0.9}";
//        System.out.println(httpClient.post(json, url));

        String url = "http://127.0.0.1:8080/createMeta";
        String json = "{\"id\": \"1\", \"key\": \"7d7c0c71A#@!3065\"}";
        System.out.println(httpRequest.post(json, url));
    }

}
