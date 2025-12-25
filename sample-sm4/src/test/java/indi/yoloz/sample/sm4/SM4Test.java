package indi.yoloz.sample.sm4;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SM4Test {


    @Test
    public void testPattern() {
        Pattern p = Pattern.compile("^[0-9A-Za-z]+$");
        assertTrue(p.matcher("64EC7C763AB7BF64E2D75FF83A319918").matches());
        assertTrue(p.matcher("JeF8U9wHFOMfs2Y8").matches());
        assertFalse(p.matcher("JeF8U9wHFOMfs2Y8!").matches());
    }

    @Test
    public void processOne() throws Exception {

        boolean isPadding = true; //对于长度不够16*n的,false会造成解密多出不适别的字符

        //长度在16,32,>32三者
        String key = "JeF8U9wHFOMfs2Y8";  //16
        String iv = "JeF8U9wHFOMfs2Y8";
        key = "64EC7C763AB7BF64E2D75FF83A319918";  //32
        iv = "64EC7C763AB7BF64E2D75FF83A319918";
        key = "64EC7C763AB7BF64E2D75FF83A3199182457"; //36
        iv = "64EC7C763AB7BF64E2D75FF83A3199182457aszx2"; //41

        String text = "Hello Word How aHello Word How aHello Word How a"; //length is 16*4
        text = "杭州市滨江区滨安路1180号"; //length is 34
        String s = byteToHex(text.getBytes(StandardCharsets.UTF_8));
        System.out.println("原文Hex:" + s);
        System.out.println("原文length:" + text.getBytes(StandardCharsets.UTF_8).length);

        SM4 sm4 = new SM4(key, iv, SM4Impl.ENCRYPT, isPadding);
        System.out.println("ECB模式加密");
        Method encrypt_ecb = sm4.getClass().getDeclaredMethod("encrypt_ecb", String.class);
        encrypt_ecb.setAccessible(true);
        String ecb = (String) encrypt_ecb.invoke(sm4, text);
        System.out.println("密文:" + ecb);
        System.out.println("CBC模式加密");
        Method encrypt_cbc = sm4.getClass().getDeclaredMethod("encrypt_cbc", String.class);
        encrypt_cbc.setAccessible(true);
        String cbc = (String) encrypt_cbc.invoke(sm4, text);
        System.out.println("密文: " + cbc);

        sm4 = new SM4(key, iv, SM4Impl.DECRYPT, isPadding);
        System.out.println("ECB模式解密");
        Method decrypt_ecb = sm4.getClass().getDeclaredMethod("decrypt_ecb", String.class);
        decrypt_ecb.setAccessible(true);
        System.out.println("解密：" + decrypt_ecb.invoke(sm4, ecb));
        System.out.println("CBC模式解密");
        Method decrypt_cbc = sm4.getClass().getDeclaredMethod("decrypt_cbc", String.class);
        decrypt_cbc.setAccessible(true);
        System.out.println("解密：" + decrypt_cbc.invoke(sm4, cbc));

    }

    @Test
    public void processBatch() throws Exception {
        boolean isPadding = true;
        int count = 100;
        String key = "JeF8U9wHFOMfs2Y8";
        String iv = "JeF8U9wHFOMfs2Y8";
        SM4 sm4 = new SM4(key, iv, SM4Impl.ENCRYPT, isPadding);
        List<String> src = new ArrayList<>(count);
        List<String> ecb = new ArrayList<>(count);
        List<String> cbc = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0) src.add("I Love You Every Day_" + i);
            else src.add("杭州市滨江区滨安路1180号_" + i);
        }
        Method encrypt_ecb = sm4.getClass().getDeclaredMethod("encrypt_ecb", String.class);
        encrypt_ecb.setAccessible(true);
        Method encrypt_cbc = sm4.getClass().getDeclaredMethod("encrypt_cbc", String.class);
        encrypt_cbc.setAccessible(true);
        for (String s : src) {
            ecb.add((String) encrypt_ecb.invoke(sm4, s));
            cbc.add((String) encrypt_cbc.invoke(sm4, s));
        }

        sm4 = new SM4(key, iv, SM4Impl.DECRYPT, isPadding);
        Method decrypt_ecb = sm4.getClass().getDeclaredMethod("decrypt_ecb", String.class);
        decrypt_ecb.setAccessible(true);
        Method decrypt_cbc = sm4.getClass().getDeclaredMethod("decrypt_cbc", String.class);
        decrypt_cbc.setAccessible(true);
        for (int j = 0; j < count; j++) {
            System.out.println("ECB[" + ecb.get(j) + "]=>：" + decrypt_ecb.invoke(sm4, ecb.get(j)));
            System.out.println("CBC[" + cbc.get(j) + "]=>：" + decrypt_cbc.invoke(sm4, cbc.get(j)));
        }
    }

    private String byteToHex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte b1 : b) {
            stmp = Integer.toHexString(b1 & 0xff);
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toLowerCase();
    }
}