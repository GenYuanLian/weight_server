package com.genyuanlian.controller;

import com.genyuanlian.pojo.Sponsor;
import com.genyuanlian.service.SponsorService;
import com.genyuanlian.utils.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/20.
 */
@RestController
public class SponsorController {

    private Logger logger = Logger.getLogger(SponsorController.class);

    @Autowired
    private SponsorService sponsorService;

    @RequestMapping(value = "/getSponsors")
    @ResponseBody
    public List<Sponsor> getSponsors(HttpServletRequest request) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        logger.debug("Session-Key:" + sessionKey);
        return sponsorService.findBySessionKey(sessionKey);
    }

    @RequestMapping(value = "/confirmSponsor")
    @ResponseBody
    public int confirmSponsor(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        Map map = new HashMap<>();
        map.put("id", id);
        map.put("confirm", 1);
        map.put("confirmTime", DateUtil.formatDate(new Date()));
        return sponsorService.confirmSponsor(map);
    }

}
