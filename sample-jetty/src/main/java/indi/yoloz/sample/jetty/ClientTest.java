package indi.yoloz.sample.jetty;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientTest {

    public static void main(String[] args) {
        try (RestClient restClient = new RestClient()) {
            restClient.get("http://localhost:7912");
            Map<String, String> params = new HashMap<>();
            params.put("username", "test");
            params.put("passwd", "test");
            restClient.post("http://localhost:7912", params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
