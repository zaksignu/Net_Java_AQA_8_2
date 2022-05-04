package ru.netology;

import lombok.Value;

public class Data {
    private Data() {
    }
    @Value
    public static class AuthenEntity extends Data  {
        private String login;
        private String password;
    }

    @Value
    public static class VerifyEntity extends Data {
        private String login;
        private String code;
    }
}
