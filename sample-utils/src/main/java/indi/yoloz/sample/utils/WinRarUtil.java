package indi.yoloz.sample.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 由于网盘中个人的压缩加密文件密码遗失(时间规则存在)
 * 故编写此工具遍历时间解除密码
 * <p>
 * on 17-5-8.
 */
public class WinRarUtil {

    private static final String rarExe = "C:\\Program Files\\WinRAR\\WinRAR.exe";

    /**
     * 解压简单的rar文件
     *
     * @param rarFile rar文件
     * @return 是否成功
     */
    static boolean unRar(String rarFile) {
        boolean bool = false;
        Path file = Paths.get(rarFile);
        if (Files.notExists(file)) {
            return false;
        }
        String fileName = file.getFileName().toString();
        String dir = fileName.substring(0, fileName.lastIndexOf("."));
        try {
            String target = file.getParent() + File.separator + dir;
            if (Files.notExists(Paths.get(target))) {
                Files.createDirectories(Paths.get(target));
            }
            String cmd = rarExe + " e -y " + rarFile + " " + target;
            Process proc = Runtime.getRuntime().exec(cmd);
            if (proc.waitFor() != 0) {
                if (proc.exitValue() == 0) {
                    bool = false;
                }
            } else {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    /**
     * 解压含有密码的rar文件
     *
     * @param rarFile  rar文件
     * @param password 密码
     * @return 是否解压成功
     */
    static boolean unRar(String rarFile, String password) {
        boolean bool = false;
        Path file = Paths.get(rarFile);
        if (Files.notExists(file)) {
            return false;
        }
        String fileName = file.getFileName().toString();
        String dir = fileName.substring(0, fileName.lastIndexOf("."));
        try {
            String target = file.getParent() + File.separator + dir;
            if (Files.notExists(Paths.get(target))) {
                Files.createDirectories(Paths.get(target));
            }
            String cmd = rarExe + " e -y " + rarFile + " -p" + password + " " + target;
            Process proc = Runtime.getRuntime().exec(cmd);
            if (proc.waitFor() != 0) {
                if (proc.exitValue() == 0) {
                    bool = false;
                }
            } else {
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Path path = Paths.get("E:\\test");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.rar")) {
            for (Path entry : stream) {
                calendar.set(2016, 0, 1); //从2015年元月开始
                String rarFile = path + File.separator + entry.getFileName();
                for (int i = 1; i < 600; i++) {
                    calendar.add(Calendar.DAY_OF_MONTH, +1);
                    String pwd = simpleDateFormat.format(calendar.getTime());
                    if (WinRarUtil.unRar(rarFile, pwd)) {
                        System.out.println(path + File.separator + entry.getFileName() + "密码：" + pwd);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String zipFile = "E:\\test\\rarpasswordrecoverymagic.rar";
//        boolean b = WinRarUtil.unRar(zipFile, "123456");
//        System.out.println(b);
    }
}
