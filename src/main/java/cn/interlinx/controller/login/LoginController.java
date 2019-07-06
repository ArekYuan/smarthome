package cn.interlinx.controller.login;

import cn.interlinx.entity.Userinfo;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.utils.consts.Constant;
import cn.interlinx.utils.util.HttpUtils;
import cn.interlinx.utils.util.ResponseUtils;
import cn.interlinx.utils.util.StringUtils;
import com.google.gson.Gson;
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
    private Gson gson = new Gson();

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
        Userinfo info = loginService.selectByOpenId(openId);
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
    @RequestMapping(value = "/api/loginWx", method = RequestMethod.GET)
    public String getOpenIdByCode(@RequestParam(value = "code") String code,
                                  @RequestParam(value = "nickName") String nickName,
                                  @RequestParam(value = "avatarUrl") String avatarUrl,
                                  @RequestParam(value = "wifiMac") String wifiMac) {
        Map<String, Object> resp = HttpUtils.getWxUserOpenid(code, Constant.APP_ID, Constant.APP_SECRET, Constant.GRANT_TYPE);

        boolean isOpenId = resp.containsKey("openid");
        boolean isErrorCode = resp.containsKey("errcode");
        int flag = 0;
        String msg = "";
        if (isOpenId) {
            String openid = (String) resp.get("openid");
            JSONObject obj = new JSONObject();
            String sessionKey = (String) resp.get("session_key");
            Userinfo user = loginService.selectByOpenId(openid);
            if (user == null) {
                if (wifiMac.equals("undefined")) {
                    wifiMac = "";
                }
//                Userinfo userinfo = insert(openid, nickName, avatarUrl, wifiMac);
                Userinfo userinfo = StringUtils.getUser(openid, nickName, avatarUrl, wifiMac);
                flag = loginService.insert(userinfo);

                if (flag == 1) {
                    msg = "新增数据成功";
                } else {
                    msg = "新增数据失败";
                }
            } else {
//                Userinfo userinfo = StringUtils.getUser(openid, nickName, avatarUrl, wifiMac);
//                user.setUserid(user.getUserid());
                user.setOpenid(openid);
                user.setUsername(nickName);
                user.setImgurl(avatarUrl);
                user.setMac(wifiMac);
                flag = loginService.updateUserInfo(user);
                if (flag == 1) {
                    msg = "新增数据成功";
                } else {
                    msg = "新增数据失败";
                }
            }

            obj.put("openid", openid);
            obj.put("sessionKey", sessionKey);
            obj.put("msg", msg);
            jsonObject = gson.toJson(obj);
//            if (user != null) {
//                obj.put("data", user);
//                jsonObject = ResponseUtils.getResult("0", "请求成功", obj);
//            } else {
//                jsonObject = ResponseUtils.getResult("500", "后台错误", "");
//            }
        } else if (isErrorCode) {
            int errcode = (int) resp.get("errcode");

            jsonObject = ResponseUtils.getResult(errcode + "", "系统繁忙，请稍候重试", "");
        } else {
            jsonObject = ResponseUtils.getResult("-1", "系统繁忙，请稍候重试", "");
        }


        return jsonObject;
    }


    @RequestMapping(value = "/api/login_wx", method = RequestMethod.GET)
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
            jsonObject = gson.toJson(obj);
        } else if (isErrorCode) {
            int errcode = (int) resp.get("errcode");
            jsonObject = ResponseUtils.getResult(errcode + "", "系统繁忙，请稍候重试", "");
        } else {
            jsonObject = ResponseUtils.getResult("-1", "系统繁忙，请稍候重试", "");
        }
        return jsonObject;
    }


    /**
     * 通过用户id 查询用户是否存在
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/loginByUserId", method = RequestMethod.GET)
    public String loginByUserId(@RequestParam(value = "userId") Integer userId) {
        Userinfo info = loginService.selectByUserId(userId);
        if (info != null) {
            jsonObject = ResponseUtils.getResult("200", "获取成功", info);
        } else {
            jsonObject = ResponseUtils.getResult("500", "用户不存在", "");
        }
        return jsonObject;
    }

    /**
     * 插入一条数据
     *
     * @param nickName
     * @param avatarUrl
     * @param wifiMac
     * @return
     */
    @RequestMapping(value = "/api/bindUser", method = RequestMethod.GET)
    public String BindUser(@RequestParam(value = "openid") String openid, String nickName, String avatarUrl, String wifiMac) {
        String jsonStr;
        if (!openid.equals("undefined")) {
            Userinfo user = loginService.selectByOpenId(openid);
            if (user == null) {
                Userinfo userinfo = StringUtils.getUser(openid, nickName, avatarUrl, wifiMac);
                int flag = loginService.insert(userinfo);
                jsonStr = ResponseUtils.getResult(flag == 1 ? "200" : "500", flag == 1 ? "保存成功" : "保存失败", "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "用户已存在", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("5001", "openId异常", "");
        }
        return jsonStr;
    }


    @RequestMapping(value = "/api/updateUser", method = RequestMethod.GET)
    public String UpdateUserInfo(String sex) {
        Userinfo info = loginService.selectByUserId(11);
        String jsonStr = "";
        if (info != null) {
//            Userinfo userinfo = new Userinfo();
//            userinfo.setUserid(11);
            info.setSex(sex);
            int flag = loginService.updateUserInfo(info);

            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "修改数据成功", "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "修改数据失败,服务器错误", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("404", "用户不存在", "");

        }
        return jsonStr;
    }
}
