package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.*;
import com.genyuanlian.pojo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/20.
 */
@Service
public class IncomeService {

    private Logger logger = Logger.getLogger(IncomeService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private IncomeDao incomeDao;

    /**
     * 根据sessionKey查找该微信号的收益
     * @param sessionKey
     * @return
     */
    public IncomeData findBySessionKey(String sessionKey) {
        IncomeData incomeData = new IncomeData();
        try {
            User user = userDao.getBySessionKey(sessionKey);
            float income = user.getIncome();
            List<Income> incomes = incomeDao.findBySessionKey(sessionKey);
            List<Income> incomeList = new ArrayList<>();
            for (Income income1 : incomes) {
                Plan plan = planDao.findById(income1.getPlanId());
                income1.setIntro(plan.getIntro());
                if (income1.getRole() == 0) {
                    income1.setRoleDesc("执行人");
                } else if (income1.getRole() == 1) {
                    income1.setRoleDesc("赞助人");
                } else if (income1.getRole() == 2) {
                    income1.setRoleDesc("见证人");
                }
                incomeList.add(income1);
            }
            incomeData.setIncome(income);
            incomeData.setIncomes(incomeList);
            incomeData.setStatus(CODE.SUCCESS);
        } catch (Exception e) {
            logger.error("findBySessionKey: " + e.toString());
            incomeData.setStatus(CODE.SYSTEM_ERROR);
        }
        return incomeData;
    }

}
