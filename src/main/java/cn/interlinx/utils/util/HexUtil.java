package cn.interlinx.utils.util;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HexUtil {

    private static Logger log = Logger.getLogger(HexUtil.class.getSimpleName());

    /**
     * 字节转16进制字符串
     *
     * @param b
     * @return
     */
    public static synchronized List<String> bytes2HexString(byte[] b) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            list.add(hex);
        }
        return list;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * @param strList mcu 端 向服务器发送的 16进通讯码
     * @return String
     */
    public static List<String> getMcuDataList(List<String> strList) {
        List<String> nbList = new ArrayList<>();
//        String str = "";

        if (strList != null && strList.size() > 0) {
            if (strList.get(0).equals("55") && strList.get(1).equals("AA")) {
//                int len = Integer.valueOf(List.get(2));
                int b = Integer.parseInt(strList.get(2), 16);//将16进制数转成10进制数
                int b1 = Integer.parseInt(strList.get(3), 16);//将16进制数转成10进制数
                int a1 = (int) (b << 8 | b1);
                System.out.println("---数据长度---->" + a1);
                System.out.println("---实际长度---->" + strList.size());

                if (a1 == strList.size()) {
                    nbList.clear();
                    nbList.addAll(strList);

                } else if (a1 > strList.size()) {
                    nbList.clear();
                    nbList.addAll(strList.subList(0, a1));
                } else {
                    nbList.clear();
                    nbList.addAll(strList);
                }

            }
        }
        return nbList;
    }


    public static String getMcuData(List<String> strList) {
        String str = "";
        if (strList != null && strList.size() > 0) {
            if (strList.get(0).equals("55") && strList.get(1).equals("AA")) {
//                int len = Integer.valueOf(List.get(2));
                int b = Integer.parseInt(strList.get(2), 16);//将16进制数转成10进制数
                int b1 = Integer.parseInt(strList.get(3), 16);//将16进制数转成10进制数
                int a1 = (int) (b << 8 | b1);
                System.out.println("---数据长度---->" + a1);
                if (a1 == strList.size()) {
                    switch (strList.get(4)) {
                        case "10"://0x10:设备登录到服务器
                            str = "0x10";
                            break;
                        case "11"://0x11:设备状态上报
                            str = "0x11";
                            break;
                        case "12"://0x12:服务器控制设备状态
                            str = "0x12";
                            break;
                        case "13"://0x13:心跳包
                            str = "0x13";
                            break;
                    }

                }

            }
        }
        return str;
    }


    public static String getSun(String[] list) {
        int sum = 0;
        for (int i = 0; i < list.length - 2; i++) {
            sum += Integer.parseInt(list[i], 16);
        }
        int mod = sum % 256;
        String hex = Integer.toHexString(mod);
        int len = hex.length();
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;

    }

}
