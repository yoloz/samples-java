package com.yoloz.sample.kafka;/*
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

import com.yoloz.sample.kafka.impl.ConsumerTest;
import com.yoloz.sample.kafka.impl.KafkaProperties;
import com.yoloz.sample.kafka.impl.ProducerTest;

import java.util.Arrays;

/**
 * javax.net.ssl.SSLHandshakeException: No subject alternative names present:
 * <p>
 * http://kafka.apache.org/documentation/#security_confighostname
 * Server host name verification disabled: broker and client setting ssl.endpoint.identification.algorithm to an empty string
 * <p>
 * http://kafka.apache.org/documentation/#security_configcerthstname
 * If host name verification is enabled, clients will verify the server's fully qualified domain name (FQDN) against one of the following two fields:
 * 1,Common Name (CN)
 * 2,Subject Alternative Name (SAN)
 * 仅仅CN(一般broker的hostname)则broker listener不可配置具体的ip监听同时client的连接地址要写hostname:port如此客户端要配置/etc/hosts
 * 使用SAN则就方便很多，broker listener监听ip:port，客户端直接ip:port访问
 * <p>
 * <p>
 * java.net.UnknownHostException: zhds-nidf: Temporary failure in name resolution
 * broker listener中配置具体ip监听或者客户端hosts添加broker的hostname
 */
public class SslTest {

    private void test(String... args) {
        if (args == null || args.length < 3) {
            System.out.println("param[" + Arrays.toString(args) + "] error");
            System.exit(1);
        }
        String host = args[0];
        String topic = args[1];
        int index = 2;
        String flag = null;
        if ("producer".equalsIgnoreCase(args[index])) {
            flag = "producer";
            index += 1;
        } else if ("consumer".equalsIgnoreCase(args[index])) {
            flag = "consumer";
            index += 1;
        }
        StringBuilder msg = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            msg.append(args[i]);
            if (i != args.length - 1) msg.append(" ");
        }
        if (flag == null || "producer".equals(flag)) {
            ProducerTest producerTest = new ProducerTest(KafkaProperties.ssl(true), host, topic);
            producerTest.write(msg.toString());
        }
        if (flag == null || "consumer".equals(flag)) {
            ConsumerTest consumerTest = new ConsumerTest(KafkaProperties.ssl(true), host, topic);
            consumerTest.read();
        }
    }

    public static void main(String[] args) {
//        PropertyConfigurator.configure(SslTest.class.getResourceAsStream("/log4j.properties"));
        SslTest clientTest = new SslTest();
//        clientTest.test("192.168.1.183:9092", "test", "ssl test without client auth"); //先写入后读取
//        clientTest.test("192.168.1.183:9092", "test", "producer", "ssl test without client auth"); //写入消息
        clientTest.test("192.168.1.183:9092", "test", "consumer"); //读取消息
    }
}
