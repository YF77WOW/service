package cn.com.qifu.taoism.weixin.controller.mvc;

import cn.com.qifu.taoism.core.CoreConstant;
import cn.com.qifu.taoism.weixin.WeiXinBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by zy on 2017/4/5.
 */
@Controller
public class RouterMVCController extends BaseMVCController {
    @RequestMapping(value = "/getopenid_{type}")
    public ModelAndView getOpenId(HttpSession session,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam(value = "code") String code,
                                  @PathVariable(value = "type") String type) {
        ModelAndView model = new ModelAndView();
        WeiXinBean weiXinBean = handleOpenId(session, request, code, model);
        System.out.println("用户openId:" + weiXinBean.getOpenid());
        weiXinBean = handleUserInfo(weiXinBean.getAccess_token(), weiXinBean.getOpenid());
        switch (type) {
            case "test":
                model.addObject("weiXinBean", weiXinBean);
                model.setViewName("/test");
                break;
            default:
                model.setViewName("/index");
        }
        return model;
    }

    @RequestMapping(value = "/weiXinUser")
    @ResponseBody
    public WeiXinBean getWeiXinUser(HttpServletResponse response,@RequestParam(value = "code") String code){
        response.setHeader(CoreConstant.HEADER, CoreConstant.HEADER_ADDRESS);
        WeiXinBean weiXinBean = handleOpenId(null, null, code, null);
        weiXinBean = handleUserInfo(weiXinBean.getAccess_token(), weiXinBean.getOpenid());
        return weiXinBean;
    }

}
