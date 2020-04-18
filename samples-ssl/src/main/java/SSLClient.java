import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * sslClient
 * <ul>
 * <li>1)生成客户端私钥</li>
 * <li>keytool -genkey -alias clientkey -keystore kclient.keystore</li>
 * <li>2)根据私钥,导出客户端证书</li>
 * <li>keytool -export -alias clientkey -keystore kclient.keystore -file client.crt</li>
 * <li>3)把证书加入到客户端受信任的keystore中</li>
 * <li>keytool -import -alias clientkey -file client.crt -keystore tserver.keystore</li>
 * </ul>
 */
public class SSLClient {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 7777;
    private static final String CLIENT_KEY_STORE_PASSWORD = "123456";
    private static final String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";

    private SSLSocket sslSocket;

    /**
     * 启动客户端程序
     */
    public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        properties.put("javax.net.debug", "ssl,handshake");
//        System.setProperties(properties);
        SSLClient client = new SSLClient();
        client.init();
        client.process();
    }

    /**
     * 通过ssl socket与服务端进行连接,并且发送一个消息
     */
    public void process() {
        if (sslSocket == null) {
            System.out.println("ERROR");
            return;
        }
        try {
            InputStream input = sslSocket.getInputStream();
            OutputStream output = sslSocket.getOutputStream();

            BufferedInputStream bis = new BufferedInputStream(input);
            BufferedOutputStream bos = new BufferedOutputStream(output);

            bos.write("Client Message".getBytes());
            bos.flush();

            byte[] buffer = new byte[20];
            bis.read(buffer);
            System.out.println(new String(buffer));

            sslSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <ul>
     * <li>ssl连接的重点:</li>
     * <li>初始化SSLSocket</li>
     * <li>导入客户端私钥KeyStore，导入客户端受信任的KeyStore(服务端的证书)</li>
     * </ul>
     */
    public void init() {
        Path path = Paths.get(System.getProperty("user.dir"), "samples-ssl/src/main/resources");
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");

//            KeyStore ks = KeyStore.getInstance("JKS");
//            KeyStore tks = KeyStore.getInstance("JKS");
//            ks.load(new FileInputStream(path.resolve("keytool/kclient.keystore").toFile()), CLIENT_KEY_STORE_PASSWORD.toCharArray());
//            tks.load(new FileInputStream(path.resolve("keytool/tclient.keystore").toFile()), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

            KeyStore ks = KeyStore.getInstance("pkcs12");
            KeyStore tks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(path.resolve("openssl/client.p12").toFile()), CLIENT_KEY_STORE_PASSWORD.toCharArray());
            tks.load(new FileInputStream(path.resolve("openssl/root.p12").toFile()), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());

            kmf.init(ks, CLIENT_KEY_STORE_PASSWORD.toCharArray());
            tmf.init(tks);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            sslSocket = (SSLSocket) ctx.getSocketFactory().createSocket(DEFAULT_HOST, DEFAULT_PORT);
            /*配置套接字在握手时使用客户机（或服务器）模式。
             此方法必须在发生任何握手之前调用。一旦握手开始，在此套接字的生存期内将不能再重置模式。
             服务器通常会验证本身，客户机则不要求这么做
             如果套接字应该以“客户机”模式开始它的握手，此参数为 true
             */
            sslSocket.setUseClientMode(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
