package com.genyuanlian.pojo;

import java.util.List;

/**
 * Created by xinruichao on 2018/5/15.
 */
public class Plan {

    private int id;

    private int idx;

    private String riqi;  //计划目标达成日期

    private String wriqi;  //计划最终完成日期

    private String intro;

    private String username;

    private String sessionKey;

    private float fine;

    private float witnessFine;

    private int submit;

    private String submitTime;

    private int verify;

    private String verifyIntro;

    private String verifyPicPath;

    private String verifyTime;

    private String submitTxid;

    private String mainAccountTxid;   //提交计划时，给平台账户的转账0.1BSTK，获得的交易ID

    private int finish;

    private int witness;

    private int witnessNum;

    private String witnessRiqi;

    private List<Sponsor> sponsors;

    private List<Witness> witnesses;

    private int validSponsorNum;

    private int validWitnessNum;

    private String createTime;

    private String updateTime;

    private String rawData;

    private String summaryData;

    private int join;  //0-我创建的，1-可参与的

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getRiqi() {
        return riqi;
    }

    public void setRiqi(String riqi) {
        this.riqi = riqi;
    }

    public String getWriqi() {
        return wriqi;
    }

    public void setWriqi(String wriqi) {
        this.wriqi = wriqi;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public float getFine() {
        return fine;
    }

    public void setFine(float fine) {
        this.fine = fine;
    }

    public float getWitnessFine() {
        return witnessFine;
    }

    public void setWitnessFine(float witnessFine) {
        this.witnessFine = witnessFine;
    }

    public int getSubmit() {
        return submit;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public int getVerify() {
        return verify;
    }

    public void setVerify(int verify) {
        this.verify = verify;
    }

    public String getVerifyIntro() {
        return verifyIntro;
    }

    public void setVerifyIntro(String verifyIntro) {
        this.verifyIntro = verifyIntro;
    }

    public String getVerifyPicPath() {
        return verifyPicPath;
    }

    public void setVerifyPicPath(String verifyPicPath) {
        this.verifyPicPath = verifyPicPath;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getSubmitTxid() {
        return submitTxid;
    }

    public void setSubmitTxid(String submitTxid) {
        this.submitTxid = submitTxid;
    }

    public String getMainAccountTxid() {
        return mainAccountTxid;
    }

    public void setMainAccountTxid(String mainAccountTxid) {
        this.mainAccountTxid = mainAccountTxid;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finished) {
        this.finish = finished;
    }

    public int getWitness() {
        return witness;
    }

    public void setWitness(int witness) {
        this.witness = witness;
    }

    public int getWitnessNum() {
        return witnessNum;
    }

    public void setWitnessNum(int witnessNum) {
        this.witnessNum = witnessNum;
    }

    public String getWitnessRiqi() {
        return witnessRiqi;
    }

    public void setWitnessRiqi(String witnessRiqi) {
        this.witnessRiqi = witnessRiqi;
    }

    public List<Sponsor> getSponsors() {
        return sponsors;
    }

    public void setSponsors(List<Sponsor> sponsors) {
        this.sponsors = sponsors;
    }

    public List<Witness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }

    public int getValidSponsorNum() {
        return validSponsorNum;
    }

    public void setValidSponsorNum(int validSponsorNum) {
        this.validSponsorNum = validSponsorNum;
    }

    public int getValidWitnessNum() {
        return validWitnessNum;
    }

    public void setValidWitnessNum(int validWitnessNum) {
        this.validWitnessNum = validWitnessNum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(String summaryData) {
        this.summaryData = summaryData;
    }

    public int getJoin() {
        return join;
    }

    public void setJoin(int join) {
        this.join = join;
    }
}
