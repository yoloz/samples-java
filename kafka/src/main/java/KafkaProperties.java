/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.kafka.common.config.SslConfigs;
import java.util.Properties;

public class KafkaProperties {
    public static final String TOPIC = "topic1";
    public static final String KAFKA_SERVER_URL = "localhost";
    public static final int KAFKA_SERVER_PORT = 9092;
    public static final int KAFKA_PRODUCER_BUFFER_SIZE = 64 * 1024;
    public static final int CONNECTION_TIMEOUT = 100000;
    public static final String TOPIC2 = "topic2";
    public static final String TOPIC3 = "topic3";
    public static final String CLIENT_ID = "SimpleConsumerDemoClient";

    private KafkaProperties() {}

    /**
     * 获取普通kafka集群消费者使用的配置
     */
    static Properties getPLAINproperties() {
        Properties props = new Properties();
        props.put("security.protocol", "PLAINTEXT");//通信协议
        return props;
    }

    /**
     * 获取配置了SSL的kafka集群消费者使用的配置
     */
    public static Properties getSSLproperties() {
        Properties props = new Properties();
        props.put("security.protocol", "SSL");//通信协议
        //证书信任列表文件
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "resources/kafka.client.truststore.jks");
        //信任文件密码
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "unimas");
        //客户端密钥文件,这个文件是在配置双向认证的时候需要的,服务器需要认可客户端,
        //需要客户端创建密钥对,然后用公共的ca（kafka集群认可的）签名,使用这个与服务端通信
        props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "resources/kafka.client.keystore.jks");
        //密钥文件的密码
        props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "unimas");
        //密钥的密码
        props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "unimas");
        return props;
    }

    /**
     * 获取配置了SASL的kafka集群消费者使用的配置
     */
    public static Properties getSASLproperties() {
        Properties props = new Properties();
        //设置系统属性,用于加载的kdc服务器的相关信息的配置
        System.setProperty("java.security.krb5.conf", "resources/krb5.conf");
        //设置系统属性,用于sasl用户认证的客户端配置文件,包含了keytab和Principal的信息
        System.setProperty("java.security.auth.login.config", "resources/kafka_client_jaas.conf");
        //设置sasl服务的名称,这个是服务器配置的,客户端要与其一致
        props.setProperty("sasl.kerberos.service.name", "kafka");
        //通信协议,PLAINTEXTB表示是空白即无加密,SASL是简单认证与安全层（Simple Authentication and Security Layer）
        //通过kerberos实现
        props.put("security.protocol", "SASL_PLAINTEXT");
        return props;
    }

    /**
     * 获取配置了SASL+SSL的kafka集群消费者使用的配置
     */
    public static Properties getSASL_SSLproperties() {
        Properties props = new Properties();
        //设置系统属性,用于加载的kdc服务器的相关信息的配置
        System.setProperty("java.security.krb5.conf", "resources/krb5.conf");
        //设置系统属性,用于sasl用户认证的客户端配置文件,包含了keytab和Principal的信息
        System.setProperty("java.security.auth.login.config", "resources/kafka_client_jaas.conf");
        //设置sasl服务的名称,这个是服务器配置的,客户端要与其一致
        props.setProperty("sasl.kerberos.service.name", "kafka");
        //通信协议
        props.put("security.protocol", "SASL_SSL");

        //证书信任列表文件
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "resources/kafka.client.truststore.jks");
        //信任文件密码
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "unimas");
        //客户端密钥文件,这个文件是在配置双向认证的时候需要的,服务器需要认可客户端,
        //需要客户端创建密钥对,然后用公共的ca（kafka集群认可的）签名,使用这个与服务端通信
        props.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "resources/kafka.client.keystore.jks");
        //密钥文件的密码
        props.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "unimas");
        //密钥的密码
        props.setProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "unimas");
        return props;
    }
}
