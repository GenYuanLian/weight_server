package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.constant.Constant;
import com.genyuanlian.dao.SponsorDao;
import com.genyuanlian.dao.PlanDao;
import com.genyuanlian.dao.UserDao;
import com.genyuanlian.dao.WitnessDao;
import com.genyuanlian.pojo.*;
import com.genyuanlian.utils.DateUtil;
import org.apache.catalina.authenticator.SpnegoAuthenticator;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * Created by xinruichao on 2018/5/15.
 */
@Service
public class PlanService {

    private Logger logger = Logger.getLogger(PlanService.class);

    @Autowired
    private PlanDao planDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private SponsorDao sponsorDao;

    @Autowired
    private WitnessDao witnessDao;

    @Autowired
    private WalletService walletService;

    /**
     * 根据sessionKey查找该微信号的提交记录
     * @param sessionKey
     * @return
     */
    public List<Plan> findBySessionKey(String sessionKey) {
        List<Plan> plans = planDao.findBySessionKey(sessionKey);

        for (Plan plan : plans) {
            plan.setJoin(0);
            plan.setSponsors(sponsorDao.findByPlanId(plan.getId()));
            plan.setWitnesses(witnessDao.findByPlanId(plan.getId()));
        }

        List<Plan> planList = new ArrayList<>();
        List<Plan> notPlans = planDao.findByNotSessionKey(sessionKey);
        for (Plan plan : notPlans) {
            if (plan.getSubmit() == 1 || plan.getFinish() == 1) {
                continue;
            }
            boolean exist = false;
            List<Sponsor> sponsors = sponsorDao.findByPlanId(plan.getId());
            for (Sponsor sponsor : sponsors) {
                if (sessionKey.equals(sponsor.getSessionKey())) {
                    exist = true;
                    break;
                }
            }
            List<Witness> witnesses = new ArrayList<>();
            if (!exist) {
                witnesses = witnessDao.findByPlanId(plan.getId());
                for (Witness witness : witnesses) {
                    if (sessionKey.equals(witness.getSessionKey())) {
                        exist = true;
                        break;
                    }
                }
            }
            if (!exist) {
                plan.setJoin(1);
                plan.setSponsors(sponsors);
                plan.setWitnesses(witnesses);
                planList.add(plan);
            }
        }

        plans.addAll(planList);

        return plans;
    }

