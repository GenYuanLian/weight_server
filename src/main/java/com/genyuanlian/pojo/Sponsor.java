package com.genyuanlian.pojo;

/**
 * Created by xinruichao on 2018/5/20.
 */
public class Sponsor {

    private int id;

    private int planId;

    private String username;

    private String sessionKey;

    private float sponsorShip;

    private int confirm;  // 0-未确认，1-同意

    private String confirmTime;

    private int active;  //0-被邀请的，1-主动发起的

    private Plan plan;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
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

    public float getSponsorShip() {
        return sponsorShip;
    }

    public void setSponsorShip(float sponsorShip) {
        this.sponsorShip = sponsorShip;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public String getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(String confirmTime) {
        this.confirmTime = confirmTime;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
