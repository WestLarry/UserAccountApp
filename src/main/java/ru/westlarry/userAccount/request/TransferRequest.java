package ru.westlarry.userAccount.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class TransferRequest {

    @NotNull
    private Long toUserId;

    @NotNull
    @Positive
    private Double amount;

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
