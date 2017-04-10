package cn.com.qifu.taoism.weixin.controller.mvc;

import cn.com.qifu.taoism.core.CoreConstant;
import cn.com.qifu.taoism.weixin.WeiXinBean;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by zy on 2017/4/1.
 */
public class BaseMVCController {

    @Value("${weixin.AppID}")
    private String appid;

    @Value("${weixin.AppSecret}")
    private String appSecret;

    public WeiXinBean handleOpenId(HttpSession session,
                                   HttpServletRequest request,
                                   String code,
                                   ModelAndView model) {
        WeiXinBean weiXinBean = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + this.appid
                            + "&secret=" + this.appSecret
                            + "&code=" + code
                            + "&grant_type=authorization_code");
            HttpResponse response = client.execute(httpGet);
            String entityJson = EntityUtils.toString(response.getEntity());
            if (StringUtils.isBlank(entityJson)) {
                return weiXinBean;
            }
            weiXinBean = new Gson().fromJson(entityJson, WeiXinBean.class);
            if (weiXinBean == null) {
                return weiXinBean;
            }
           /* if (StringUtils.isNotBlank(weiXinBean.getOpenid())) {
                String openid = weiXinBean.getOpenid();
                session.setAttribute(WebConstant.OPEN_ID, openid);
                return openid;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weiXinBean;
    }


    public WeiXinBean handleUserInfo(String access_token, String openid) {
        WeiXinBean weiXinBean = null;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(
                    "https://api.weixin.qq.com/sns/userinfo?" +
                            "access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN");
            HttpResponse response = client.execute(httpGet);
            String entityJson = EntityUtils.toString(response.getEntity());
            if (StringUtils.isBlank(entityJson)) {
                return weiXinBean;
            }
            entityJson = new String(entityJson.getBytes(CoreConstant.ISO_8859_1),CoreConstant.UTF_8);
            weiXinBean = new Gson().fromJson(entityJson, WeiXinBean.class);
            if (weiXinBean == null) {
                return weiXinBean;
            }
            return weiXinBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weiXinBean;
    }
}
