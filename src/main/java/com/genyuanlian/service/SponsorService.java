package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.SponsorDao;
import com.genyuanlian.dao.PlanDao;
import com.genyuanlian.dao.WitnessDao;
import com.genyuanlian.pojo.Sponsor;
import com.genyuanlian.pojo.Plan;
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
public class SponsorService {

    private Logger logger = Logger.getLogger(SponsorService.class);

    @Autowired
    private SponsorDao sponsorDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private WitnessDao witnessDao;

    /**
     * 根据sessionKey查找该微信号的提交记录
     * @param sessionKey
     * @return
     */
    public List<Sponsor> findBySessionKey(String sessionKey) {
        List<Sponsor> sponsorList = new ArrayList<>();
        try {
            List<Sponsor> sponsors = sponsorDao.findBySessionKey(sessionKey);
            for (Sponsor sponsor : sponsors) {
                Plan plan = planDao.findById(sponsor.getPlanId());
                plan.setSponsors(sponsorDao.findByPlanId(plan.getId()));
                plan.setWitnesses(witnessDao.findByPlanId(plan.getId()));
                sponsor.setPlan(plan);
                sponsorList.add(sponsor);
            }
        } catch (Exception e) {
            logger.error("findBySessionKey: " + e.toString());
        }
        return sponsorList;
    }

    /**
     * 确认赞助
     * @param map
     * @return
     */
    public int confirmSponsor(Map map) {
        try {
            sponsorDao.confirmSponsor(map);
            return CODE.SUCCESS;
        } catch (Exception e) {
            logger.error("confirmSponsor: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
    }

}
