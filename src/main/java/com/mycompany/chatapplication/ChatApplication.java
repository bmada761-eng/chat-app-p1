/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapplication;
import java.util.Scanner;

/**
 *Main Application class
 * Handles user interaction via console
 */
public class ChatApplication {

    public static void main(String[] args) {
    try (Scanner scanner = new Scanner(System.in)) {
            Login registeredUser = null;
            boolean isRegistered = false;

            System.out.println("==========================================");
            System.out.println("   WELCOME TO THE LOGIN SYSTEM - POE");
            System.out.println("==========================================");

            // REGISTRATION PHASE
            while (!isRegistered) {
                System.out.println("\n--- REGISTRATION ---");
                System.out.print("Enter Username (must have _ and <=5 chars): ");
                String usernameInput = scanner.nextLine();

                System.out.print("Enter Password (8+ chars, Capital, Number, Special): ");
                String passwordInput = scanner.nextLine();

                System.out.print("Enter Cell Number (+27XXXXXXXXX): ");
                String cellInput = scanner.nextLine();

                registeredUser = new Login(usernameInput, passwordInput, cellInput);

                // Show validation messages
                System.out.println("\n>> " + registeredUser.getUserNameMessage());
                System.out.println(">> " + registeredUser.getPasswordMessage());
                System.out.println(">> " + registeredUser.getCellPhoneMessage());

                // Registration succeeds only if all validations pass
                if (registeredUser.checkUserName() &&
                    registeredUser.checkPasswordComplexity() &&
                    registeredUser.checkCellPhoneNumber()) {
                    isRegistered = true;
                    System.out.println("Registration complete! Proceeding to login...");
                } else {
                    System.out.println("Please try again with correct format.\n");
                }
            }

            // LOGIN PHASE
            boolean isLoggedIn = false;
            int attempts = 0;
            int maxAttempts = 3;

            while (!isLoggedIn && attempts < maxAttempts) {
                System.out.println("\n--- LOGIN (Attempt " + (attempts + 1) + "/" + maxAttempts + ") ---");
                System.out.print("Enter Username: ");
                String loginUsername = scanner.nextLine();

                System.out.print("Enter Password: ");
                String loginPassword = scanner.nextLine();

                boolean loginSuccess = registeredUser.loginUser(loginUsername, loginPassword);
                String statusMessage = registeredUser.returnLoginStatus(loginUsername, loginPassword);

                System.out.println("\n>> " + statusMessage);

                if (loginSuccess) {
                    isLoggedIn = true;
                    System.out.println("\n==========================================");
                    System.out.println("   LOGIN SUCCESSFUL - ACCESS GRANTED");
                    System.out.println("==========================================");

                    // Menu after login
                    boolean exit = false;
                    while (!exit) {
                        System.out.println("\nSelect an option:");
                        System.out.println("1. Validate Username");
                        System.out.println("2. Validate Password");
                        System.out.println("3. Validate Cell Phone");
                        System.out.println("4. Exit");

                        System.out.print("Choice: ");
                        String input = scanner.nextLine().trim();

                        if (input.isEmpty()) {
                            System.out.println("Invalid input. Please enter a number.");
                            continue;
                        }

                        int choice;
                        try {
                            choice = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            continue;
                        }

                        switch (choice) {
                            case 1:
                                System.out.println(">> " + registeredUser.getUserNameMessage());
                                break;
                            case 2:
                                System.out.println(">> " + registeredUser.getPasswordMessage());
                                break;
                            case 3:
                                System.out.println(">> " + registeredUser.getCellPhoneMessage());
                                break;
                            case 4:
                                System.out.println("Goodbye!");
                                exit = true;
                                break;
                            default:
                                System.out.println("Invalid choice. Try again.");
                        }
                    }
                } else {
                    attempts++;
                    if (attempts < maxAttempts) {
                        System.out.println("You have " + (maxAttempts - attempts) + " attempts left.");
                    } else {
                        System.out.println("Maximum attempts reached. Account locked.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // shows any unexpected error instead of exit code 1
        }
    }
}