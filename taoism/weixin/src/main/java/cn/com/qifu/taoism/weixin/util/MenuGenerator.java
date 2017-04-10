package cn.com.qifu.taoism.weixin.util;

import cn.com.qifu.taoism.core.CoreConstant;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by zy on 2017/4/1.
 */
public class MenuGenerator {
    private String appId;

    private String appSecret;

    private String host;

    private Map<String, String> placeholderMap;

    public MenuGenerator() {
        loadProps();
        placeholderMap = new HashMap<>(2);
        placeholderMap.put("${openid}", this.appId);
        placeholderMap.put("${host}", this.host);
    }

    public void createMenu() {
        try {
            String menu = loadMenuJson();

            //连接超时30秒
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            //读取超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");

            String accessToken = WeiXinKeys.getAccessToken(appId, appSecret);
            String action = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;

            URL url = new URL(action);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();

            OutputStream os = http.getOutputStream();
            os.write(menu.getBytes(CoreConstant.UTF_8));
            os.flush();
            os.close();
            InputStream is = http.getInputStream();
            byte[] jsonBytes = new byte[is.available()];
            is.read(jsonBytes);
            System.out.println("\nresponse: " + new String(jsonBytes, CoreConstant.UTF_8));
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProps() {
        ResourceBundle rb = ResourceBundle.getBundle("weixin");
        this.appId = rb.getString("weixin.AppID");
        this.appSecret = rb.getString("weixin.AppSecret");
        this.host = rb.getString("weixin.Host");
    }

    public String loadMenuJson() throws IOException {

        File menuDataFile = new File(MenuGenerator.class.getClassLoader().getResource("Menu.json").getFile());

        if (!menuDataFile.exists() || !menuDataFile.isFile()) {
            //throw new ServiceException("无法加载菜单数据");

        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonStr = mapper.readTree(menuDataFile).toString();

        //replace placeholder
        for (Map.Entry<String, String> e : placeholderMap.entrySet()) {
            jsonStr = jsonStr.replace(e.getKey(), e.getValue());
        }

        return jsonStr;
    }

    public static void main(String[] args) throws Exception {
        MenuGenerator generator = new MenuGenerator();
        generator.createMenu();

    }
}
