package com.yoloz.sample.ssl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

/**
 * SSL Server
 *
 * <ul>
 * <li>1)生成服务端私钥</li>
 * <li>keytool -genkeypair -alias serverkey -keystore kserver.keystore</li>
 * <li>2)根据私钥,到处服务端证书</li>
 * <li>keytool -exportcert -alias serverkey -keystore kserver.keystore -file server.crt</li>
 * <li>3)把证书加入到客户端受信任的keystore中</li>
 * <li>keytool -importcert -alias serverkey -file server.crt -keystore tclient.keystore</li>
 * </ul>
 */
public class SSLServer {

    private static final int DEFAULT_PORT = 7777;

    private static final String SERVER_KEY_STORE_PASSWORD = "123456";
    private static final String SERVER_TRUST_KEY_STORE_PASSWORD = "123456";

    private SSLServerSocket serverSocket;

    /**
     * 启动程序
     */
    public static void main(String[] args) {
//        Properties properties = System.getProperties();
//        properties.put("javax.net.debug", "ssl,handshake");
//        System.setProperties(properties);
        SSLServer server = new SSLServer();
        server.init();
        server.start();
    }

    /**
     * <ul>
     * <li>听SSL Server Socket</li>
     * <li> 由于该程序不是演示Socket监听，所以简单采用单线程形式，并且仅仅接受客户端的消息，并且返回客户端指定消息</li>
     * </ul>
     */
    public void start() {
        if (serverSocket == null) {
            System.out.println("ERROR");
            return;
        }
        while (true) {
            try {
                Socket s = serverSocket.accept();
                InputStream input = s.getInputStream();
                OutputStream output = s.getOutputStream();

                BufferedInputStream bis = new BufferedInputStream(input);
                BufferedOutputStream bos = new BufferedOutputStream(output);

                byte[] buffer = new byte[20];
                bis.read(buffer);
                System.out.println(new String(buffer));

                bos.write("Server Echo".getBytes());
                bos.flush();

                s.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * <ul>
     * <li>ssl连接的重点:</li>
     * <li>初始化SSLServerSocket</li>
     * <li>导入服务端私钥KeyStore，导入服务端受信任的KeyStore(客户端的证书)</li>
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
//            ks.load(new FileInputStream(path.resolve("keytool/kserver.keystore").toFile()), SERVER_KEY_STORE_PASSWORD.toCharArray());
//            tks.load(new FileInputStream(path.resolve("keytool/tserver.keystore").toFile()), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

            KeyStore ks = KeyStore.getInstance("pkcs12");
            KeyStore tks = KeyStore.getInstance("pkcs12");
            ks.load(new FileInputStream(path.resolve("openssl/server.p12").toFile()), SERVER_KEY_STORE_PASSWORD.toCharArray());
            tks.load(new FileInputStream(path.resolve("openssl/root.p12").toFile()), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

            kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
            tmf.init(tks);

            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            serverSocket = (SSLServerSocket) ctx.getServerSocketFactory().createServerSocket(DEFAULT_PORT);
            serverSocket.setUseClientMode(false);
            serverSocket.setNeedClientAuth(true);  //需要验证客户端的身份
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
