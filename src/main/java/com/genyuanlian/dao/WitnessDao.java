package com.genyuanlian.dao;

import com.genyuanlian.pojo.Witness;

import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/25.
 */
public interface WitnessDao {

    Witness findById(int id);
    Witness findBySessionKeyAndPlanId(Map map);
    List<Witness> findBySessionKey(String sessionKey);
    List<Witness> findByPlanId(int planId);
    int insertWitness(Witness witness);
    int confirmWitness(Map map);
    int judgeWitness(Map map);
    int updateWitness(Map map);
    int deleteWitness(int planId);
    int deleteWitnesses(Map map);
}
