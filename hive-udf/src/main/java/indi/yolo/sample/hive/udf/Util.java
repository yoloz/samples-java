package indi.yolo.sample.hive.udf;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author yolo
 */

public class Util {

    private static byte[] ecb(byte[] content, String en_key, int mode) {
        try {
            byte[] _content = content;
            if (Cipher.DECRYPT_MODE == mode) _content = HexBin.decode(new String(content, StandardCharsets.UTF_8));
            if (_content == null) return content;
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(en_key.getBytes(StandardCharsets.UTF_8));
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, key);
            byte[] bytes = cipher.doFinal(_content);
            if (Cipher.ENCRYPT_MODE == mode) bytes = HexBin.encode(bytes).getBytes(StandardCharsets.UTF_8);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static Object AESDecrypt(Object obj, Object key) {  //ecb
        return new String(ecb(String.valueOf(obj).getBytes(StandardCharsets.UTF_8), String.valueOf(key),
                Cipher.DECRYPT_MODE), StandardCharsets.UTF_8);
    }

    public static Object AESEncrypt(Object obj, Object key) {  //ecb
        return new String(ecb(String.valueOf(obj).getBytes(StandardCharsets.UTF_8), String.valueOf(key),
                Cipher.ENCRYPT_MODE), StandardCharsets.UTF_8);
    }


}
