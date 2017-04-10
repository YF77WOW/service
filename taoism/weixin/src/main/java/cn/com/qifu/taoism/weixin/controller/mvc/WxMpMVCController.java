package cn.com.qifu.taoism.weixin.controller.mvc;

import cn.com.qifu.taoism.core.CoreConstant;
import cn.com.qifu.taoism.weixin.WebConstant;
import cn.com.qifu.taoism.weixin.servlet.SimpleHandler;
import me.chanjar.weixin.mp.api.*;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by zy on 2017/4/1.
 */
@Controller
@RequestMapping("/")
public class WxMpMVCController {

    private static Logger logger = LoggerFactory.getLogger(WxMpMVCController.class);

    protected WxMpInMemoryConfigStorage config;
    protected WxMpService wxMpService;
    protected WxMpMessageRouter wxMpMessageRouter;
    @Value("${weixin.AppID}")
    private String appid;

    @Value("${weixin.AppSecret}")
    private String AppSecret;

    @Value("${weixin.Token}")
    private String token;

    @Value("${weixin.EncodingAESKey}")
    private String encodingAESKey;

    @Value("${weixin.Host}")
    private String host;

    private void init() throws ServletException {

        config = new WxMpInMemoryConfigStorage();
        config.setAppId(this.appid); // 设置微信公众号的appid
        config.setSecret(this.AppSecret); // 设置微信公众号的app corpSecret
        config.setToken(this.token); // 设置微信公众号的token
        config.setAesKey(this.encodingAESKey); // 设置微信公众号的EncodingAESKey

        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        WxMpMessageHandler handler = new SimpleHandler();

        wxMpMessageRouter = new WxMpMessageRouter(wxMpService);
        wxMpMessageRouter
                .rule()
                .async(false)
                .content("哈哈") // 拦截内容为“哈哈”的消息
                .handler(handler);

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        init();
        response.setContentType(CoreConstant.CONTENT_TYPE);
        response.setStatus(HttpServletResponse.SC_OK);

        String signature = request.getParameter("signature");
        String nonce = request.getParameter("nonce");
        String timestamp = request.getParameter("timestamp");

        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            // 消息签名不正确，说明不是公众平台发过来的消息
            response.getWriter().println("非法请求");
            return;
        }

        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            response.getWriter().println(echostr);
            System.out.println("echostr返回:" + echostr);
            return;
        }

        String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ?
                "raw" :
                request.getParameter("encrypt_type");

        if ("raw".equals(encryptType)) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            response.getWriter().write(outMessage.toXml());
            return;
        }

        if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            String msgSignature = request.getParameter("msg_signature");
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), config, timestamp, nonce, msgSignature);
            WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
            response.getWriter().write(outMessage.toEncryptedXml(config));
            return;
        }

        response.getWriter().println("不可识别的加密类型");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void post(HttpSession session, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //response.setContentType(CoreConstant.CONTENT_TYPE);
        System.out.println("微信推送service");
        logger.debug("微信推送service");
        String returnXML = null;
        try {

            ServletInputStream weixin = request.getInputStream();

            SAXReader reader = new SAXReader();
            Document document = reader.read(weixin);
//
            String toUserName = document.selectSingleNode("//ToUserName").getText();
            String fromUserName = document.selectSingleNode("//FromUserName").getText();
            String createTime = document.selectSingleNode("//CreateTime").getText();
            String msgType = document.selectSingleNode("//MsgType").getText();
            System.out.println("msgType(事件类型):" + msgType);
            logger.debug("msgType(事件类型):" + msgType);
            session.setAttribute(WebConstant.OPEN_ID, fromUserName);
            if (msgType.equals("text")) {
                String sendMessage;
                //sendMessage = String.format(getWelcomeMsg(), appid, appid);
                //returnXML = WeixinUtil.getXml(fromUserName, toUserName, "欢迎你", createTime, msgType);
                sendText(response.getWriter(), fromUserName, toUserName, "小伙眼光不错");
                System.out.println(returnXML);
                //   response.getWriter().write(returnXML);
                return;
            }
            // 事件判断
            if (msgType.equals("event")) {
                String sendMessage;
                String event = document.selectSingleNode("//Event").getText();
                System.out.println("event:" + event);
                logger.debug("event:" + event);
                //*String eventKey = document.selectSingleNode("//EventKey").getText();*//*
                //logger.info("start processPost even= {}", event);
                switch (event) {
                    case "subscribe":
                        // 关注语
                        sendMessage = String.format(getWelcomeMsg(), appid, appid);
                        //returnXML = WeixinUtil.getXml(fromUserName, toUserName, "helloWorld", createTime, msgType);
                        System.out.println("返回xml信息:" + returnXML);
                        logger.debug("返回xml信息:" + returnXML);
                        sendText(response.getWriter(), fromUserName, toUserName, "你的openid："+fromUserName);
                        break;
                    case "unsubscribe":
                        break;
                    case "CLICK":
                        /*sendMessage = clickEventCallback(eventKey);
                        returnXML = WeixinUtil.getXml(fromUserName, toUserName, sendMessage, createTime, msgType);*/
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送文本信息
     **/
    public void sendText(PrintWriter out, String toUserName, String fromUserName, String content) throws IOException {
        out.println("<xml>");
        out.println("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
        out.println("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
        out.println("<CreateTime><![CDATA[" + System.currentTimeMillis() + "]]></CreateTime>");
        out.println("<MsgType><![CDATA[text]]></MsgType>");
        out.println("<Content><![CDATA[" + content + "]]></Content>");
        out.println("</xml>");
    }

    private String getWelcomeMsg() {
        String callback = "http://" + this.host + "/weixin/";

        return "您好！欢迎关注灵山和安" +
                "<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri="
                + callback + "getopenid_binging_mobile&response_type=code&scope=snsapi_base&state=1#wechat_redirect\">点击这里，立即绑定</a>";

    }
}