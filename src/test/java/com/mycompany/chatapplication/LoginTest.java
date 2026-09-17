/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.chatapplication;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class LoginTest {
   
  // USERNAME TESTS
    @Test
    public void testUsernameCorrectlyFormattedBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertTrue(login.checkUserName());
    }

    @Test
    public void testUsernameCorrectlyFormattedMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Welcome kyl, 1 it is great to see you.", login.getUserNameMessage());
    }

    @Test
    public void testUsernameIncorrectlyFormattedBoolean() {
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27383968976");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormattedMessage() {
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.", login.getUserNameMessage());
    }

    // PASSWORD TESTS
    @Test
    public void testPasswordMeetsComplexityBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordMeetsComplexityMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Password successfully captured.", login.getPasswordMessage());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityBoolean() {
        Login login = new Login("kyl_1", "password", "+27383968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexityMessage() {
        Login login = new Login("kyl_1", "password", "+27383968976");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.", login.getPasswordMessage());
    }

    // CELL PHONE TESTS
    @Test
    public void testCellPhoneCorrectlyFormattedBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneCorrectlyFormattedMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Cell number successfully captured.", login.getCellPhoneMessage());
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormattedMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.", login.getCellPhoneMessage());
    }

    // LOGIN TESTS
    @Test
    public void testLoginSuccessfulBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginSuccessfulMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Welcome kyl, 1 it is great to see you.", login.returnLoginStatus("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailedBoolean() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertFalse(login.loginUser("wrongUser", "wrongPass"));
    }

    @Test
    public void testLoginFailedMessage() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27383968976");
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus("wrongUser", "wrongPass"));
    }
}