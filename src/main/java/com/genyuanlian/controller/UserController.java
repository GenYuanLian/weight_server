package com.genyuanlian.controller;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.pojo.*;
import com.genyuanlian.service.OnLoginService;
import com.genyuanlian.service.UserService;
import com.genyuanlian.service.WalletService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by zhuyst on 2017/7/8.
 */
@RestController
@EnableAutoConfiguration
public class UserController {

    private static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private OnLoginService onLoginService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    //在用户登录时使用登录凭证 requestdata 获取 session_key 和 openid
    @RequestMapping(value = "/onLogin")
    @ResponseBody
    public String onLogin(@RequestBody String requestdata) throws Exception{
        logger.info("++++++++++++++++++++++++++++用户登录+++++++++++++++++++++++++++++++");
        logger.info("requestdata:" + requestdata);
        JSONObject jsonObject = new JSONObject(requestdata);
        String code = (String) jsonObject.get("code");
        String sessionKey = onLoginService.onLogin(code);
        String openId = SessionKey.SessionsMap.get(sessionKey).getOpenId();
        walletService.createWalletByUserName(openId);
        return sessionKey;
    }

    //获取钱包地址
    @RequestMapping(value = "/checkPubAddr")
    @ResponseBody
    public WalletPubAddr checkPubAddr(HttpServletRequest request) {
        String sessionKey = request.getHeader("Session-Key");
        return userService.getWalletPubAddr(sessionKey);
    }

    //获取余额
    @RequestMapping(value = "/checkBalance")
    @ResponseBody
    public WalletBalance checkBalance(HttpServletRequest request) {
        WalletBalance resData = new WalletBalance();
        resData.setStatus(CODE.SYSTEM_ERROR);
        try {
            String sessionKey = request.getHeader("Session-Key");
            resData = walletService.getWalletBalanceBySessionKey(sessionKey);
        } catch (Exception exc){
            logger.error("checkBalance: " + exc.getMessage());
        }
        return resData;
    }

    //提现操作
    @RequestMapping(value = "/withDraw")
    @ResponseBody
    public int withDraw(HttpServletRequest request, @RequestBody WithDrawData withDrawData) {
        String sessionKey = request.getHeader("Session-Key");
        return userService.withDraw(sessionKey, withDrawData);
    }

    @RequestMapping(value = "/register")
    @ResponseBody
    public LoginRegistResData register(@RequestBody LoginRegistData loginRegistData) {
        LoginRegistResData data;
        data = userService.register(loginRegistData.getUsername(), loginRegistData.getPassword());
        if (data.getStatus() == CODE.SUCCESS) {
            data.setStatus(walletService.createWalletByUserName(loginRegistData.getUsername()));
        }
        return data;
    }

    @RequestMapping(value = "/login")
    @ResponseBody
    public LoginRegistResData login(@RequestBody LoginRegistData loginRegistData) {
        LoginRegistResData data;
        data = userService.login(loginRegistData);
        if (data.getStatus() == CODE.SUCCESS) {
            data.setStatus(walletService.createWalletByUserName(loginRegistData.getUsername()));
        }
        return data;
    }

    @RequestMapping(value = "/getAllUsers")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
