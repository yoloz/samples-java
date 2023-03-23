package indi.yolo.sample.hive;

import javax.security.sasl.AuthenticationException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.service.auth.PasswdAuthenticationProvider;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;


/**
 * HiverServer2 的: hive.server2.authentication=CUSTOM
 *
 * @author yoloz
 */
public class CustomPasswdAuthenticator implements PasswdAuthenticationProvider {

    private static final String HIVE_JDBC_PASSWD_AUTH_PREFIX = "hive.jdbc_passwd.auth.%s";

    @Override
    public void Authenticate(String username, String password) throws AuthenticationException {
        HiveConf hiveConf = new HiveConf();
        Configuration conf = new Configuration(hiveConf);
        String passwdMD5 = conf.get(String.format(HIVE_JDBC_PASSWD_AUTH_PREFIX, username));
        if (passwdMD5 == null) {
            String message = "user's ACL configuration is not found. user:" + username;
            throw new AuthenticationException(message);
        }
        if (!passwdMD5.equals(password)) {
            String message = "user name and password is mismatch. user:" + username;
            throw new AuthenticationException(message);
        }
    }

//    @Override
//    public void Authenticate(String username, String password) throws AuthenticationException {
//        boolean ok = false;
//        HiveConf hiveConf = new HiveConf();
//        Configuration conf = new Configuration(hiveConf);
//        String filePath = conf.get("hive.server2.custom.authentication.file");
//        File file = new File(filePath);
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String tempString;
//            while ((tempString = reader.readLine()) != null) {
//                String[] datas = tempString.split(",", -1);
//                if (datas.length != 2) continue;
//                if (datas[0].equals(username) && datas[1].equals(password)) {
//                    ok = true;
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            throw new AuthenticationException("read auth config file error, [" + filePath + "] ..", e);
//        }
//        if (!ok) {
//            throw new AuthenticationException("user [" + username + "] auth check fail .. ");
//        }
//    }

}
