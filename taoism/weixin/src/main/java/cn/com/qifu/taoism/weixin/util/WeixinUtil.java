package cn.com.qifu.taoism.weixin.util;

/**
 * Created by zy on 2017/4/1.
 */
public class WeixinUtil {
    /**
     * 返回给腾讯的xml文本信息
     */
    public static String getXml(String fromUserName, String toUserName, String content, String createTime, String msgType) {
        if (fromUserName == null || toUserName == null || content == null || createTime == null || msgType == null) {
            return null;
        }
        String weiXinXml = "";
        try {
            weiXinXml = "<xml>" +
                    "<ToUserName><![CDATA[" +  fromUserName+ "]]></ToUserName>" +
                    "<FromUserName><![CDATA[" + toUserName + "]]></FromUserName>" +
                    "<CreateTime>" + createTime + "</CreateTime>" +
                    "<MsgType><![CDATA[text]]></MsgType>" +
                    "<Content><![CDATA[" + content + "]]></Content>" +
                    "</xml>";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weiXinXml;
    }
}
