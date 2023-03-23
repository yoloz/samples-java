#!/usr/bin/env bash
#Step 1  Generate SSL key and certificate for each Kafka broker
keytool -genkeypair -storetype pkcs12 -keystore 1-183.p12 -alias 1-183-server -validity 365 -keyalg RSA
#Step 2  Creating your own CA
openssl req -new -x509 -keyout ca.key -out ca.crt -days 365
keytool -importcert -file ca.crt -alias CARoot -keystore 1-183-truststore.p12 -storetype pkcs12
keytool -importcert -file ca.crt -alias CARoot -keystore user1-truststore.p12 -storetype pkcs12
#Step 3  Signing the certificate
keytool -certreq -file 1-183.csr -storetype pkcs12 -keystore 1-183.p12 -alias 1-183-server
openssl x509 -req -CA ca.crt -CAkey ca.key -in 1-183.csr -out 1-183.crt -days 365 -CAcreateserial -extfile {SAN.conf}
keytool -importcert -file ca.crt -storetype pkcs12 -keystore 1-183.p12 -alias CARoot
keytool -importcert -file 1-183.crt -storetype pkcs12 -keystore 1-183.p12 -alias 1-183-server
######### If client authentication is required #####
keytool -genkeypair -storetype pkcs12 -keystore user1.p12 -alias user1 -validity 365 -keyalg RSA
keytool -certreq -file user1.csr -storetype pkcs12 -keystore user1.p12 -alias user1
openssl x509 -req -CA ca.crt -CAkey ca.key -in user1.csr -out user1.crt -days 365 -CAcreateserial
keytool -importcert -file ca.crt -storetype pkcs12 -keystore user1.p12 -alias CARoot
keytool -importcert -file user1.crt -storetype pkcs12 -keystore user1.p12 -alias user1
#用户证书导入服务端信任库
keytool -importcert -alias user1trust -file user1.crt -keystore 1-183-truststore.p12
