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

    public Long addNewEmail(Long userId, String email) throws NonUniqueException, UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            boolean exists = emailDataRepository.existsByEmail(email);
            if (exists) {
                throw new NonUniqueException("Такой email уже используется");
            }
            EmailData emailData = new EmailData();
            emailData.setEmail(email);
            emailData.setUser(user.get());

            EmailData saved = emailDataRepository.save(emailData);
            return saved.getId();
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Long changeEmail(Long userId, Long emailDataId, String newEmail) throws NonUniqueException, UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<EmailData> emailDataOpt = emailDataRepository.findById(emailDataId);
            if (emailDataOpt.isEmpty()) {
                throw new CommonApiException("Email не найден");
            }
            EmailData currentEmailData = emailDataOpt.get();
            String currentEmail = currentEmailData.getEmail();
            Long ownerId = currentEmailData.getUser().getId();
            if (!user.get().getId().equals(ownerId)) {
                throw new CommonApiException("Запрещается изменять чужие данные");
            }

            if (currentEmail.equalsIgnoreCase(newEmail)) {
                throw new CommonApiException("Значение email не отличается от сохраненного");
            }

            boolean exists = emailDataRepository.existsByEmail(newEmail);
            if (exists) {
                throw new NonUniqueException("Такой email уже используется");
            }

            currentEmailData.setEmail(newEmail);
            EmailData saved = emailDataRepository.save(currentEmailData);
            return saved.getId();
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Long deleteEmail(Long userId, Long emailDataId) throws UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<EmailData> emailDataOpt = emailDataRepository.findById(emailDataId);
            if (emailDataOpt.isEmpty()) {
                throw new CommonApiException("Email не найден");
            }
            EmailData currentEmailData = emailDataOpt.get();
            Long ownerId = currentEmailData.getUser().getId();
            if (!user.get().getId().equals(ownerId)) {
                throw new CommonApiException("Запрещается удалять чужие данные");
            }

            emailDataRepository.deleteById(emailDataId);
            return emailDataId;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Long addNewPhone(Long userId, String phone) throws NonUniqueException, UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            boolean exists = phoneDataRepository.existsByPhone(phone);
            if (exists) {
                throw new NonUniqueException("Такой phone уже используется");
            }

            PhoneData phoneData = new PhoneData();
            phoneData.setPhone(phone);
            phoneData.setUser(user.get());

            PhoneData saved = phoneDataRepository.save(phoneData);
            return saved.getId();
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Long changePhone(Long userId, Long phoneDataId, String newPhone) throws NonUniqueException, UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<PhoneData> phoneDataOpt = phoneDataRepository.findById(phoneDataId);
            if (phoneDataOpt.isEmpty()) {
                throw new CommonApiException("Phone не найден");
            }

            PhoneData currentPhoneData = phoneDataOpt.get();
            String currentPhone = currentPhoneData.getPhone();
            Long ownerId = currentPhoneData.getUser().getId();
            if (!user.get().getId().equals(ownerId)) {
                throw new CommonApiException("Запрещается изменять чужие данные");
            }
            if (currentPhone.equalsIgnoreCase(newPhone)) {
                throw new CommonApiException("Значение phone не отличается от сохраненного");
            }

            boolean exists = phoneDataRepository.existsByPhone(newPhone);
            if (exists) {
                throw new NonUniqueException("Такой phone уже используется");
            }

            currentPhoneData.setPhone(newPhone);
            PhoneData saved = phoneDataRepository.save(currentPhoneData);
            return saved.getId();
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public Long deletePhone(Long userId, Long phoneDataId) throws UserNotFoundException, CommonApiException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<PhoneData> phoneDataOpt = phoneDataRepository.findById(phoneDataId);
            if (phoneDataOpt.isEmpty()) {
                throw new CommonApiException("Email не найден");
            }
            PhoneData currentPhoneData = phoneDataOpt.get();
            Long ownerId = currentPhoneData.getUser().getId();
            if (!user.get().getId().equals(ownerId)) {
                throw new CommonApiException("Запрещается удалять чужие данные");
            }

            phoneDataRepository.deleteById(phoneDataId);
            return phoneDataId;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
