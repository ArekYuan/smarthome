<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>TEST</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        html, body {
            height: 100%;
            width: 100%;
        }
    </style>
</head>
<body>
<h1>设置设备Wifi</h1>
<br><br>
<div id="message"></div>
</body>
<script src="${ctx}/jquery-3.1.0.min.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
    wx.config({
        beta: true, // 开启内测接口调用，注入wx.invoke方法
        debug: false, // 开启调试模式
        appId: '${config.appId}', // 第三方app唯一标识
        timestamp: '${config.timestamp}', // 生成签名的时间戳
        nonceStr: '${config.nonce}', // 生成签名的随机串
        signature: '${config.signature}',// 签名
        jsApiList: ['configWXDeviceWiFi'] // 需要使用的jsapi列表
    });

    var second = 5;
    wx.ready(function () {
        wx.checkJsApi({
            jsApiList: ['configWXDeviceWiFi'],
            success: function (res) {
                wx.invoke('configWXDeviceWiFi', {}, function (res) {
                    var err_msg = res.err_msg;
                    if (err_msg == 'configWXDeviceWiFi:ok') {
                        $('#message').html("配置 WIFI成功，<span id='second'>5</span>秒后跳转到首页。");
                        setInterval(count, 1000);
                        return;
                    } else {
                        $('#message').html("配置 WIFI失败，是否<a href=\"/wechat/scan/airkiss" + window.location.search + "\">再次扫描</a>。<br>不配置WIFI,<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxba9815745454a069&redirect_uri=http://letux.xyz/wechat/page/main&response_type=code&scope=snsapi_base&state=1#wechat_redirect\">直接进入首页</a>。");
                    }
                });
            }
        });
    });

    function count() {
        second--;
        $('#second').html(second);
        if (second == 0) {
            //跳转到首页
            window.location.href = '/consumer/main'
        }
    }

</script>
</html>