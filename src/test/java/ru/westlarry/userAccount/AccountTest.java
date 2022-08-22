package ru.westlarry.userAccount;

import org.junit.jupiter.api.Test;
import ru.westlarry.userAccount.entity.Account;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    @Test
    public void testUpdateBalance() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(110.0));

        int limitBalancePercent = 207;
        int updateBalancePercent = 10;

        BigDecimal initBalance1 = new BigDecimal("110.00");
        boolean result = account.allowUpdateBalance(initBalance1, updateBalancePercent, limitBalancePercent);

        assertTrue(result);

        account.updateBalance(updateBalancePercent);
        assertEquals(BigDecimal.valueOf(121).intValue(), account.getBalance().intValue());

        account.setBalance(BigDecimal.valueOf(193.0));

        BigDecimal initBalance2 = new BigDecimal("100.00");
        result = account.allowUpdateBalance(initBalance2,updateBalancePercent, limitBalancePercent);

        assertFalse(result);
    }
}
