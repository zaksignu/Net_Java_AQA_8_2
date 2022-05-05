package ru.netology;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ApiTests {
    static DataWizard.FellowOne newGhost;
    static DataWizard.FellowOne ghost = DataWizard.UserManipulating.generateUser();
    static DataWizard.FellowOne newSecondGhost;
    static DataWizard.FellowOne secondGhost = DataWizard.UserManipulating.generateUser();

    @BeforeAll
    static void startUP() {

        DataWizard.ConnectMe.AuthenticateMe(secondGhost);
        newSecondGhost = DataWizard.ConnectMe.VerificateMe(secondGhost);

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
    void shouldGetCardInfo() {
        Data.CardBalance card = DataWizard.ConnectMe.getCards(newGhost);
        Assertions.assertEquals(newGhost.getCardOne().substring(14, 16), card.getNumber());
        Assertions.assertEquals(newGhost.getId(), card.getUserId());
    }

    @Test
    void shouldTransferFromOneToTwo() {
        int initBalance = 100_00;
        int amount = 50;
        DataWizard.ConnectMe.TransferMe(newGhost, secondGhost, amount,200);
        Data.CardBalance cardOne = DataWizard.ConnectMe.getCards(newGhost);
        Data.CardBalance cardTwo = DataWizard.ConnectMe.getCards(newSecondGhost);
        int actualOne = cardOne.getBalance();
        int expectedOne = (initBalance - amount * 100) / 100;
        int actualTwo = cardTwo.getBalance();
        int expectedTwo = (initBalance + amount * 100) / 100;
        Assertions.assertEquals(expectedOne, actualOne);
        Assertions.assertEquals(expectedTwo, actualTwo);
    }

    @Test
    void shouldNotTransferWithWrongToken() {

        DataWizard.FellowOne fakeGhost;
        int amount = 150;
        fakeGhost = DataWizard.UserManipulating.updateUserToken(ghost, "eeerrr");
        DataWizard.ConnectMe.TransferMe(fakeGhost, secondGhost, amount,401);

    }

    @AfterAll
    static void cleanUp() {
        DataWizard.cleanGarbage(ghost);
        DataWizard.cleanGarbage(secondGhost);
    }
}


