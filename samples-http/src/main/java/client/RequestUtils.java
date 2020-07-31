package client;

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
 */
public class RequestUtils {

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
        RequestUtils requestUtils = new RequestUtils();
        String url = "http://127.0.0.1:10001/executeRule";
        String json = "{\"id\": \"9\", \"taskid\": \"32\", \"key\": \"57f5458aA@#!9f8e\", \"rule\": \"24&4&7&2&1&3&21&16&22&17&5&15\"}";
        System.out.println(requestUtils.post(json, url));
    }


}
