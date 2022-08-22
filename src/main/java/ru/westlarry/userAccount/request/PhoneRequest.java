package ru.westlarry.userAccount.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class PhoneRequest {
    @NotBlank
    @Size(max = 13)
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