    /**
     * 新增计划
     * @param plan
     * @return
     */
    public PlanData insertPlan(Plan plan) {
        PlanData data = new PlanData();
        try {
            List<Sponsor> sponsors = plan.getSponsors();
            List<Witness> witnesses = plan.getWitnesses();

            // 查找用户是否存在
            String message = "以下用户不存在:\r\n";
            boolean isOk = true;
            User user;
            for (Sponsor sponsor : sponsors) {
                user = userDao.getByUserName(sponsor.getUsername());
                if (user == null) {
                    isOk = false;
                    message += sponsor.getUsername() + "\r\n";
                }
            }
            for (Witness witness : witnesses) {
                user = userDao.getByUserName(witness.getUsername());
                if (user == null) {
                    isOk = false;
                    message += witness.getUsername() + "\r\n";
                }
            }
            if (!isOk) {
                data.setMessage(message);
                data.setStatus(CODE.USERNAME_NOT_ERROR);
                return data;
            }

            // 查询执行人的余额是否足够，需要是大于违约金加上0.2BSTK
            // 查询赞助人的余额是否足够，需要是大于赞助金加上0.1BSTK
            message = "以下用户余额不足:\r\n";
            isOk = true;
            WalletBalance balance = walletService.getWalletBalanceBySessionKey(plan.getSessionKey());
            if (balance.getTotalBalance() < plan.getFine() + 0.2) {
                isOk = false;
                message += userDao.getBySessionKey(plan.getSessionKey()).getUsername() + "\r\n";
            }
            for (Sponsor sponsor : sponsors) {
                user = userDao.getByUserName(sponsor.getUsername());
                balance = walletService.getWalletBalanceBySessionKey(user.getSessionKey());
                if (balance.getTotalBalance() < sponsor.getSponsorShip() + 0.1) {
                    isOk = false;
                    message += sponsor.getUsername() + "\r\n";
                }
            }
            if (!isOk) {
                data.setMessage(message);
                data.setStatus(CODE.BALANCE_NOT_ENOUGH);
                return data;
            }

            plan.setCreateTime(DateUtil.formatDate(new Date()));
            planDao.insertPlan(plan);
            int id = plan.getId();

            // 插入sponsor表
            for (Sponsor sponsor : sponsors) {
                sponsor.setPlanId(id);
                user = userDao.getByUserName(sponsor.getUsername());
                sponsor.setSessionKey(user.getSessionKey());
                if (sponsor.getPlanId() != 0) {
                    sponsorDao.insertSponsor(sponsor);
                }
            }

            // 插入witness表
            for (Witness witness : witnesses) {
                witness.setPlanId(id);
                user = userDao.getByUserName(witness.getUsername());
                witness.setSessionKey(user.getSessionKey());
                if (witness.getPlanId() != 0) {
                    witnessDao.insertWitness(witness);
                }
            }

            data.setId(id);
            data.setStatus(CODE.SUCCESS);
        } catch (Exception e) {
            logger.error("insertPlan: " + e.toString());
            data.setStatus(CODE.SYSTEM_ERROR);
        }
        return data;
    }

