package com.genyuanlian.dao;

import com.genyuanlian.pojo.Income;
import com.genyuanlian.pojo.Sponsor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/20.
 */
@Repository
public interface IncomeDao {
    Income findById(int id);
    Income findBySessionKeyAndPlanId(Map map);
    List<Income> findByUsername(String username);
    List<Income> findBySessionKey(String sessionKey);
    int insertIncome(Income income);
}
