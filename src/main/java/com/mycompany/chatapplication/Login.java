/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapplication;

import java.util.regex.*;
/**
 *
 * @author Student
 */
public class Login {
   private String username;
    private String password;
    private String cellPhone;

    public Login(String username, String password, String cellPhone) {
        this.username = username;
        this.password = password;
        this.cellPhone = cellPhone;
    }

    // USERNAME VALIDATION
    public boolean checkUserName() {
        return username.contains("_") && username.length() <= 5;
    }

    public String getUserNameMessage() {
        if (checkUserName()) {
            return "Welcome " + username + ", it is great to see you.";
        } else {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
    }
    

    // PASSWORD VALIDATION
    public boolean checkPasswordComplexity() {
        if (password == null) return false;

        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return password.length() >= 8 && hasUpper && hasDigit && hasSpecial;
    }

    public String getPasswordMessage() {
        if (checkPasswordComplexity()) {
            return "Password successfully captured.";
        } else {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
    }

    // CELL PHONE VALIDATION
    public boolean checkCellPhoneNumber() {
        return cellPhone.matches("^\\+27\\d{9}$");
    }

    public String getCellPhoneMessage() {
        if (checkCellPhoneNumber()) {
            return "Cell number successfully captured.";
        } else {
            return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
        }
    }

    // WRAPPER FOR MAIN PROGRAM
    public String registerUser() {
        return getUserNameMessage() + "\n" + getPasswordMessage() + "\n" + getCellPhoneMessage();
    }

    // LOGIN LOGIC
    public boolean loginUser(String loginUsername, String loginPassword) {
        return this.username.equals(loginUsername) && this.password.equals(loginPassword);
    }

    public String returnLoginStatus(String loginUsername, String loginPassword) {
        if (loginUser(loginUsername, loginPassword)) {
            return "Welcome " + username + ", it is great to see you.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}