package cn.interlinx.web;


import cn.interlinx.utils.consts.WebApi;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;


/**
 * 跳转页面
 */
@WebServlet(name = "airkiss", urlPatterns = "/airkiss")
public class BackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
//            RequestDispatcher dispatcher = req
//                    .getRequestDispatcher("/WEB-INF/jsp/index.jsp");
            //配置参数信息（url 参数为跳转js配置信息页面链接,56c30bcb.ngrok.io 为 ngrok 映射的地址）
            req.setAttribute("config",
                    WebApi.createConfig("http://a16705de.ngrok.io/airkiss"));
            req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, resp);
//            dispatcher.forward(req, resp);
        } catch (Throwable e) {
            e.printStackTrace();
            out("", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            RequestDispatcher dispatcher = req
                    .getRequestDispatcher("/WEB-INF/jsp/index.jsp");
            req.setAttribute("config", WebApi.createConfig("http://a16705de.ngrok.io/airkiss"));
            dispatcher.forward(req, resp);
        } catch (Throwable e) {
            e.printStackTrace();
            // 异常时响应空串
            out("", resp);
        }
    }


    /**
     * 输出字符串
     */
    protected void out(String str, HttpServletResponse response) {
        Writer out = null;
        try {
            response.setContentType("text/xml;charset=UTF-8");
            out = response.getWriter();
            out.append(str);
            out.flush();
        } catch (IOException e) {
            // ignore
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

//	/**
//	 * 解析请求中的xml元素为Map
//	 */
//	@SuppressWarnings("unchecked")
//	private static Map<String, String> parseXml(InputStream in)
//			throws DocumentException, IOException {
//		Map<String, String> map = new HashMap<String, String>();
//		SAXReader reader = new SAXReader();
//		Document document = reader.read(in);
//		Element root = document.getRootElement();
//		List<Element> elementList = root.elements();
//		for (Element e : elementList) {
//			map.put(e.getName(), e.getText());
//		}
//		return map;
//	}

}
