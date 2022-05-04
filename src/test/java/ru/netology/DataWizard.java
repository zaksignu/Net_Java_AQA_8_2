package ru.netology;


import at.favre.lib.crypto.bcrypt.BCrypt;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Locale;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;

public class DataWizard {
    private DataWizard() {
    }


    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    static Faker ghostOne = new Faker(new Locale("EN"));

    public static String generateIt() {
        return ghostOne.regexify("[a-z1-9]{10}");
    }

    public static String generateCard() {
        return ghostOne.regexify("[1-9]{16}");
    }

    public static String generateLogin() {
        return (ghostOne.aviation().aircraft() + "." + ghostOne.ancient().hero());
    }

    public static String generateHumanPass() {
        return ghostOne.regexify("[a-z1-9]{10}");
    }

    public static String generateBCryptPass(String humanPass) {
        return BCrypt.withDefaults().hashToString(10, humanPass.toCharArray());
    }

    @SneakyThrows
    public static void userRegister(FellowOne user) {

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                var registerIt = conn.createStatement();
        ) {
            registerIt.execute("INSERT INTO app.users (id,login,password,status) VALUES('" +
                    user.getId() + "','" +
                    user.getLogin() + "','" +
                    user.getBcCryptPass() + "'," +
                    "'active')");
            registerIt.execute("INSERT INTO app.cards (id,user_id,number,balance_in_kopecks) VALUES('" +
                    generateIt() + "','" +
                    user.getId() + "','" +
                    user.getCardOne() + "'," +
                    "'1000000')");
        }

    }

    @SneakyThrows
    public static String getVeryficationCode(FellowOne user) {

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                var veryfyIT = conn.createStatement();
        ) {
            try (var rs = veryfyIT.executeQuery("SELECT code from auth_codes WHERE user_id = '" + user.getId() + "';")) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }


        }
        return "";
    }


    @SneakyThrows
    public static String getUserIdFromCards(String id) {

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );
                var getId = conn.createStatement();
        ) {
            try (var rs = getId.executeQuery("SELECT user_id FROM cards WHERE id = '" + id + "';")) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }


        }
        return "";
    }

    public static class UserManipulating {
        private UserManipulating() {
        }

        public static FellowOne generateUser() {
            String humanPass;
            FellowOne user = new FellowOne(
                    generateIt(),
                    generateLogin(),
                    humanPass = generateHumanPass(),
                    generateBCryptPass(humanPass),
                    "",
                    generateCard());
            userRegister(user);
            return user;
        }

        public static FellowOne updateUserToken(FellowOne oldUser, String token) {
            FellowOne newUser = new FellowOne(
                    oldUser.getId(),
                    oldUser.getLogin(),
                    oldUser.getHumanPass(),
                    oldUser.BcCryptPass,
                    token,
                    oldUser.cardOne);
            return newUser;
        }

        public static Data getAuthentInfo(FellowOne user) {
            Data newUser = new Data.AuthenEntity(
                    user.getLogin(),
                    user.getHumanPass());
            return newUser;
        }

        public static Data getWrongAuthentInfo(FellowOne user) {
            Data newUser = new Data.AuthenEntity(
                    user.getLogin(),
                    "tr543");
            return newUser;
        }

        public static Data getVerifyInfo(FellowOne user) {
            Data newUser = new Data.VerifyEntity(
                    user.getLogin(),
                    getVeryficationCode(user));
            return newUser;
        }

        public static Data getWrongVerifyInfo(FellowOne user) {
            Data newUser = new Data.VerifyEntity(
                    user.getLogin(),
                    "3321");
            return newUser;
        }

    }

    public static class ConnectMe {
        private ConnectMe() {
        }

        public static RequestSpecification Specification(String token) {
            RequestSpecification requestSpec = new RequestSpecBuilder()
                    .setBaseUri("http://localhost")
                    .setPort(9999)
                    .setAccept(ContentType.JSON)
                    .setContentType(ContentType.JSON)
                    .addParam("bearer", token)
                    .log(LogDetail.ALL)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return requestSpec;
        }


        public static void AuthenticateMe(FellowOne ghost) {
            given() // "дано"
                    .spec(requestSpec)
                    .body(DataWizard.UserManipulating.getAuthentInfo(ghost))
                    .when()
                    .post("/api/auth")
                    .then()
                    .statusCode(200);
        }


        public static FellowOne VerificateMe(FellowOne ghost) {
            Response response = given()
                    .spec(requestSpec)
                    .body(DataWizard.UserManipulating.getVerifyInfo(ghost))
                    .when()
                    .post("/api/auth/verification");
            String responseBody = response.getBody().asString();

            JsonPath jsonPath = new JsonPath(responseBody);
            String token = jsonPath.getString("token");
            FellowOne updatedGhost = DataWizard.UserManipulating.updateUserToken(ghost, token);
            return updatedGhost;
        }

        public static Data.CardBalance getCards(FellowOne ghost) {
            RequestSpecification tokenSpecification = DataWizard.ConnectMe.Specification(ghost.getBearToken());
            Response response = given()
                    .spec(tokenSpecification)
                    .body("")
                    .when()
                    .get("/api/cards");
            String responseBody = response.getBody().asString();

            JsonPath jsonPath = new JsonPath(responseBody);
            String id = jsonPath.getString("id").replace("[", "").replace("]", "");
            String number = jsonPath.getString("number").replace("[", "").replace("]", "").replace("*", "").replace(" ", "");

            String balance = jsonPath.getString("balance").replace("[", "").replace("]", "");

            //  System.out.println("");
            return new Data.CardBalance(id, getUserIdFromCards(id), number, balance);
//
//            Response response = given()
//                    .spec(requestSpec)
//                    .body(DataWizard.UserManipulating.getVerifyInfo(ghost))
//                    .when()
//                    .post("/api/auth/verification");
//            String responseBody = response.getBody().asString();
//
//            JsonPath jsonPath = new JsonPath(responseBody);
//            String token = jsonPath.getString("token");
//            FellowOne updatedGhost = DataWizard.UserManipulating.updateUserToken(ghost,token);
//           // return updatedGhost;
        }


        public static void wrongAuth(FellowOne ghost) {
            given()
                    .spec(requestSpec)
                    .body(DataWizard.UserManipulating.getWrongAuthentInfo(ghost))
                    .when()
                    .post("/api/auth")
                    .then()
                    .statusCode(400);
        }

        public static void wrongVerify(FellowOne ghost) {
            given()
                    .spec(requestSpec)
                    .body(DataWizard.UserManipulating.getWrongVerifyInfo(ghost))
                    .when()
                    .post("/api/auth/verification")
                    .then()
                    .statusCode(400);

        }
    }


