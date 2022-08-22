package ru.westlarry.userAccount.request;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    List<UserInfo> users = new ArrayList<>();

    public List<UserInfo> getUsers() {
        return users;
    }
}
