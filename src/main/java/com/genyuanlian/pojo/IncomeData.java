package com.genyuanlian.pojo;

import java.util.List;

/**
 * Created by xinruichao on 2018/6/19.
 */
public class IncomeData {

    private int status;

    private float income;

    private List<Income> incomes;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public List<Income> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }
}
