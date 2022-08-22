package ru.westlarry.userAccount.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.westlarry.userAccount.entity.EmailData;
import ru.westlarry.userAccount.entity.PhoneData;
import ru.westlarry.userAccount.entity.User;
import ru.westlarry.userAccount.exception.CommonApiException;
import ru.westlarry.userAccount.exception.NonUniqueException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.request.*;
import ru.westlarry.userAccount.security.UserDetailsImpl;
import ru.westlarry.userAccount.service.UserService;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/email")
    public ResponseEntity<?> addNewEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            userService.addNewEmail(getCurrentUserId(), emailRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NonUniqueException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changeEmail")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody ChangeEmailRequest emailRequest) {
        try {
            userService.changeEmail(getCurrentUserId(), emailRequest.getOldEmail(), emailRequest.getNewEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NonUniqueException | CommonApiException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/email")
    public ResponseEntity<?> deleteEmail(@Valid @RequestBody EmailRequest emailRequest) {
        try {
            userService.deleteEmail(getCurrentUserId(), emailRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException | CommonApiException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/phone")
    public ResponseEntity<?> addNewPhone(@RequestBody PhoneRequest phoneRequest) {
        try {
            userService.addNewPhone(getCurrentUserId(), phoneRequest.getPhone());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NonUniqueException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changePhone")
    public ResponseEntity<?> changePhone(@Valid @RequestBody ChangePhoneRequest phoneRequest) {
        try {
            userService.changePhone(getCurrentUserId(), phoneRequest.getOldPhone(), phoneRequest.getNewPhone());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NonUniqueException | CommonApiException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/phone")
    public ResponseEntity<?> deletePhone(@Valid @RequestBody PhoneRequest phoneRequest) {
        try {
            userService.deletePhone(getCurrentUserId(), phoneRequest.getPhone());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException | CommonApiException e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("ERROR {} " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> findUsers(@Valid @RequestBody SearchRequest searchRequest) {
        try {
            List<User> arr = userService.findUsers(searchRequest.getName(), searchRequest.getDateOfBirth(),
                    searchRequest.getEmail(), searchRequest.getPhone(), searchRequest.getPage(), searchRequest.getSize());
            if (arr.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                SearchResponse searchResponse = new SearchResponse();
                arr.forEach(e -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setName(e.getName());
                    userInfo.setDateOfBirth(e.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    userInfo.setEmails(String.join(", ", e.getEmails().stream().map(EmailData::getEmail).collect(Collectors.toSet())));
                    userInfo.setPhones(String.join(", ", e.getPhones().stream().map(PhoneData::getPhone).collect(Collectors.toSet())));
                    searchResponse.getUsers().add(userInfo);
                });
                return new ResponseEntity<>(searchResponse, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getCause().getCause().getLocalizedMessage(), HttpStatus.NO_CONTENT);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}
