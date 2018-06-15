package com.genyuanlian.dao;

import com.genyuanlian.pojo.Plan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/15.
 */

@Repository
public interface PlanDao {
    Plan findById(int id);
    List<Plan> findByUserName(String userName);
    List<Plan> findBySessionKey(String sessionKey);
    List<Plan> findByNotSessionKey(String sessionKey);  //找到用户可参与的计划
    List<Plan> findAll();
    int insertPlan(Plan plan);
    int updatePlan(Plan plan);
    int updateVerifyPic(Map map);
    int submitPlan(Map map);
    int finishPlan(int id);
    int deletePlan(int id);
}
