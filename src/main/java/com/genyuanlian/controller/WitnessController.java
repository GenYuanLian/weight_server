package com.genyuanlian.controller;

import com.genyuanlian.pojo.Witness;
import com.genyuanlian.service.WitnessService;
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
 * Created by xinruichao on 2018/5/25.
 */
@RestController
public class WitnessController {

    private Logger logger = Logger.getLogger(WitnessController.class);

    @Autowired
    private WitnessService witnessService;

    @RequestMapping(value = "/getWitnesses")
    @ResponseBody
    public List<Witness> getWitnesses(HttpServletRequest request) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        logger.debug("Session-Key:" + sessionKey);
        return witnessService.findBySessionKey(sessionKey);
    }

    @RequestMapping(value = "/confirmWitness")
    @ResponseBody
    public int confirmWitness(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        int confirm = Integer.parseInt(request.getParameter("confirm"));
        Map map = new HashMap<>();
        map.put("id", id);
        map.put("confirm", confirm);
        map.put("confirmTime", DateUtil.formatDate(new Date()));
        return witnessService.confirmWitness(map);
    }

    @RequestMapping(value = "/judgeWitness")
    @ResponseBody
    public int judgeWitness(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        int judge = Integer.parseInt(request.getParameter("judge"));
        Map map = new HashMap<>();
        map.put("id", id);
        map.put("judge", judge);
        map.put("judgeTime", DateUtil.formatDate(new Date()));
        return witnessService.judgeWitness(map);
    }

}
