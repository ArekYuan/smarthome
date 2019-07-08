package cn.interlinx.controller.personal;

import cn.interlinx.entity.Feedback;
import cn.interlinx.entity.KD_Info;
import cn.interlinx.entity.Userinfo;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.service.personal.PersonalService;
import cn.interlinx.utils.util.ResponseUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class PersonalController {

    String jsonStr = "";

    @Autowired
    LoginService loginService;

    @Autowired
    PersonalService personalService;

    /**
     * 获取物流信息
     *
     * @param expCode 快递公司简称
     * @param expNo   物流单号
     * @return
     */
    @RequestMapping(value = "/smarthome/api/personal/orderTraces", method = RequestMethod.POST)
    public String getOrderTraces(Integer userId, @Param("expCode") String expCode, String expNo) {
//        Userinfo userinfo = loginService.selectByUserId(userId);
        KD_Info info = personalService.selectByKdName(expCode);
        if (info != null) {
            KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
            try {
                String result = api.getOrderTracesByJson(info.getKd_num(), expNo);
                jsonStr = ResponseUtils.getResult("200", "获取物流数据成功", result);
            } catch (Exception e) {
                e.printStackTrace();
                jsonStr = ResponseUtils.getResult("-1", "获取物流数据失败", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("-1", "请输入正确的物流公司名称", "");
        }
        return jsonStr;
    }

    /**
     * 添加产品反馈
     *
     * @param userId
     * @param repairName
     * @param repairPhone
     * @param repairImgUrl
     * @param desc
     * @return
     */
    @RequestMapping(value = "/smarthome/api/personal/addFeedBack")
    public String addFeedBack(Integer userId, String repairName, String repairPhone, String repairImgUrl, String desc) {
        String jsonStr;
        Feedback feedback = new Feedback();
        feedback.setUserid(userId);
        feedback.setFeedback(desc);
        feedback.setRepairImgUrl(repairImgUrl);
        feedback.setRepairName(repairName);
        feedback.setRepairPhone(repairPhone);
        feedback.setRepairTime(new Date());
        int flag = personalService.addFeedBack(feedback);
        if (flag == 1) {
            jsonStr = ResponseUtils.getResult("200", "上报成功，请耐心等待", "");
        } else {
            jsonStr = ResponseUtils.getResult("-1", "上报失败", "");
        }

        return jsonStr;
    }


}
