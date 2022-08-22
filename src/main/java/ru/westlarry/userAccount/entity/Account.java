package ru.westlarry.userAccount.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "account", indexes = {
        @Index(name = "IDX_ACCOUNT_USER", columnList = "USER_ID")
})
public class Account {

    @Column(name = "USER_ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero
    @Column(name = "BALANCE", nullable = false, precision = 19, scale = 2)
    @NotNull
    private BigDecimal balance;

    @NotNull
    @MapsId
    @JoinColumn(name = "USER_ID", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean allowUpdateBalance(BigDecimal initBalance, int updateOnPercent, int limitBalancePercent) {
        BigDecimal multipliedBalance = balance.multiply(BigDecimal.valueOf(1 + ((double) updateOnPercent / 100))).
                setScale(2, RoundingMode.HALF_UP); // увеличенное на X процентов значение
        BigDecimal limitBalance = initBalance.multiply(BigDecimal.valueOf((double) limitBalancePercent / 100)).
                setScale(2, RoundingMode.HALF_UP); // лимит в Y процентов от начального баланса
        return multipliedBalance.compareTo(limitBalance) < 0;
    }

    public boolean allowTransfer(double amount) {
        BigDecimal subtractBalance = balance.subtract(BigDecimal.valueOf(amount)).setScale(2, RoundingMode.HALF_UP);
        return subtractBalance.signum() >= 0;
    }

    public void updateBalance(int updateOnPercent) {
        setBalance(balance.multiply(BigDecimal.valueOf(1 + ((double) updateOnPercent / 100))).setScale(2, RoundingMode.HALF_UP)); // увеличенное на 10 процентов значение
    }

    public void addAmount(double amount) {
        setBalance(balance.add(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)));
    }

    public void subtractAmount(double amount) {
        setBalance(balance.subtract(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)));
    }
}
