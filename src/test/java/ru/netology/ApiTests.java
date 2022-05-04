package ru.netology;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ApiTests {
    static DataWizard.FellowOne newGhost;
    static DataWizard.FellowOne ghost = DataWizard.UserManipulating.generateUser();

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Test
    void shouldAuthenticateAndVerify() {
        DataWizard.ConnectMe.AuthenticateMe(ghost);
        newGhost = DataWizard.ConnectMe.VerificateMe(ghost);
    }

    @Test
    void shouldNotWorkAuthentication() {
        DataWizard.ConnectMe.wrongAuth(ghost);
    }


    @Test
    void shouldNotWorkVerificationWithCorrectAuthentication() {
        DataWizard.ConnectMe.AuthenticateMe(ghost);
        DataWizard.ConnectMe.wrongVerify(ghost);
    }

    @Test
    void shouldTransfer() {
        DataWizard.ConnectMe.AuthenticateMe(ghost);
        newGhost = DataWizard.ConnectMe.VerificateMe(ghost);
        Data.CardBalance card = DataWizard.ConnectMe.getCards(newGhost);
        System.out.println("");
        Assertions.assertEquals(newGhost.getCardOne().substring(14, 16), card.getNumber());
        Assertions.assertEquals(newGhost.getId(), card.getUserId());
    }

}


