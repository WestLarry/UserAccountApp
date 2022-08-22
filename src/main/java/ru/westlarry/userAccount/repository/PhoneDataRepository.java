package ru.westlarry.userAccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.westlarry.userAccount.entity.PhoneData;

import java.util.List;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);

    List<PhoneData> findByPhoneAndUserId(String phone, Long userId);
}
