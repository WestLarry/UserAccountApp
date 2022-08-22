package ru.westlarry.userAccount.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.westlarry.userAccount.entity.Account;
import ru.westlarry.userAccount.exception.InsufficientFundsException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Value("${app.limitBalancePercent}")
    private int limitBalancePercent;

    @Value("${app.updateBalancePercent}")
    private int updateBalancePercent;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //Начинаем новую транзакцию, все вложенные транзакции будут присоединяться к ней
    public void updateBalance(Long accountId, BigDecimal initBalance) {
        Optional<Account> accountOpt = accountRepository.findById(accountId); //пессимистическая блокировка аккаунта
        if (accountOpt.isPresent() && accountOpt.get().allowUpdateBalance(initBalance, updateBalancePercent, limitBalancePercent)) {
            Account account = accountOpt.get();
            BigDecimal oldBalance = account.getBalance();
            account.updateBalance(updateBalancePercent);
            BigDecimal newBalance = account.getBalance();
            accountRepository.save(account); // сохранение происходит в отдельной транзакции
            logger.info(String.format("Account %d - the balance changed from %f to %f", account.getId(), oldBalance, newBalance));
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //Начинаем новую транзакцию, все вложенные транзакции будут присоединяться к ней
    public void transfer(Long fromAccountId, Long toAccountId, double amount) throws UserNotFoundException, InsufficientFundsException {
        Optional<Account> fromAccountOpt = accountRepository.findById(fromAccountId); //пессимистическая блокировка аккаунта
        Optional<Account> toAccountOpt = accountRepository.findById(toAccountId); //пессимистическая блокировка аккаунта
        if (fromAccountOpt.isPresent() && toAccountOpt.isPresent()) {
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();
            if (fromAccount.allowTransfer(amount)) {
                fromAccount.subtractAmount(amount);
                toAccount.addAmount(amount);
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
            } else {
                throw new InsufficientFundsException("Недостаточно средств");
            }
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
