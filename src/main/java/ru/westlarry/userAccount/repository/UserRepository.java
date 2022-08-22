package ru.westlarry.userAccount.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.westlarry.userAccount.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users t where exists (select e.id from email_data e where e.user_id = t.id and e.email = :login) or " +
            " exists (select p.id from phone_data p where p.user_id = t.id and p.phone = :login)", nativeQuery = true)
    Optional<User> findByEmailOrPhone(@Param("login") String login);

    @Query(value = "select distinct t from User t join t.emails e join t.phones p" +
            " where (cast(:dateOfBirth as date) is null or t.dateOfBirth > :dateOfBirth) and (:name is null or t.name like :name%) and " +
            " (:email is null or e.email = :email) and " +
            " (:phone is null or p.phone = :phone) order by t.name")
    List<User> searchByFilter(@Param("dateOfBirth") LocalDate dateOfBirth, @Param("name") String name,
            @Param("email") String email, @Param("phone") String phone, Pageable pageable);
}
