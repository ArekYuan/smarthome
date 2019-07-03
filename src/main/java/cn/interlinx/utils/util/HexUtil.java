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
    public static List<String> bytes2HexString(byte[] b) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            log.info("---hex-->" + hex);
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

}
