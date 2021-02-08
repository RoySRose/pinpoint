package com.navercorp.pinpoint.api.bo;

import java.util.List;

public class AggregatedTransaction {

    private String transactionIdCreated;
    private List<Long> labeledRpc;
    private long startTime;
    private long endTime;
    private long elapsed;
    private int errCode;

    private long slow;
    private long error;
    private long rolledSlowVal;
    private long rolledErrorVal;

    public AggregatedTransaction(String transactionIdCreated, List<Long> labeledRpc, long startTime, long endTime, long elapsed) {
        this.transactionIdCreated = transactionIdCreated;
        this.labeledRpc = labeledRpc;
        this.startTime = startTime;
        this.endTime = endTime;
        this.elapsed = elapsed;
    }

    public String getTransactionIdCreated() {
        return transactionIdCreated;
    }

    public void setTransactionIdCreated(String transactionIdCreated) {
        this.transactionIdCreated = transactionIdCreated;
    }

    public List<Long> getLabeledRpc() {
        return labeledRpc;
    }

    public void setLabeledRpc(List<Long> labeledRpc) {
        this.labeledRpc = labeledRpc;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public long getSlow() {
        return slow;
    }

    public void setSlow(long slow) {
        this.slow = slow;
    }

    public long getError() {
        return error;
    }

    public void setError(long error) {
        this.error = error;
    }

    public long getRolledSlowVal() {
        return rolledSlowVal;
    }

    public void setRolledSlowVal(long rolledSlowVal) {
        this.rolledSlowVal = rolledSlowVal;
    }

    public long getRolledErrorVal() {
        return rolledErrorVal;
    }

    public void setRolledErrorVal(long rolledErrorVal) {
        this.rolledErrorVal = rolledErrorVal;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    @Override
    public String toString() {
        return "AggregatedTransaction{" +
                "transactionIdCreated='" + transactionIdCreated + '\'' +
                ", labeledRpc=" + labeledRpc +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", elapsed=" + elapsed +
                ", errCode=" + errCode +
                ", slow=" + slow +
                ", error=" + error +
                ", rolledSlowVal=" + rolledSlowVal +
                ", rolledErrorVal=" + rolledErrorVal +
                '}';
    }
}
