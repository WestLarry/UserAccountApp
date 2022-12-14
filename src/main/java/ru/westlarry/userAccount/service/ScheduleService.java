package ru.westlarry.userAccount.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.westlarry.userAccount.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ScheduleService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ExternalService externalService;

    @Autowired
    private BalanceService balanceService;

    @Scheduled(fixedRateString = "${app.scheduleRateInMsec}", initialDelayString = "${app.scheduleRateInMsec}")
    public void runUpdateBalanceJob() {
        List<Long> accounts = accountRepository.findAllIds();
        accounts.forEach(e -> {
            BigDecimal initBalance = externalService.getInitBalanceForAccount(e);
            if (initBalance != null) {
                balanceService.updateBalance(e, initBalance);
            }
        });
    }
}
