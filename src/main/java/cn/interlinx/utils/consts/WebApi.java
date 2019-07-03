package cn.interlinx.utils.consts;

import cn.interlinx.utils.util.HttpUtil;
import cn.interlinx.utils.util.SHA1;
import net.sf.json.JSONObject;

import java.util.UUID;

/**
 * 网站配置相关 API
 */
public class WebApi {
	private static final String JsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=ACCESS_TOKEN";

	/**
	 * 生成JsAPIConfig
	 * 
	 * @Title: createConfig
	 * @Description: TODO
	 * @param @param link 使用AirKiss的链接地址
	 * @param @return
	 * @param @throws Exception
	 * @return JsAPIConfig
	 * @throws
	 */
	public static JsAPIConfig createConfig(String link) throws Exception {
		JsAPIConfig config = new JsAPIConfig();
		config.setLink(link);
		String nonce = UUID.randomUUID().toString();
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String src = "jsapi_ticket=" + getJsTicket() + "&noncestr=" + nonce
				+ "&timestamp=" + timestamp + "&url=" + config.getLink();
		String signature = SHA1.gen(src);
		config.setAppId(WxConfig.APPID);
		config.setDebug(true);
		config.setNonce(nonce);
		config.setTimestamp(timestamp);
		config.setSignature(signature);

		return config;
	}

	/**
	 * 获取jsapi_ticket
	 * 
	 * @return
	 */
	public static String getJsTicket() {
		String ticketXML = HttpUtil.doGet(JsapiTicketUrl);
		JSONObject codeJson = JSONObject.fromObject(ticketXML);
		String ticket = codeJson.getString("ticket");
		System.out.println("ticket=" + ticket);
		return ticket;

	}

}
