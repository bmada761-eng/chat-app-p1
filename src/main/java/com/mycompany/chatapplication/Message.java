/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapplication;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents one QuickChat message and holds the validation, hashing,
 * sending and storing logic described in the Part 2 brief.
 *
 * References / attribution:
 *  - The JSON file format (arrays, objects, strings and the escape rules used
 *    in jsonEscape) follows the JSON specifications
 *    
 * 
 */

public class Message {

    /** Maximum number of characters allowed in a message. */
    public static final int MAX_LENGTH = 250;

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Messages sent while the program is running. */
    private static final List<Message> sentMessages = new ArrayList<>();

    /** File that stored messages are written to (changeable for testing). */
    private static Path storagePath = Paths.get("storedMessages.json");

    private final String messageId;     // unique ten-digit ID
    private final int messageNumber;    // starts at 0, incremented by the loop counter
    private final String recipient;     // cell number including international code
    private final String text;          // the message itself
    private final String messageHash;   // e.g. 00:0:HITONIGHT

    /** Creates a message with a randomly generated ID. */
    public Message(int messageNumber, String recipient, String text) {
        this(generateMessageId(), messageNumber, recipient, text);
    }

    /** Creates a message with a supplied ID (used by the unit tests). */
    public Message(String messageId, int messageNumber, String recipient, String text) {
        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    /**
     * Generates a random ten-digit message ID using string manipulation:
     * a long string of random digits is created, then a loop counter and
     * substring pick out exactly ten digits.
     */
    public static String generateMessageId() {
        String digits = (RANDOM.nextLong() & Long.MAX_VALUE) + "" + (RANDOM.nextLong() & Long.MAX_VALUE)
                + "0000000000"; // padding guarantees at least ten digits
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(digits.substring(i, i + 1));
        }
        return id.toString();
    }

    /** Ensures the message ID is not more than ten characters. */
    public boolean checkMessageID() {
        return messageId != null && messageId.length() <= 10;
    }

    /** Returns the text shown to the user when the ID has been created. */
    public String messageIdGenerated() {
        return "Message ID generated: " + messageId;
    }

    /** Returns the success or failure text for a message's length. */
    public static String lengthStatus(String text) {
        int length = text == null ? 0 : text.length();
        if (length <= MAX_LENGTH) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - MAX_LENGTH)
                + ", please reduce the size.";
    }

    /** Ensures this message is not more than 250 characters. */
    public String checkMessageLength() {
        return lengthStatus(text);
    }

    /**
     * Re-uses the Part 1 cell number validation (Login.checkCellPhoneNumber)
     * as the brief instructs. Only the cell number matters here, so the
     * username and password passed to Login are placeholders.
     */
    public static boolean isValidRecipient(String number) {
        return number != null && new Login("_", "", number).checkCellPhoneNumber();
    }

    /** Returns the success or failure text for a recipient cell number. */
    public static String recipientStatus(String number) {
        if (isValidRecipient(number)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /** Checks this message's recipient cell number. */
    public String checkRecipientCell() {
        return recipientStatus(recipient);
    }

    /**
     * Creates the message hash: first two digits of the ID, a colon, the
     * message number, a colon, then the first and last words in capitals.
     */
    public String createMessageHash() {
        String prefix = messageId.substring(0, Math.min(2, messageId.length()));
        String[] words = text == null ? new String[0] : text.trim().split("\\s+");
        String first = words.length > 0 ? stripPunctuation(words[0]) : "";
        String last = words.length > 0 ? stripPunctuation(words[words.length - 1]) : "";
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Removes punctuation such as commas and question marks from a word. */
    private static String stripPunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Lets the user send (1), disregard (2) or store (3) the message and
     * returns the matching confirmation text.
     */
    public String SentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    /** Full details in the required order: ID, hash, recipient, message. */
    public String details() {
        return "Message ID: " + messageId + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + text;
    }

    /** Returns the details of every message sent while the program runs. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.details()).append("\n\n");
        }
        return sb.toString().trim();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    /**
     * Appends this message to the JSON file (created if it does not exist).
     * The file holds a JSON array, e.g. [ {...}, {...} ]. The format follows
     * the JSON specification - see the references in the class comment.
     */
    public void storeMessage() {
        String entry = "  {\n"
                + "    \"messageId\": \"" + jsonEscape(messageId) + "\",\n"
                + "    \"messageNumber\": " + messageNumber + ",\n"
                + "    \"messageHash\": \"" + jsonEscape(messageHash) + "\",\n"
                + "    \"recipient\": \"" + jsonEscape(recipient) + "\",\n"
                + "    \"message\": \"" + jsonEscape(text) + "\"\n"
                + "  }";
        try {
            String existing = Files.exists(storagePath)
                    ? Files.readString(storagePath, StandardCharsets.UTF_8).trim()
                    : "";
            String json;
            if (existing.startsWith("[") && existing.endsWith("]") && existing.length() > 2) {
                // Existing array with messages: drop the closing bracket and append
                String withoutEnd = existing.substring(0, existing.length() - 1).trim();
                json = withoutEnd + ",\n" + entry + "\n]";
            } else {
                // No file yet (or an empty array): start a new array
                json = "[\n" + entry + "\n]";
            }
            Files.writeString(storagePath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    /** Escapes characters that are not allowed inside a JSON string. */
    private static String jsonEscape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    // ---- getters and test helpers ----
    public String getMessageId() { return messageId; }
    public int getMessageNumber() { return messageNumber; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }

    public static void setStoragePath(Path path) { storagePath = path; }
    public static void resetSentMessages() { sentMessages.clear(); }
}