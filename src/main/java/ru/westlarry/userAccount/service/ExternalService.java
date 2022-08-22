package ru.westlarry.userAccount.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ExternalService {

    @Cacheable(value = "initBalanceCache")
    public BigDecimal getInitBalanceForAccount(Long accountId) {
        // предполагается что информация о начальных балансах также как о пользователях можно получить из какой-то внешней системы
        // кеш заполняется значениями из этого источника
        if (accountId.equals(1L)) {
            return BigDecimal.valueOf(100.00);
        } else if (accountId.equals(2L)) {
            return BigDecimal.valueOf(201.00);
        }
        return null;
    }
}
