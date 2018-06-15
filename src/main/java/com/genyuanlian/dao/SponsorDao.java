package com.genyuanlian.dao;

import com.genyuanlian.pojo.Sponsor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/20.
 */
@Repository
public interface SponsorDao {
    Sponsor findById(int id);
    Sponsor findBySessionKeyAndPlanId(Map map);
    List<Sponsor> findBySessionKey(String sessionKey);
    List<Sponsor> findByPlanId(int planId);
    int insertSponsor(Sponsor sponsor);
    int updateSponsor(Map map);
    int confirmSponsor(Map map);
    int deleteSponsor(int planId);
    int deleteSponsors(Map map);
}
