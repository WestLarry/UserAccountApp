package ru.westlarry.userAccount.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.westlarry.userAccount.exception.InsufficientFundsException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.request.TransferRequest;
import ru.westlarry.userAccount.security.UserDetailsImpl;
import ru.westlarry.userAccount.service.BalanceService;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/balance")
@Api(tags = {"Operations"})
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/transfer")
    @ApiOperation(value = "Операция трансфера денег (со счета текущего пользователя на счет другого пользователя)")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest transferRequest) throws UserNotFoundException, InsufficientFundsException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        balanceService.transfer(userId, transferRequest.getToUserId(), transferRequest.getAmount());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
