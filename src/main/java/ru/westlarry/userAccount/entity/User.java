package ru.westlarry.userAccount.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 500)
    @NotBlank
    @Column(name = "NAME", nullable = false, length = 500)
    private String name;

    //@JsonFormat(pattern = "dd.MM.yyyy")
    @Column(name = "DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @NotBlank
    @Size(min = 8, max = 500)
    @Column(name = "PASSWORD", nullable = false, length = 500)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @NotEmpty
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @NotEmpty
    private Set<PhoneData> phones = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Account account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<EmailData> getEmails() {
        return emails;
    }

    public void setEmails(Set<EmailData> emails) {
        this.emails = emails;
    }

    public Set<PhoneData> getPhones() {
        return phones;
    }

    public void setPhones(Set<PhoneData> phones) {
        this.phones = phones;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