    /**
     * 修改计划
     * @param plan
     * @return
     */
    public PlanData updatePlan(Plan plan) {
        PlanData data = new PlanData();
        try {
            List<Sponsor> sponsors = plan.getSponsors();
            List<Witness> witnesses = plan.getWitnesses();

            List<Sponsor> oldSponsors = sponsorDao.findByPlanId(plan.getId());
            List<Witness> oldWitnesses = witnessDao.findByPlanId(plan.getId());

            // 查找用户是否存在
            String message = "以下用户不存在:\r\n";
            boolean isOk = true;
            User user;
            for (Sponsor sponsor : sponsors) {
                user = userDao.getByUserName(sponsor.getUsername());
                if (user == null) {
                    isOk = false;
                    message += sponsor.getUsername() + "\r\n";
                }
            }
            for (Witness witness : witnesses) {
                user = userDao.getByUserName(witness.getUsername());
                if (user == null) {
                    isOk = false;
                    message += witness.getUsername() + "\r\n";
                }
            }
            if (!isOk) {
                data.setMessage(message);
                data.setStatus(CODE.USERNAME_NOT_ERROR);
                return data;
            }

            // 查询执行人的余额是否足够，需要是大于违约金加上0.02BSTK
            // 查询赞助人的余额是否足够，需要是大于赞助金加上0.01BSTK
            message = "以下用户余额不足:\r\n";
            isOk = true;
            WalletBalance balance = walletService.getWalletBalanceBySessionKey(plan.getSessionKey());
            if (balance.getTotalBalance() < plan.getFine() + 0.02) {
                isOk = false;
                message += userDao.getBySessionKey(plan.getSessionKey()).getUsername() + "\r\n";
            }
            for (Sponsor sponsor : sponsors) {
                user = userDao.getByUserName(sponsor.getUsername());
                balance = walletService.getWalletBalanceBySessionKey(user.getSessionKey());
                if (balance.getTotalBalance() < sponsor.getSponsorShip() + 0.01) {
                    isOk = false;
                    message += sponsor.getUsername() + "\r\n";
                }
            }
            if (!isOk) {
                data.setMessage(message);
                data.setStatus(CODE.BALANCE_NOT_ENOUGH);
                return data;
            }

            planDao.updatePlan(plan);

            for (Sponsor sponsor : sponsors) {
                Sponsor sponsor1 = sponsorDao.findById(sponsor.getId());
                if (sponsor1 != null) {
                    Map map = new HashMap<>();
                    map.put("id", sponsor.getId());
                    map.put("username", sponsor.getUsername());
                    user = userDao.getByUserName(sponsor.getUsername());
                    map.put("sessionKey", user.getSessionKey());
                    map.put("sponsorShip", sponsor.getSponsorShip());
                    sponsorDao.updateSponsor(map);
                } else {
                    sponsor.setPlanId(plan.getId());
                    user = userDao.getByUserName(sponsor.getUsername());
                    sponsor.setSessionKey(user.getSessionKey());
                    if (sponsor.getPlanId() != 0) {
                        sponsorDao.insertSponsor(sponsor);
                    }
                }
            }

            List<Sponsor> delSponsors = new ArrayList<>();
            for (Sponsor oldSponsor: oldSponsors) {
                boolean exist = false;
                for (Sponsor sponsor : sponsors) {
                    if (oldSponsor.getId() == sponsor.getId()) {
                        exist = true;
                        break;
                    }
                }
                if (!exist && oldSponsor.getConfirm()==0) {
                    delSponsors.add(oldSponsor);
                }
            }

            for (Sponsor sponsor : delSponsors) {
                Map map = new HashMap<>();
                map.put("planId", plan.getId());
                map.put("username", sponsor.getUsername());
                sponsorDao.deleteSponsors(map);
            }


            for (Witness witness : witnesses) {
                Witness witness1 = witnessDao.findById(witness.getId());
                if (witness1 != null) {
                    Map map = new HashMap<>();
                    map.put("id", witness.getId());
                    map.put("username", witness.getUsername());
                    user = userDao.getByUserName(witness.getUsername());
                    map.put("sessionKey", user.getSessionKey());
                    witnessDao.updateWitness(map);
                } else {
                    witness.setPlanId(plan.getId());
                    user = userDao.getByUserName(witness.getUsername());
                    witness.setSessionKey(user.getSessionKey());
                    if (witness.getPlanId() != 0) {
                        witnessDao.insertWitness(witness);
                    }
                }
            }


            List<Witness> delWitnesses = new ArrayList<>();
            for (Witness oldWitness: oldWitnesses) {
                boolean exist = false;
                for (Witness witness : witnesses) {
                    if (oldWitness.getId() == witness.getId()) {
                        exist = true;
                        break;
                    }
                }
                if (!exist && oldWitness.getConfirm()==0) {
                    delWitnesses.add(oldWitness);
                }
            }

            for (Witness witness : delWitnesses) {
                Map map = new HashMap<>();
                map.put("planId", plan.getId());
                map.put("username", witness.getUsername());
                witnessDao.deleteWitnesses(map);
            }

            data.setStatus(CODE.SUCCESS);

        } catch (Exception e) {
            logger.error("updatePlan: " + e.toString());
            data.setStatus(CODE.SYSTEM_ERROR);
        }
        return data;
    }

