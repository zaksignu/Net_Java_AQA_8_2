package ru.netology;

import lombok.Value;

public class Data {
    private Data() {
    }

    @Value
    public static class AuthenEntity extends Data {
        private String login;
        private String password;
    }

    @Value
    public static class VerifyEntity extends Data {
        private String login;
        private String code;
    }

    @Value
    public static class CardBalance extends Data {
        private String id;
        private String userId;
        private String number;
        private int balance;
    }

    @Value
    public static class CardsTransfer extends Data {
        private String from;
        private String to;
        private int amount;
    }
}
