package com.genyuanlian.pojo;

/**
 * Created by xinruichao on 2018/5/18.
 */
public class WalletBalance {

    private int status;

    private float totalBalance;

    private float availableBalance;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(float totalBalance) {
        this.totalBalance = totalBalance;
    }

    public float getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(float availableBalance) {
        this.availableBalance = availableBalance;
    }
}
