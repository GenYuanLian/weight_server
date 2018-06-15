package com.genyuanlian.pojo;

/**
 * Created by qaa on 2017/10/24.
 * 使用小程序的用户登录时，获取其相关信息
 */
public class LoginData {
    private String openId;//openid，用户唯一标识
    private String sessionKey;//session_key
    private Integer expires_in;//生效时间

    public LoginData(String openId, String sessionKey, Integer expires_in) {
        this.openId = openId;
        this.sessionKey = sessionKey;
        this.expires_in = expires_in;
    }

    public String getOpenId() {
        return openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public Integer getExpires_in() {
        return expires_in;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "openId='" + openId + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", expires_in='" + expires_in + '\'' +
                '}';
    }
}