//            given() // "дано"
//                    .spec(requestSpec) // указываем, какую спецификацию используем
//                    .body(fatBody) // передаём в теле объект, который будет преобразован в JSON
//                    .when() // "когда"
//                    .post(path) // на какой путь, относительно BaseUri отправляем запрос
//                    .then() // "тогда ожидаем"
//                    .statusCode(200); // код 200 OK


//           given() // "дано"
//                    .spec(requestSpec) // указываем, какую спецификацию используем
//                    .body(fatBody) // передаём в теле объект, который будет преобразован в JSON
//                    .when() // "когда"
//                    .post(path) // на какой путь, относительно BaseUri отправляем запрос
//                    .then() // "тогда ожидаем"
//                    .statusCode(200); // код 200 OK
//            if (type == "verify"){
//
//            Response response = given().spec(requestSpec).body(fatBody).when().post(path);
//            String responseBody = response.getBody().asString();
//
//            JsonPath jsonPath = new JsonPath(responseBody);
//            String user_id = jsonPath.getString("token");
//                System.out.println("");
//            }

//            ArrayList<String> amounts =  given() // "дано"
//                    .spec(requestSpec) // указываем, какую спецификацию используем
//                    .body(fatBody).when().post(path).then().extract().path("data") ;

    //          System.out.println("");
    //     }


    //  }


    @Value
    public static class FellowOne {
        private String id;
        private String login;
        private String HumanPass;
        private String BcCryptPass;
        private String bearToken;
        private String cardOne;
    }


//    @Value
//    public static class AuthenEntity   {
//        private String login;
//        private String password;
//    }
//
//    @Value
//    public static class VerifyEntity {
//        private String login;
//        private String code;
//    }

}


