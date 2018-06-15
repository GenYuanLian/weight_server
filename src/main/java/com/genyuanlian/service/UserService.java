package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.UserDao;
import com.genyuanlian.pojo.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qaa on 2017/10/25.
 */
@Service
public class UserService {
    private Logger logger = Logger.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private WalletService walletService;

    public LoginRegistResData register(String username, String password) {
        LoginRegistResData data = new LoginRegistResData();
        try {
            User user = userDao.getByUserName(username);
            if (user != null) {
                data.setStatus(CODE.USERNAME_ERROR);
                return data;
            }
            user = new User();
            user.setUsername(username);
            user.setPassword(password);

            String sessionKey = DigestUtils.md5Hex(username + password);
            user.setSessionKey(sessionKey);

            userDao.insert(user);

            data.setStatus(CODE.SUCCESS);
            data.setSessionKey(sessionKey);
            return data;
        } catch (Exception e) {
            logger.error("register: " + e.toString());
            data.setStatus(CODE.SYSTEM_ERROR);
            return data;
        }
    }

    public LoginRegistResData login(LoginRegistData loginRegistData) {
        LoginRegistResData data = new LoginRegistResData();
        try {
            User user = userDao.getByUserNameAndPassword(loginRegistData);
            if (user == null) {
                data.setStatus(CODE.USERNAME_NOT_ERROR);
                return data;
            }

            String sessionKey = DigestUtils.md5Hex(loginRegistData.getUsername() +
                    loginRegistData.getPassword());
            if (!StringUtils.equals(sessionKey, user.getSessionKey())) {
                user.setSessionKey(sessionKey);
                userDao.updateByPrimaryKey(user);
            }

            data.setStatus(CODE.SUCCESS);
            data.setSessionKey(sessionKey);
            return data;
        } catch (Exception e) {
            logger.error("login: " + e.toString());
            data.setStatus(CODE.SYSTEM_ERROR);
            return data;
        }
    }

    public WalletPubAddr getWalletPubAddr(String sessionKey) {
        WalletPubAddr pubAddr = new WalletPubAddr();
        pubAddr.setStatus(CODE.SYSTEM_ERROR);
        try {
            String addr = userDao.getWalletPubAddr(sessionKey);
            pubAddr.setPubAddr(addr);
            pubAddr.setStatus(CODE.SUCCESS);
        } catch (Exception e) {
            logger.error("getWalletPubAddr: " + e.toString());
        }
        return pubAddr;
    }

    public int withDraw(String sessionKey, WithDrawData withDrawData) {
        try {
            User user = userDao.getBySessionKey(sessionKey);
            String txid = walletService.transferWallet(user.getWallet(), withDrawData.getAddr(),
                    withDrawData.getAmount());
            if (StringUtils.isEmpty(txid)) {
                return CODE.USER_WITHDRAW_FAIL;
            }
        } catch (Exception e) {
            logger.error("withDraw: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
        return CODE.SUCCESS;
    }

    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (Exception e) {
            logger.error("getAllUsers: " + e.toString());
        }
        return null;
    }
}
