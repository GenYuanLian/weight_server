package com.genyuanlian.controller;

import com.genyuanlian.pojo.IncomeData;
import com.genyuanlian.service.IncomeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by xinruichao on 2018/6/19.
 */
@RestController
public class IncomeController {

    private Logger logger = Logger.getLogger(IncomeController.class);

    @Autowired
    private IncomeService incomeService;

    @RequestMapping(value = "/checkIncome")
    @ResponseBody
    public IncomeData getIncomes(HttpServletRequest request) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        logger.debug("Session-Key:" + sessionKey);
        return incomeService.findBySessionKey(sessionKey);
    }

}
