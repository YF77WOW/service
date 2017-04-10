
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <div>
        <div><h2>你的openid：${weiXinBean.openid}</h2></div>
        <div><h2>你的昵称：${weiXinBean.nickname}</h2></div>
        <div><h2>你的性别：${weiXinBean.sex}</h2></div>
        <div><h2>你的省份：${weiXinBean.province}</h2></div>
        <div><h2>你的城市：${weiXinBean.city}</h2></div>
        <div><h2>你的国家：${weiXinBean.country}</h2></div>
        <div>头像<img src="${weiXinBean.headimgurl}"></div>
    </div>
</body>
</html>
