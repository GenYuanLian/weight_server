package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.UserDao;
import com.genyuanlian.pojo.User;
import com.genyuanlian.pojo.WalletBalance;
import com.genyuanlian.utils.UHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/18.
 */
@Service
public class WalletService {

    private Logger logger = Logger.getLogger(WalletService.class);

    private final static String WALLET_CREATE_URL = "http://service.genyuanlian.com/api/wallet/create";

    private final static String WALLET_BALANCE_URL = "http://service.genyuanlian.com/api/wallet/balance";

    private final static String WALLET_TRANSFER_URL = "http://service.genyuanlian.com/api/wallet/transfer";

    @Autowired
    private UserDao userDao;

    public int createWalletByUserName(String userName) {
        try {
            User user = userDao.getByUserName(userName);
            if (user == null) {
                logger.info("用户不存在");
                return CODE.SYSTEM_ERROR;
            }
            if (StringUtils.isEmpty(user.getWallet()) || StringUtils.isEmpty(user.getPubAddr())) {
                // 创建钱包
                UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.post, WALLET_CREATE_URL, null, "UTF-8", true);
                JSONObject jsonObject = new JSONObject(res.content);
                boolean isOk = (boolean)jsonObject.get("isOk");
                if (isOk) {
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    String wallet = (String)dataObject.get("wallet");
                    String pubAddr = (String)dataObject.get("mainAddr");
                    user.setWallet(wallet);
                    user.setPubAddr(pubAddr);
                    userDao.updateByPrimaryKey(user);
                }
            }
            return CODE.SUCCESS;
        } catch (Exception e) {
            logger.error("createWallet: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
    }

    public WalletBalance getWalletBalanceBySessionKey(String sessionKey) {
        WalletBalance resData = new WalletBalance();
        resData.setStatus(CODE.SYSTEM_ERROR);
        try {
            User user = userDao.getBySessionKey(sessionKey);
            if (user == null) {
                logger.info("用户不存在");
                return resData;
            }
            if (StringUtils.isEmpty(user.getWallet()) || StringUtils.isEmpty(user.getPubAddr())) {
                // 还没有钱包，先创建钱包
                if (createWalletByUserName(user.getUsername()) == CODE.SYSTEM_ERROR) {
                    return resData;
                }
                user = userDao.getBySessionKey(sessionKey);
            }
            // 查询余额
            Map<String, Object> params = new HashMap<>();
            params.put("wallet", user.getWallet());
            UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.post, WALLET_BALANCE_URL, params, "UTF-8", true);
            JSONObject jsonObject = new JSONObject(res.content);
            boolean isOk = (boolean)jsonObject.get("isOk");
            if (isOk) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                float totalBalance = (float)dataObject.getInt("totalAmount") / 100000000.0f;
                float availableBalance = (float)dataObject.getInt("availableAmount") / 100000000.0f;
                user.setTotalBalance(totalBalance);
                user.setAvailableBalance(availableBalance);
                userDao.updateByPrimaryKey(user);
                resData.setStatus(CODE.SUCCESS);
                resData.setTotalBalance(totalBalance);
                resData.setAvailableBalance(availableBalance);
            }
        } catch (Exception e) {
            logger.error("getWalletBalance: " + e.toString());
        }
        return resData;
    }

    public String transferWallet(String senderWallet, String receiverAddress, float amount) throws Exception {
        // 查询余额
        Map<String, Object> params = new HashMap<>();
        params.put("senderWallet", senderWallet);
        params.put("receiverAddress", receiverAddress);
        params.put("amount", (int)(amount*1.0e8));
        UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.post, WALLET_TRANSFER_URL, params, "UTF-8", true);
        JSONObject jsonObject = new JSONObject(res.content);
        boolean isOk = (boolean)jsonObject.get("isOk");
        if (isOk) {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            String txid = dataObject.getString("txid");
            return txid;
        }
        return null;
    }

}
