package ru.westlarry.userAccount.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BalanceController {

    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
            balanceService.transfer(userId, transferRequest.getToUserId(), transferRequest.getAmount());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InsufficientFundsException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
