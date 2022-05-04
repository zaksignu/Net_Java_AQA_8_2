package ru.netology;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;



public class ApiTests {
    static DataWizard.FellowOne ghost = DataWizard.UserManipulating.generateUser();
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Test
    void happyPath(){
        DataWizard.ConnectMe.AuthenticateMe(ghost);
        DataWizard.ConnectMe.VerificateMe( ghost);

    }

    @Test
    void shouldNotWorkAuthentication(){
        DataWizard.ConnectMe.wrongAuth(ghost);
    }


    @Test
    void shouldNotWorkVerificationWithCorrectAuthentication(){
        DataWizard.ConnectMe.AuthenticateMe(ghost);
        DataWizard.ConnectMe.wrongVerify(ghost);
    }

    }

