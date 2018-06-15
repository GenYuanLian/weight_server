package com.genyuanlian.pojo;

/**
 * Created by qaa on 2017/10/26.
 * 设置姓名页面的请求数据
 */
public class NameRequestData {
    private String localSessionKey;//本地session-key
    private String name;//用户姓名

    public NameRequestData() {
    }


    public NameRequestData(String localSessionKey, String name) {
        this.localSessionKey = localSessionKey;
        this.name = name;
    }

    public String getLocalSessionKey() {
        return localSessionKey;
    }

    public void setLocalSessionKey(String localSessionKey) {
        this.localSessionKey = localSessionKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NameRequestData{" +
                "localSessionKey='" + localSessionKey + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
