package com.genyuanlian.controller;

import ch.qos.logback.core.util.FileUtil;
import com.genyuanlian.pojo.JoinSponsor;
import com.genyuanlian.pojo.JoinWitness;
import com.genyuanlian.pojo.PlanData;
import com.genyuanlian.pojo.Plan;
import com.genyuanlian.service.PlanService;
import com.genyuanlian.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by xinruichao on 2018/5/15.
 */
@RestController
public class PlanController {

    private Logger logger = Logger.getLogger(PlanController.class);

    @Autowired
    private PlanService planService;

    @RequestMapping(value = "/getPlans")
    @ResponseBody
    public List<Plan> getPlans(HttpServletRequest request) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        logger.debug("Session-Key: " + sessionKey);
        return planService.findBySessionKey(sessionKey);
    }

    //查询已绑定的姓名
    @RequestMapping(value = "/insertPlan")
    @ResponseBody
    public PlanData insertPlan(@RequestBody Plan plan) throws Exception {
        return planService.insertPlan(plan);
    }

    @RequestMapping(value = "/updatePlan")
    @ResponseBody
    public PlanData updatePlan(@RequestBody Plan plan) throws Exception{
        plan.setUpdateTime(DateUtil.formatDate(new Date()));
        return planService.updatePlan(plan);
    }

    @RequestMapping(value = "/submitPlan")
    @ResponseBody
    public PlanData submitPlan(HttpServletRequest request) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        return planService.submitPlan(id);
    }

    @RequestMapping(value = "/deletePlan")
    @ResponseBody
    public int deletePlan(HttpServletRequest request) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        return planService.deletePlan(id);
    }

    @Value("${web.upload.path}")
    private String uploadPath;

    @RequestMapping("/uploadVerifyPic")
    @ResponseBody
    public int upload(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file) {
        int id = Integer.parseInt(request.getParameter("id"));
        String verifyIntro = request.getParameter("verifyIntro");
        return planService.uploadVerifyPic(id, verifyIntro, file, uploadPath);
    }

    @RequestMapping(value = "/downloadVerifyPic", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> download(HttpServletRequest request) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        return planService.downloadVerifyPic(id);
    }

    @RequestMapping(value = "/joinSponsor")
    @ResponseBody
    public int joinSponsor(HttpServletRequest request, @RequestBody JoinSponsor joinSponsor) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        return planService.joinSponsor(sessionKey, joinSponsor);
    }

    @RequestMapping(value = "/joinWitness")
    @ResponseBody
    public int joinWitness(HttpServletRequest request, @RequestBody JoinWitness joinWitness) throws Exception {
        String sessionKey = request.getHeader("Session-Key");
        return planService.joinWitness(sessionKey, joinWitness);
    }

}
