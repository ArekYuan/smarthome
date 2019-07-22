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
    @RequestMapping(value = "/api/orderTraces")
    public String getOrderTraces(Integer userId, @Param("expCode") String expCode, String expNo) {
//        Userinfo userinfo = loginService.selectByUserId(userId);
        KD_Info info = personalService.selectByKdName(expCode);
        if (info != null) {
            KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
            try {
                jsonStr = api.getOrderTracesByJson(info.getKd_num(), expNo);
//                jsonStr = ResponseUtils.getResult("200", "获取物流数据成功", result);
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
    @RequestMapping(value = "/api/addFeedBack")
    public String addFeedBack(Integer userId, String repairName, String repairPhone, String repairImgUrl, String desc) {
        String jsonStr;
        Feedback feedback = new Feedback();
        feedback.setUserid(userId);
        feedback.setFeedback(desc);
        feedback.setRepairImgUrl(repairImgUrl);
        feedback.setRepairName(repairName);
        feedback.setRepairPhone(repairPhone);
        feedback.setDescType(1);
        feedback.setRepairTime(new Date());
        int flag = personalService.addFeedBack(feedback);
        if (flag == 1) {
            jsonStr = ResponseUtils.getResult("200", "上报成功，请耐心等待", "");
        } else {
            jsonStr = ResponseUtils.getResult("-1", "上报失败", "");
        }

        return jsonStr;
    }

    /**
     * 给我们留言
     *
     * @param userId      用户id
     * @param repairName  留言人姓名
     * @param repairPhone 留言人电话号码
     * @param desc        建议
     * @return JsonStr
     */
    @RequestMapping(value = "/api/liuYan")
    public String addFeedBack(Integer userId, String repairName, String repairPhone, String desc) {
        String jsonStr;
        Feedback feedback = new Feedback();
        feedback.setUserid(userId);
        feedback.setFeedback(desc);
        feedback.setRepairName(repairName);
        feedback.setRepairPhone(repairPhone);
        feedback.setDescType(2);
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
