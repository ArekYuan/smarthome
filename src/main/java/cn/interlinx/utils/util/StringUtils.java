package cn.interlinx.utils.util;

import cn.interlinx.entity.Userinfo;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    public static byte[] decryptBASE64(byte[] key) throws Exception {
        return (new Base64()).decode(key);
    }

    /**
     * BASE64加密 * @param key * @return * @throws Exception
     */
    public static byte[] encryptBASE64(byte[] key) throws Exception {
        return (new Base64()).encode(key);
    }


    public static String getUserInfo(String encryptedData, String sessionKey, String iv) throws Exception {
        byte[] sessionKeyBy = decryptBASE64(sessionKey.getBytes());
        byte[] encryptedDataBy = decryptBASE64(encryptedData.getBytes());
        byte[] ivBy = decryptBASE64(iv.getBytes());
        byte[] dec = Pkcs7Encoder.decryptOfDiyIV(encryptedDataBy, sessionKeyBy, ivBy);
        return new String(dec);
    }


    public static Userinfo getUser(String openId, String nickName,String avatarUrl,String wifiMac) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        JSONObject userInfoJSON = new JSONObject(result);
//        String openId = (String) userInfoJSON.get("openId");
//        String sex = (String) userInfoJSON.get("gender");
//        String imgUrl = (String) userInfoJSON.get("avatarUrl");
//        String userName = (String) userInfoJSON.get("nickName");

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

//        userInfo.put("openId", userInfoJSON.get("openId"));
//        userInfo.put("nickName", userInfoJSON.get("nickName"));
//        userInfo.put("gender", userInfoJSON.get("gender"));
//        userInfo.put("city", userInfoJSON.get("city"));
//        userInfo.put("province", userInfoJSON.get("province"));
//        userInfo.put("country", userInfoJSON.get("country"));
//        userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
        String passWord = PasswordEncrypt.encodeByMd5("123456");
        Userinfo user = new Userinfo();
        user.setUsername(nickName);
        user.setImgurl(avatarUrl);
        user.setCreatetime(timestamp);
        user.setOpenid(openId);
        user.setSex("");
        user.setPassword(passWord);
        user.setIsAdmin(0);
        user.setMac(wifiMac);
        return user;
    }


    public static String getTime(Date date) {
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format0.format(date.getTime());

        return time;
    }


}
