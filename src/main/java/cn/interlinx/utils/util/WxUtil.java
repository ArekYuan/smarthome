package cn.interlinx.utils.util;

import cn.interlinx.utils.json.AccessToken;
import net.minidev.json.JSONObject;
import net.sf.json.util.JSONUtils;

import java.util.HashMap;
import java.util.Map;

import static cn.interlinx.utils.util.MpApi.GetAccessTokenUrl;

public class WxUtil {

    public final static String kf_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send";

    public static String getToken() throws Exception {
        String accessToken = HttpUtil.executeGet(GetAccessTokenUrl);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(accessToken);
        return String.valueOf(jsonObject.get("access_token"));
    }

    public static String sendKfMessage(String openid, String text, String access_token) throws Exception {

        JSONObject object = new JSONObject();

        Map<String, Object> map_content = new HashMap<>();
        map_content.put("content", text);
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openid);
        map.put("msgtype", "text");

        map.put("text", map_content);
        object.put("touser", openid);
        object.put("msgtype", "text");
        object.put("text", map_content);

        String content = object.toJSONString();
        return HttpUtil.doPost(kf_url + "?access_token=" + access_token, content);

    }

}
