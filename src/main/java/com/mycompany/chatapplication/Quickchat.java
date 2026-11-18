/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapplication;

import java.util.List;
import java.util.Scanner;

/**
 * QuickChat: welcome message, numeric menu, message-sending loops (Part 2)
 * and the Stored Messages menu (Part 3).
 * Called from ChatApplication once the user has logged in successfully.
 */
public class Quickchat {

  
    /** Entry point. Re-uses the Scanner created in ChatApplication. */
    public static void start(Scanner in, String username) {
        // Read the JSON file into the stored messages array
        MessageManager.loadStoredMessages();
        System.out.println("Welcome to QuickChat.");
        runMenu(in, username);
    }

    /** While loop: keeps showing the menu until the user chooses Quit. */
    private static void runMenu(Scanner in, String username) {
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            switch (askInt(in, "Choose an option: ")) {
                case 1:
                    sendMessages(in, username);
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                case 4:
                    storedMessagesMenu(in);
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, 3 or 4.");
            }
        }
    }

    /** For loop: the user enters exactly the number of messages they chose. */
    private static void sendMessages(Scanner in, String username) {
        int numMessages = askInt(in, "How many messages would you like to enter? ");

        // The loop counter (i) is the message number used in the hash
        for (int i = 0; i < numMessages; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + numMessages + " ---");

            String recipient = askRecipient(in);
            String recipientName = ask(in, "Recipient name: ");
            String text = askMessageText(in);

            Message msg = new Message(i, recipient, text);
            msg.setSender(username);
            System.out.println(msg.messageIdGenerated());
            System.out.println("Message Number: " + msg.getMessageNumber());
            System.out.println("Message Hash: " + msg.getMessageHash());
            System.out.println(msg.checkMessageLength());

            int choice = askSendChoice(in);
            System.out.println(msg.SentMessage(choice));

            if (choice == 2 && ask(in, "Enter 0 to delete: ").equals("0")) {
                System.out.println("Message deleted.");
            }

            System.out.println("Name: " + recipientName + " | Number: " + recipient);
            if (choice == 1) {
                System.out.println("\n" + msg.details());
            }
        }

        // Once all messages have been entered: everything sent plus the total
        System.out.println("\n=== Messages sent ===");
        System.out.println(Message.printMessages());
        System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
    }

    /** Part 3: the Stored Messages menu, which uses the arrays in MessageManager. */
    private static void storedMessagesMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages to a particular recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Back to main menu");
            switch (askInt(in, "Choose an option: ")) {
                case 1:
                    System.out.println(MessageManager.displaySenderAndRecipient());
                    break;
                case 2:
                    System.out.println(MessageManager.longestStoredMessage());
                    break;
                case 3:
                    System.out.println(MessageManager.searchByMessageId(ask(in, "Enter the message ID: ")));
                    break;
                case 4:
                    List<String> found = MessageManager.searchByRecipient(ask(in, "Enter the recipient number: "));
                    if (found.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String message : found) {
                        System.out.println(message);
                    }
                    break;
                case 5:
                    System.out.println(MessageManager.deleteByHash(ask(in, "Enter the message hash: ")));
                    break;
                case 6:
                    System.out.println(MessageManager.storedMessagesReport());
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1 to 7.");
            }
        }
    }

    /** Asks for the recipient's cell number until it is valid. */
    private static String askRecipient(Scanner in) {
        String recipient = ask(in, "Recipient cell number (with international code, e.g. +27718693002): ");
        while (!Message.isValidRecipient(recipient)) {
            System.out.println(Message.recipientStatus(recipient));
            recipient = ask(in, "Recipient cell number: ");
        }
        System.out.println(Message.recipientStatus(recipient));
        return recipient;
    }

    /** Asks for the message until it is no longer than 250 characters. */
    private static String askMessageText(Scanner in) {
        String text = ask(in, "Message: ");
        while (text.length() > Message.MAX_LENGTH) {
            System.out.println(Message.lengthStatus(text));
            System.out.println("Please enter a message of less than 250 characters.");
            text = ask(in, "Message: ");
        }
        return text;
    }

    /** Asks whether to send (1), disregard (2) or store (3) the message. */
    private static int askSendChoice(Scanner in) {
        int choice;
        do {
            System.out.println("1) Send Message  2) Disregard Message  3) Store Message to send later");
            choice = askInt(in, "Choose: ");
        } while (choice < 1 || choice > 3);
        return choice;
    }

    /** Prompts for and returns a line of text. */
    private static String ask(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    /** Prompts until the user enters a whole number. */
    private static int askInt(Scanner in, String prompt) {
        while (true) {
            try {
                return Integer.parseInt(ask(in, prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}