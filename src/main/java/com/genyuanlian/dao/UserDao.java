package com.genyuanlian.dao;

import com.genyuanlian.pojo.LoginRegistData;
import com.genyuanlian.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    List<User> getAllUsers();
    User getByUserName(String username);
    User getBySessionKey(String sessionKey);
    User getByUserNameAndPassword(LoginRegistData loginRegistData);
    String getWalletPubAddr(String sessionKey);
    int updateByPrimaryKey(User user);
    int insert(User user);
}