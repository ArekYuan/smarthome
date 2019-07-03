package cn.interlinx.controller.personal;

import cn.interlinx.entity.Userinfo;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.utils.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonalController {

    String jsonStr = "";

    @Autowired
    LoginService loginService;

    /**
     * 获取物流信息
     *
     * @param expCode 快递公司编码
     * @param expNo   物流单号
     * @return
     */
    @RequestMapping(value = "api/personal/orderTraces", method = RequestMethod.POST)
    public String getOrderTraces(Integer userId, String expCode, String expNo) {
        Userinfo userinfo = loginService.selectByUserId(userId);
        if (userinfo != null) {
            KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
            try {
                String result = api.getOrderTracesByJson(expCode, expNo);
                jsonStr = ResponseUtils.getResult("200", "获取物流数据成功", result);
            } catch (Exception e) {
                e.printStackTrace();
                jsonStr = ResponseUtils.getResult("-1", "获取物流数据失败", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("-1", "请重新登录", "");
        }
        return jsonStr;
    }

}
