package ru.westlarry.userAccount.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.westlarry.userAccount.entity.EmailData;
import ru.westlarry.userAccount.entity.PhoneData;
import ru.westlarry.userAccount.entity.User;
import ru.westlarry.userAccount.exception.CommonApiException;
import ru.westlarry.userAccount.exception.NonUniqueException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.repository.EmailDataRepository;
import ru.westlarry.userAccount.repository.PhoneDataRepository;
import ru.westlarry.userAccount.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private EmailDataRepository emailDataRepository;

    @Autowired
    private PhoneDataRepository phoneDataRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> findUsers(String name, String dateOfBirth, String email, String phone, int page, int size) {
        LocalDate localDate = dateOfBirth == null ? null : LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return userRepository.searchByFilter(localDate, name == null ? "" : name, email, phone, PageRequest.of(page, size, Sort.Direction.ASC, "name"));
    }

    public void addNewEmail(Long userId, String email) throws NonUniqueException, UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            boolean exists = emailDataRepository.existsByEmail(email);
            if (exists) {
                throw new NonUniqueException("Такой email уже используется");
            }
            EmailData emailData = new EmailData();
            emailData.setEmail(email);
            emailData.setUser(user.get());

            emailDataRepository.save(emailData);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void changeEmail(Long userId, String oldEmail, String newEmail) throws NonUniqueException, UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
           List<EmailData> emailData = emailDataRepository.findByEmailAndUserId(oldEmail, userId);
           if (emailData.isEmpty()) {
               throw new CommonApiException("Email не найден");
           }

           if (oldEmail.equalsIgnoreCase(newEmail)) {
               throw new CommonApiException("Значение email не отличается от сохраненного");
           }

            boolean exists = emailDataRepository.existsByEmail(newEmail);
            if (exists) {
                throw new NonUniqueException("Такой email уже используется");
            }

            EmailData currentEmailData = emailData.get(0);
            currentEmailData.setEmail(newEmail);
            emailDataRepository.save(currentEmailData);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void deleteEmail(Long userId, String email) throws UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<EmailData> emailDataList = emailDataRepository.findByEmailAndUserId(email, userId);
            if (emailDataList.isEmpty()) {
                throw new CommonApiException("Email не найден");
            }
            emailDataRepository.delete(emailDataList.get(0));
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void addNewPhone(Long userId, String phone) throws NonUniqueException, UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            boolean exists = phoneDataRepository.existsByPhone(phone);
            if (exists) {
                throw new NonUniqueException("Такой phone уже используется");
            }

            PhoneData phoneData = new PhoneData();
            phoneData.setPhone(phone);
            phoneData.setUser(user.get());

            phoneDataRepository.save(phoneData);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void changePhone(Long userId, String oldPhone, String newPhone) throws NonUniqueException, UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<PhoneData> phoneDataList = phoneDataRepository.findByPhoneAndUserId(oldPhone, userId);
            if (phoneDataList.isEmpty()) {
                throw new CommonApiException("Phone не найден");
            }

            if (oldPhone.equalsIgnoreCase(newPhone)) {
                throw new CommonApiException("Значение phone не отличается от сохраненного");
            }

            boolean exists = phoneDataRepository.existsByPhone(newPhone);
            if (exists) {
                throw new NonUniqueException("Такой phone уже используется");
            }

            PhoneData currentPhoneData = phoneDataList.get(0);
            currentPhoneData.setPhone(newPhone);
            phoneDataRepository.save(currentPhoneData);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void deletePhone(Long userId, String phone) throws UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<PhoneData> phoneDataList = phoneDataRepository.findByPhoneAndUserId(phone, userId);
            if (phoneDataList.isEmpty()) {
                throw new CommonApiException("Phone не найден");
            }
            phoneDataRepository.delete(phoneDataList.get(0));
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
