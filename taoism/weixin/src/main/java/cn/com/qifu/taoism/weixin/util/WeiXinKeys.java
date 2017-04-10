package cn.com.qifu.taoism.weixin.util;

import cn.com.qifu.taoism.weixin.WeiXinBean;
import com.google.gson.Gson;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by zy on 2017/4/1.
 */
public class WeiXinKeys {
    public static String getAccessToken(String appId, String appSecret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appId + "&secret=" + appSecret;
        String accessToken = null;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");      //必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
            http.connect();

            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            System.out.print(message);
            Gson gson = new Gson();
            WeiXinBean weiXinBean = gson.fromJson(message, WeiXinBean.class);
            accessToken = weiXinBean.getAccess_token();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;

    }

    public static String getOpenId(String appId, String appSecret) {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + appId + "&secret=" + appSecret;
        String accessToken = null;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET");      //必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");//连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); //读取超时30秒
            http.connect();

            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            System.out.print(message);
            Gson gson = new Gson();
            WeiXinBean weiXinBean = gson.fromJson(message, WeiXinBean.class);
            accessToken = weiXinBean.getAccess_token();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;

    }
    public static void main(String[] args) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("weixin");

        String appId = rb.getString("weixin.AppID");
        String secret = rb.getString("weixin.AppSecret");
        String host = rb.getString("weixin.Host");
        System.out.println("appId:"+appId+",secret:"+secret+",host:"+host+"\n");

        String token = WeiXinKeys.getAccessToken(appId,secret);

        System.out.println("token:"+token);

    }
}
