package cn.interlinx.controller.wechat;

import cn.interlinx.utils.util.CheckoutUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WeChatController {

    /**
     * token 验证
     */
    @RequestMapping(value = "/api/personal/signatureWx", method = RequestMethod.GET)
    public String signatureWx(String signature, String timestamp, String nonce, String echostr) {
        String str = "";
        if (signature != null && CheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            str = echostr;
        }
        return str;
    }

//    @RequestMapping(value = "/airkiss", method = RequestMethod.GET)
//    public String testF2F() {
//        return "index";
//    }
}

