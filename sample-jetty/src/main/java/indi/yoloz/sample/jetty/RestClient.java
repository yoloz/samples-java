package indi.yoloz.sample.jetty;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestClient implements Closeable {
    private final CloseableHttpClient httpClient;

    public RestClient() {
        this.httpClient = HttpClients.createDefault();
    }

    public void get(String url) {
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            showResponse(entity);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void post(String url, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, v)));
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            showResponse(entity);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ignore) {
                }
            }
        }
    }


    private void showResponse(HttpEntity entity) {
        if (entity != null) {
            try (InputStream is = entity.getContent()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                System.out.println(sb.toString());
            } catch (UnsupportedOperationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.httpClient.close();
    }
}