    /**
     * 提交计划
     * @param id
     * @return
     */
    public PlanData submitPlan(int id) {
        PlanData resData = new PlanData();
        try {
            boolean isOk;
            String message;
            Plan plan = planDao.findById(id);

            // 计划是否提交验证数据
            if (plan.getVerify() == 0) {
                resData.setStatus(CODE.PLAN_NOT_VERIFY);
                message = "计划没有提交验证数据";
                resData.setMessage(message);
                return resData;
            }

            // 计划是否还有赞助人没有确认
            isOk = true;
            List<Sponsor> sponsors = sponsorDao.findByPlanId(id);
            for (Sponsor sponsor : sponsors) {
                if (sponsor.getConfirm() == 0) {
                    isOk = false;
                    message = "计划还有赞助人没有确认";
                    resData.setMessage(message);
                    resData.setStatus(CODE.PLAN_SPONSOR_NOT_CONFIRM);
                    break;
                }
            }
            if (!isOk) {
                return resData;
            }

            // 计划是否还有见证人没有确认
            isOk = true;
            List<Witness> witnesses = witnessDao.findByPlanId(id);
            for (Witness witness : witnesses) {
                if (witness.getConfirm() == 0) {
                    isOk = false;
                    message = "计划还有见证人没有确认";
                    resData.setMessage(message);
                    resData.setStatus(CODE.PLAN_WITNESS_NOT_CONFIRM);
                    break;
                }
            }
            if (!isOk) {
                return resData;
            }

            // 计划是否还有见证人没有给出评判结果
            isOk = true;
            for (Witness witness : witnesses) {
                if (witness.getJudge() == 0) {
                    isOk = false;
                    message = "计划还有见证人没有给出评判结果";
                    resData.setMessage(message);
                    resData.setStatus(CODE.PLAN_WITNESS_NOT_JUDGE);
                    break;
                }
            }
            if (!isOk) {
                return resData;
            }

            // 给节点服务的主账户转账
            // 节点服务的主账户用来做数据摘要的上线操作，即发送OP_RETURN数据上链
            User user = userDao.getBySessionKey(plan.getSessionKey());
            String mainAccountTxid = walletService.transferWallet(user.getWallet(), Constant.MAIN_WALLET_ADDR, 0.1f);

            String submitTime = DateUtil.formatDate(new Date());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", plan.getId());
            jsonObject.put("username", plan.getUsername());
            jsonObject.put("intro", plan.getIntro());
            jsonObject.put("idx", plan.getIdx());
            jsonObject.put("fine", plan.getFine());
            jsonObject.put("createTime", plan.getCreateTime());
            jsonObject.put("updateTime", plan.getUpdateTime());
            jsonObject.put("verify", plan.getVerify());
            jsonObject.put("verifyIntro", plan.getVerifyIntro());
            jsonObject.put("verifyPicPath", plan.getVerifyPicPath());
            jsonObject.put("verifyTime", plan.getVerifyTime());
            jsonObject.put("submit", 1);
            jsonObject.put("submitTime", submitTime);
            JSONArray jsonArray = new JSONArray();
            for (Sponsor sponsor : sponsors) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("planId", sponsor.getPlanId());
                jsonObject1.put("username", sponsor.getUsername());
                jsonObject1.put("sponsorShip", sponsor.getSponsorShip());
                jsonObject1.put("confirm", sponsor.getConfirm());
                jsonObject1.put("confirmTime", sponsor.getConfirmTime());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("sponsors", jsonArray);
            jsonArray = new JSONArray();
            for (Witness witness : witnesses) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("planId", witness.getPlanId());
                jsonObject1.put("username", witness.getUsername());
                jsonObject1.put("confirm", witness.getConfirm());
                jsonObject1.put("confirmTime", witness.getConfirmTime());
                jsonObject1.put("judge", witness.getJudge());
                jsonObject1.put("judgeTime", witness.getJudgeTime());
                jsonArray.put(jsonObject1);
            }
            jsonObject.put("witnesses", jsonArray);
            String rawData = jsonObject.toString();
            String summaryData = "0x"+DigestUtils.md5Hex(rawData);
            String submitTxid = storeOpReturnForPlan(summaryData);

            Map map = new HashMap<>();
            map.put("id", plan.getId());
            map.put("submitTxid", submitTxid);
            map.put("mainAccountTxid", mainAccountTxid);
            map.put("submitTime", submitTime);
            map.put("rawData", rawData);
            map.put("summaryData", summaryData);
            planDao.submitPlan(map);

            // 判断计划结果
            int judge1 = 0;
            int judge2 = 0;
            for (Witness witness : witnesses) {
                if (witness.getJudge() == 1) {
                    judge1++;
                } else if (witness.getJudge() == 2) {
                    judge2++;
                }
            }

            if (judge1 > judge2) {
                /* 计划通过
                   赞助人给执行人赞助金
                   赞助人拿出赞助金的千分之10到千分之50给见证人
                   见证人对计划评判通过的对赞助金进行评分
                   见证人对计划评判不通过的不分得赞助金
                 */
                // 给见证人赞助金
                float num;
                Random random = new Random();
                float percent = (10 + random.nextInt(40)) / 1000.0f;
                for (Sponsor sponsor : sponsors) {
                    num = (sponsor.getSponsorShip() * percent) / judge1;
                    user = userDao.getBySessionKey(sponsor.getSessionKey());
                    for (Witness witness : witnesses) {
                        if (witness.getJudge() == 1) {
                            User user1 = userDao.getBySessionKey(witness.getSessionKey());
                            walletService.transferWallet(user.getWallet(), user1.getPubAddr(), num);
                        }
                    }
                }

                // 给执行人赞助金
                for (Sponsor sponsor : sponsors) {
                    num = sponsor.getSponsorShip() * (1.0f - percent);
                    user = userDao.getBySessionKey(sponsor.getSessionKey());
                    User user1 = userDao.getBySessionKey(plan.getSessionKey());
                    walletService.transferWallet(user.getWallet(), user1.getPubAddr(), num);
                }
            } else if (judge1 < judge2){
                /* 计划不通过
                   执行人给赞助人违约金
                   执行人拿出违约金的千分之10到千分之50给见证人
                   见证人对计划评判不通过的对违约金进行平分
                   见证人对计划评判通过的不分得违约金
                 */
                // 给见证人违约金
                float num;
                Random random = new Random();
                float percent = (10 + random.nextInt(40)) / 1000.0f;
                num = (plan.getFine() * percent) / judge2;
                user = userDao.getBySessionKey(plan.getSessionKey());
                for (Witness witness : witnesses) {
                    if (witness.getJudge() == 0) {
                        User user1 = userDao.getBySessionKey(witness.getSessionKey());
                        walletService.transferWallet(user.getWallet(), user1.getPubAddr(), num);
                    }
                }

                // 给赞助人违约金
                num = (plan.getFine() * (1.0f - percent)) / sponsors.size();
                for (Sponsor sponsor : sponsors) {
                    User user1 = userDao.getBySessionKey(sponsor.getSessionKey());
                    walletService.transferWallet(user.getWallet(), user1.getPubAddr(), num);
                }
            } else {
                /*
                 * 计划通过和不通过数相等，没有得出哪方获胜结果
                 * 不进行资金的转移操作
                 */
            }

            resData.setStatus(CODE.SUCCESS);
        } catch (Exception e) {
            logger.error("submitPlan: " + e.toString());
            resData.setStatus(CODE.SYSTEM_ERROR);
        }
        return resData;
    }

    /**
     * 删除计划
     * @param id
     * @return
     */
    public int deletePlan(int id) {
        try {
            List<Sponsor> sponsors = sponsorDao.findByPlanId(id);
            for (Sponsor sponsor : sponsors) {
                if (sponsor.getConfirm() == 1) {
                    return CODE.PLAN_SPONSOR_CONFIRMED;
                }
            }

            planDao.deletePlan(id);
            sponsorDao.deleteSponsor(id);
            witnessDao.deleteWitness(id);
            return CODE.SUCCESS;
        } catch (Exception e) {
            logger.error("deletePlan: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
    }

    public int uploadVerifyPic(int id, String verifyIntro, MultipartFile file, String uploadPath) {
        List<Witness> witnesses = witnessDao.findByPlanId(id);
        for (Witness witness : witnesses) {
            if (witness.getJudge() != 0) {
                return CODE.PLAN_WITNESS_JUDGED;
            }
        }

        //多文件上传
        if(file != null) {
            try {
                String fileName = file.getOriginalFilename();
                //判断是否有文件(实际生产中要判断是否是音频文件)
                if(StringUtils.isNoneBlank(fileName)) {
                    //创建输出文件对象
                    String picPath = uploadPath + "/" + id + ".jpg";
                    File outFile = new File(picPath);
                    //拷贝文件到输出文件对象
                    FileUtils.copyInputStreamToFile(file.getInputStream(), outFile);

                    Map map = new HashMap<>();
                    map.put("id", id);
                    map.put("verifyIntro", verifyIntro);
                    map.put("verifyPicPath", picPath);
                    map.put("verifyTime", DateUtil.formatDate(new Date()));
                    planDao.updateVerifyPic(map);
                }
            } catch (Exception e) {
                logger.error("updateVerifyPic: " + e.toString());
                return CODE.SYSTEM_ERROR;
            }
        }
        return CODE.SUCCESS;
    }

    public ResponseEntity<InputStreamResource> downloadVerifyPic(int id) {
        try {
            Plan plan = planDao.findById(id);
            FileSystemResource file = new FileSystemResource(plan.getVerifyPicPath());
            logger.info("fileName="+file.getFilename());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(file.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(file.getInputStream()));
        } catch (Exception e) {
            logger.error("downloadVerifyPic: " + e.toString());
            return null;
        }
    }

    public String storeOpReturnForPlan(String data) {
        // write your code here
        String txid = null;
        BufferedReader in = null;
        try {
            //需传入的参数
            String[] params = new String[] { "python", Constant.OP_RETURN_PATH, data };
            Process pr = Runtime.getRuntime().exec(params);

            in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.contains(line, "TxIDs")) {
                    txid = in.readLine();
                    break;
                }
            }
            pr.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return txid;
    }

    public int joinSponsor(String sessionKey, JoinSponsor joinSponsor) {
        try {
            Sponsor sponsor;
            Map map = new HashMap<>();
            map.put("planId", joinSponsor.getPlanId());
            map.put("sessionKey", sessionKey);
            sponsor = sponsorDao.findBySessionKeyAndPlanId(map);
            if (sponsor != null) {
                return CODE.PLAN_SPONSOR_EXIST;
            }
            Witness witness = witnessDao.findBySessionKeyAndPlanId(map);
            if (witness != null) {
                return CODE.PLAN_WITNESS_EXIST;
            }
            sponsor = new Sponsor();
            sponsor.setPlanId(joinSponsor.getPlanId());
            sponsor.setSessionKey(sessionKey);
            sponsor.setSponsorShip(joinSponsor.getSponsorShip());
            User user = userDao.getBySessionKey(sessionKey);
            sponsor.setUsername(user.getUsername());
            sponsor.setActive(1);
            sponsorDao.insertSponsor(sponsor);
        } catch (Exception e) {
            logger.equals("joinSponsor: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
        return CODE.SUCCESS;
    }

    public int joinWitness(String sessionKey, JoinWitness joinWitness) {
        try {
            Witness witness;
            Map map = new HashMap<>();
            map.put("planId", joinWitness.getPlanId());
            map.put("sessionKey", sessionKey);
            witness = witnessDao.findBySessionKeyAndPlanId(map);
            if (witness != null) {
                return CODE.PLAN_WITNESS_EXIST;
            }
            Sponsor sponsor = sponsorDao.findBySessionKeyAndPlanId(map);
            if (sponsor != null) {
                return CODE.PLAN_SPONSOR_EXIST;
            }
            witness = new Witness();
            witness.setPlanId(joinWitness.getPlanId());
            witness.setSessionKey(sessionKey);
            User user = userDao.getBySessionKey(sessionKey);
            witness.setUsername(user.getUsername());
            witness.setActive(1);
            witnessDao.insertWitness(witness);
        } catch (Exception e) {
            logger.equals("joinWitness: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
        return CODE.SUCCESS;
    }

}
