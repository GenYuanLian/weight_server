package com.genyuanlian.service;

import com.genyuanlian.pojo.LoginData;
import com.genyuanlian.pojo.SessionKey;
import com.genyuanlian.utils.UHttpClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qaa on 2017/10/24.
 * @author zhao qing
 */
@Service
public class OnLoginService {
    private Logger logger = Logger.getLogger(OnLoginService.class);

    ////在用户登录时使用登录凭证 code 获取 session_key 和 openid
    public String onLogin(String code) throws Exception {
        String baseUrl = "https://api.weixin.qq.com/sns/jscode2session";
        String appId = "wx47a0694aa997f205";
        String secret = "1508c89ad6e503e0ed591330bf9c0995";
        String jsCode = code;

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("appid",appId);
        params.put("secret",secret);
        params.put("js_code",jsCode);
        params.put("grant_type","authorization_code");

        UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.get,baseUrl,params,"UTF-8",true);

        //解析openid、session_key
        JSONObject jsonObject = new JSONObject(res.content);
        String openId = (String) jsonObject.get("openid");
        String sessionKey = (String) jsonObject.get("session_key");

        Integer expireIn = null/*(Integer) jsonObject.get("expires_in")*/;
        LoginData loginData = new LoginData(openId,sessionKey,expireIn);

        String localSessionKey =  DigestUtils.md5Hex(appId + openId);//生成自己的sessionkey
        SessionKey.SessionsMap.put(localSessionKey,loginData);//存入全局map
        logger.info("已存入SessionsMap" + ",key:" + localSessionKey + ",value:" + loginData);

        return localSessionKey;
    }
}
