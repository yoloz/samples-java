package indi.yolo.sample.jdbc;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * CREATE TABLE `test` (
 * `name` varchar(100) DEFAULT NULL,
 * `nickname` varchar(100) DEFAULT NULL,
 * `title` varchar(100) DEFAULT NULL,
 * `phone` varchar(100) DEFAULT NULL,
 * `email` varchar(100) DEFAULT NULL,
 * `sex` varchar(100) DEFAULT NULL,
 * `nation` varchar(100) DEFAULT NULL,
 * `hjd` varchar(100) DEFAULT NULL,
 * `czd` varchar(100) DEFAULT NULL,
 * `csrq` varchar(100) DEFAULT NULL
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 *
 * @author yoloz
 */
public class MysqlInsertData {

    private final Random random = new Random();

    public static void main(String[] args) {
        MysqlInsertData mysqlInsertData = new MysqlInsertData();
        mysqlInsertData.insert();
    }

    public void insert() {
        String sql = "INSERT INTO test_app.test" +
                "(name, nickname, title, phone, email, sex, nation, hjd, czd, csrq)" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String url = "jdbc:mysql://192.168.124.171:3306/test_app";
        String[] titles = new String[]{"团员", "党员", "群众"};
        try (Connection conn = DriverManager.getConnection(url, "test", "test@123");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int counter = 0;
            for (int i = 0; i < 150; i++) {
                int nameSize = random.nextInt(4);
                String name = getChineseCharacters(nameSize <= 1 ? 2 : nameSize);
                int nickSize = random.nextInt(8);
                String nickname = nick(nickSize <= 1 ? 2 : nickSize);
                String title = titles[random.nextInt(3)];
                String phone = phone();
                String email = email();
                String sex = random.nextBoolean() ? "男" : "女";
                String nation = nation();
                String hjd = address();
                String czd = address();
                String csrq = date();
//                System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n", name, nickname, title, phone, email, sex, nation, hjd, czd, csrq);
                stmt.setString(1, name);
                stmt.setString(2, nickname);
                stmt.setString(3, title);
                stmt.setString(4, phone);
                stmt.setString(5, email);
                stmt.setString(6, sex);
                stmt.setString(7, nation);
                stmt.setString(8, hjd);
                stmt.setString(9, czd);
                stmt.setString(10, csrq);
                stmt.addBatch();
                counter++;
                if (counter == 15) {
                    stmt.executeBatch();
                    counter = 0;
                }
            }
            if (counter > 0) {
                stmt.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String date() {
        Random random = new Random();
        int minDay = (int) LocalDate.of(1990, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2010, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        return randomBirthDate.format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public String address() {
        String[] CITY_NAMES = {"杭州市", "郑州市", "石家庄市", "张家口市", "安庆市", "北京市", "上海市"};
        String[] STATE_NAMES = {"浙江省", "河南省", "安徽省", "江苏省", "山东省", "河北省", "江西省"};
        int houseNumber = random.nextInt(1000) + 1;
        String city = CITY_NAMES[random.nextInt(CITY_NAMES.length)];
        String state = STATE_NAMES[random.nextInt(STATE_NAMES.length)];
        int zipCode = random.nextInt(90000) + 10000;
        return houseNumber + " " + city + " " + state + " " + zipCode;
    }

    public String nation() {
        String[] nations = new String[]{"汉族",
                "蒙古族", "回族",
                "藏族", "维吾尔族",
                "苗族", "彝族",
                "壮族", "布依族", "朝鲜族", "满族", "侗族", "瑶族",
                "白族", "土家族",
                "哈尼族",
                "哈萨克族",
                "傣族",
                "黎族",
                "傈僳族",
                "佤族",
                "畲族",
                "高山族",
                "拉祜族",
                "水族",
                "东乡族",
                "纳西族",
                "景颇族",
                "柯尔克孜族",
                "土族",
                "达斡尔族",
                "仫佬族",
                "羌族",
                "布朗族",
                "撒拉族",
                "毛南族",
                "仡佬族",
                "锡伯族",
                "阿昌族",
                "普米族",
                "塔吉克族",
                "怒族",
                "乌孜别克族",
                "俄罗斯族",
                "鄂温克族",
                "德昂族",
                "保安族",
                "裕固族",
                "京族",
                "塔塔尔族",
                "独龙族",
                "鄂伦春族",
                "赫哲族",
                "门巴族",
                "珞巴族",
                "基诺族",
                "其他",
                "外国血统中国籍人士"};
        return nations[random.nextInt(nations.length)];
    }

    public String email() {
        int size = random.nextInt(12);
        if (size <= 3) size = 3;
        return nick(size) + random.nextInt(100) + "@test.com";
    }

    public String phone() {
        Random random = new Random();
        int firstThreeDigits = random.nextInt(900) + 100;
        int lastEightDigits = random.nextInt(90000000) + 10000000;
        return firstThreeDigits + String.valueOf(lastEightDigits);
    }

    public String nick(int size) {
        char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        Random random = new Random(26);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(chars[random.nextInt(chars.length)]);
        }
        return stringBuilder.toString();
    }

//    String generate_name(){
////         随机姓氏
//       String[] first_name = new String[]{"王", "李", "张", "刘", "赵", "蒋", "孟", "陈", "徐", "杨", "沈", "马", "高", "殷", "上官", "钟", "常","钱","孙","周","吴","郑","司马","东皇","东方"};
//    }

    /**
     * 使用GB2312编码生成汉字：
     * <p>
     * GB2312规定对收录的每个字符采用两个字节表示，
     * 第一个字节为“高字节”，对应94个区；第二个字节为“低字节”，对应94个位。
     * 可以理解为一张94行94列的区位表，每一行称为一个“区”，每一列称为一个“位”，
     * 共94个区，每区含有94个位，共8836个码位，这种表示方式称为区位码。
     * <p>
     * 区码范围：
     * 01-09区收录除汉字外的682个特殊字符。
     * 10-15区为空白区，没有使用。
     * 16-55区收录3755个一级汉字，按拼音字母排序。
     * 56-87区收录3008个二级汉字，按部首/笔画排序。
     * 88-94区为空白区，没有使用。
     * <p>
     * 位码范围：区码为55时，位码范围为1-89；其他区码，位码范围为1-94。
     * <p>
     * 区位码范围：0101-9494，区号和位号分别转换成十六进制加上0xA0就是GB2312编码。
     * GB2312编码范围：A1A1-FEFE，其中汉字编码范围：B0A1-F7FE，
     * 汉字编码：第一字节0xB0-0xF7（对应区号：16-87），第二个字节0xA1-0xFE（对应位号：01-94）。
     * 例如最后一个码位是9494，区号和位号分别转换成十六进制是5E5E，0x5E+0xA0=0xFE，所以该码位的GB2312编码是FEFE。
     */
    public String getChineseCharacters(int size) {
        final int AREA_CODE_55 = 215;
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // 第一字节范围：0xB0~0xF7（对应区号：16~87），最常用的一级汉字（16~55），最后加上0xA0（160）
            // Random.nextInt(n)取值为0~(n-1)不包含n，所以16~55为16+nextInt(55-16)+160
            int r = 176 + random.nextInt(39);
            // 第二字节范围：0xA1~0xFE（对应位号：01~94），最后加上0xA0（160）
            // 当区码为55时位码范围为1~89，其他为1~94
            int c = 161 + random.nextInt(94);
            byte[] bytes = new byte[]{(byte) r, (byte) c};
            try {
                result.append(new String(bytes, "GB2312"));
            } catch (UnsupportedEncodingException ignore) {
            }
        }
        return result.toString();
    }

}
