package ru.westlarry.userAccount.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ChangePhoneRequest {
    @NotBlank
    @Size(max = 13)
    private String oldPhone;

    @NotBlank
    @Size(max = 13)
    private String newPhone;

    public String getOldPhone() {
        return oldPhone;
    }

    public void setOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public void setNewPhone(String newPhone) {
        this.newPhone = newPhone;
    }
}
