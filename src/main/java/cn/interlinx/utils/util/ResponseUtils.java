package cn.interlinx.utils.util;


import net.minidev.json.JSONObject;

public class ResponseUtils {

    public static String getResult(String status, String msg, Object data) {
        JSONObject result = new JSONObject();
        result.put("status", status);
        result.put("msg", msg);
        result.put("data", data);
        return result.toJSONString();
    }

}
