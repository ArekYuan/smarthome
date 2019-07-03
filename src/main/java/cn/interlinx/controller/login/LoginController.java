package cn.interlinx.controller.login;

import cn.interlinx.entity.Userinfo;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.utils.consts.Constant;
import cn.interlinx.utils.util.HttpUtils;
import cn.interlinx.utils.util.ResponseUtils;
import cn.interlinx.utils.util.StringUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by bysocket on 07/02/2017.
 */
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;
    String jsonObject;


    /**
     * 用户名密码登录，后台登录
     *
     * @param userName
     * @param passWord
     * @return
     */
    @RequestMapping(value = "/api/loginByName", method = RequestMethod.GET)
    public String loginByLogin(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String passWord) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Userinfo info;
        if (userName != null && passWord != null) {
//            String pwd = PasswordEncrypt.encodeByMd5(passWord);
            info = loginService.selectByLogin(userName, passWord);
            if (info != null) {
                jsonObject = ResponseUtils.getResult("200", "获取成功", info);
            } else {
                jsonObject = ResponseUtils.getResult("505", "用户名或密码错误", "");
            }
        } else {
            jsonObject = ResponseUtils.getResult("505", "用户名或密码错误", "");
        }

        return jsonObject;
    }

    /**
     * 微信小程序登录
     *
     * @param openId
     * @return
     */
    @RequestMapping(value = "/api/loginByAppId", method = RequestMethod.GET)
    public String loginByAppId(@RequestParam(value = "openId") String openId) {
        Userinfo info = loginService.selectById(openId);
        if (info != null) {
            jsonObject = ResponseUtils.getResult("200", "获取成功", info);
        } else {
            jsonObject = ResponseUtils.getResult("500", "用户名或密码错误", "");
        }
        return jsonObject;
    }

    /**
     * 通过微信端传输过来的code 获取openid
     *
     * @param code     code
     * @param nickName 用户信息
     * @return
     */
    @RequestMapping(value = "/api/loginWx", method = RequestMethod.POST)
    public String getOpenIdByCode(@RequestParam(value = "code") String code,
                                  @RequestParam(value = "nickName") String nickName,
                                  @RequestParam(value = "avatarUrl") String avatarUrl,
                                  @RequestParam(value = "wifiMac") String wifiMac) {
        Map<String, Object> resp = HttpUtils.getWxUserOpenid(code, Constant.APP_ID, Constant.APP_SECRET, Constant.GRANT_TYPE);

        boolean isOpenId = resp.containsKey("openid");
        boolean isErrorCode = resp.containsKey("errcode");

        if (isOpenId) {
            String openid = (String) resp.get("openid");
            JSONObject obj = new JSONObject();
            String sessionKey = (String) resp.get("session_key");
            Userinfo user = insert(openid, nickName, avatarUrl, wifiMac);
            if (user != null) {
                obj.put("data", user);
                jsonObject = ResponseUtils.getResult("0", "请求成功", obj);
            } else {
                jsonObject = ResponseUtils.getResult("500", "后台错误", "");
            }
        } else if (isErrorCode) {
            int errcode = (int) resp.get("errcode");

            jsonObject = ResponseUtils.getResult(errcode + "", "系统繁忙，请稍候重试", resp);
        } else {
            jsonObject = ResponseUtils.getResult("-1", "系统繁忙，请稍候重试", resp);
        }


        return jsonObject;
    }


    @RequestMapping(value = "/api/login_wx", method = RequestMethod.POST)
    public String getOpenId_Code(@RequestParam(value = "code") String code) {
        Map<String, Object> resp = HttpUtils.getWxUserOpenid(code, Constant.APP_ID, Constant.APP_SECRET, Constant.GRANT_TYPE);
        Logger.getLogger(LoginController.class.getSimpleName()).info("----->" + resp.toString());
        boolean isOpenId = resp.containsKey("openid");
        boolean isErrorCode = resp.containsKey("errcode");
        if (isOpenId) {
            String openid = (String) resp.get("openid");
            JSONObject obj = new JSONObject();
            String sessionKey = (String) resp.get("session_key");
//            Userinfo user = insert(openid, nickName, wifiMac);
//            if (user != null) {
            obj.put("openid", openid);
            obj.put("sessionKey", sessionKey);
            jsonObject = ResponseUtils.getResult("0", "请求成功", obj);
        } else if (isErrorCode) {
            int errcode = (int) resp.get("errcode");
            jsonObject = ResponseUtils.getResult(errcode + "", "系统繁忙，请稍候重试", resp);
        } else {
            jsonObject = ResponseUtils.getResult("-1", "系统繁忙，请稍候重试", resp);
        }
        return jsonObject;
    }


    /**
     * 解密用户数据 插入 获取用户信息
     *
     * @param nickName
     */
    private Userinfo insert(String openId, String nickName, String avatarUrl, String wifiMac) {
        Userinfo info = null;
        try {
//            String result = StringUtils.getUser(encryptedData);
//            if (null != result && result.length() > 0) {
            Userinfo user = StringUtils.getUser(openId, nickName, avatarUrl, wifiMac);
            if (user != null) {

                loginService.insert(user);
                info = loginService.selectById(user.getOpenid());
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 通过用户id 查询用户是否存在
     *
     * @param openId
     * @return
     */
    @RequestMapping(value = "/api/loginByUserId", method = RequestMethod.GET)
    public String loginBUserId(@RequestParam(value = "userId") Integer openId) {
        Userinfo info = loginService.selectByUserId(openId);
        if (info != null) {
            jsonObject = ResponseUtils.getResult("200", "获取成功", info);
        } else {
            jsonObject = ResponseUtils.getResult("500", "用户不存在", "");
        }
        return jsonObject;
    }

}
