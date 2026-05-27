package com.gestaofinanceira.model;

public class InvestmentEvolution {
    private int period;
    private double initialBalance;
    private double contribution;
    private double interestEarned;
    private double finalBalance;
    private double cumulativeInterest;
    private double totalInvested;

    public InvestmentEvolution(int period, double initialBalance, double contribution, 
                               double interestEarned, double finalBalance, 
                               double cumulativeInterest, double totalInvested) {
        this.period = period;
        this.initialBalance = initialBalance;
        this.contribution = contribution;
        this.interestEarned = interestEarned;
        this.finalBalance = finalBalance;
        this.cumulativeInterest = cumulativeInterest;
        this.totalInvested = totalInvested;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public double getContribution() {
        return contribution;
    }

    public void setContribution(double contribution) {
        this.contribution = contribution;
    }

    public double getInterestEarned() {
        return interestEarned;
    }

    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }

    public double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public double getCumulativeInterest() {
        return cumulativeInterest;
    }

    public void setCumulativeInterest(double cumulativeInterest) {
        this.cumulativeInterest = cumulativeInterest;
    }

    public double getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(double totalInvested) {
        this.totalInvested = totalInvested;
    }
}
