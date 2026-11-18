/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Part 3 - holds the arrays of messages and the operations on them:
 * display, search, delete and reports.
 *
 * The five arrays (dynamic arrays, so they can grow as messages are added):
 *  - sentMessages          all messages that were sent
 *  - disregardedMessages   all messages that were disregarded
 *  - storedMessages        all stored messages, read back from the JSON file
 *  - messageHashes         all message hashes
 *  - messageIDs            all message IDs
 *
 * References / attribution:
 *  - The JSON reader (JsonReader below) was written by me following the grammar
 *    for arrays, objects, strings and numbers at https://www.json.org/json-en.html
 *    (RFC 8259: https://www.rfc-editor.org/rfc/rfc8259). It only needs to read
 *    the simple format that Message.storeMessage() writes.
 */
public class MessageManager {

    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private MessageManager() {
        // static helper class - no objects needed
    }

    // ------------------------------------------------------------------
    // Populating the arrays (no hard-coding: the app calls these as messages are handled)
    // ------------------------------------------------------------------

    public static void addSent(Message message) {
        sentMessages.add(message);
        recordIdentifiers(message);
    }

    public static void addDisregarded(Message message) {
        disregardedMessages.add(message);
        recordIdentifiers(message);
    }

    public static void addStored(Message message) {
        if (!containsId(storedMessages, message.getMessageId())) {
            storedMessages.add(message);
        }
        recordIdentifiers(message);
    }

    /** Adds the message's hash and ID to their arrays (once per message). */
    private static void recordIdentifiers(Message message) {
        if (!messageIDs.contains(message.getMessageId())) {
            messageIDs.add(message.getMessageId());
            messageHashes.add(message.getMessageHash());
        }
    }

    /**
     * Reads the JSON file into the stored messages array. Safe to call more
     * than once: a message that is already in the array is not added again.
     */
    public static void loadStoredMessages() {
        String json = Message.readStorageFile().trim();
        if (json.isEmpty()) {
            return;
        }
        try {
            for (Map<String, String> fields : new JsonReader(json).readArray()) {
                Message message = new Message(
                        fields.getOrDefault("messageId", ""),
                        Integer.parseInt(fields.getOrDefault("messageNumber", "0")),
                        fields.getOrDefault("recipient", ""),
                        fields.getOrDefault("message", ""));
                message.setSender(fields.getOrDefault("sender", "Unknown"));
                addStored(message);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Could not read the stored messages file: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Stored Messages menu operations
    // ------------------------------------------------------------------

    /** a. The sender and recipient of every stored message. */
    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.getSender())
              .append(" | Recipient: ").append(m.getRecipient()).append("\n");
        }
        return sb.toString().trim();
    }

    /** b. The longest stored message (the first one if there is a tie). */
    public static String longestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) {
            if (m.getText().length() > longest.getText().length()) {
                longest = m;
            }
        }
        return longest.getText();
    }

    /** c. Searches for a message ID and returns the recipient and message. */
    public static String searchByMessageId(String messageId) {
        if (!messageIDs.contains(messageId)) {
            return "Message ID not found.";
        }
        List<List<Message>> allArrays = new ArrayList<>();
        allArrays.add(sentMessages);
        allArrays.add(storedMessages);
        allArrays.add(disregardedMessages);
        for (List<Message> array : allArrays) {
            for (Message m : array) {
                if (m.getMessageId().equals(messageId)) {
                    return "Recipient: " + m.getRecipient() + " | Message: " + m.getText();
                }
            }
        }
        return "Message ID not found.";
    }

    /** d. All messages sent or stored for a particular recipient. */
    public static List<String> searchByRecipient(String recipient) {
        List<String> found = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.getRecipient().equals(recipient)) {
                found.add(m.getText());
            }
        }
        for (Message m : storedMessages) {
            if (m.getRecipient().equals(recipient)) {
                found.add(m.getText());
            }
        }
        return found;
    }

    /** e. Deletes a stored message using its hash (also updates the JSON file). */
    public static String deleteByHash(String messageHash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.getMessageHash().equalsIgnoreCase(messageHash.trim())) {
                storedMessages.remove(i);
                messageHashes.remove(m.getMessageHash());
                messageIDs.remove(m.getMessageId());
                Message.writeStoredFile(storedMessages);
                return "Message: \"" + m.getText() + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /** f. Report with the full details of all stored messages. */
    public static String storedMessagesReport() {
        return buildReport("Stored Messages Report", storedMessages);
    }

    /** Report with the hash, recipient and message of all sent messages. */
    public static String sentMessagesReport() {
        return buildReport("Sent Messages Report", sentMessages);
    }

    private static String buildReport(String title, List<Message> messages) {
        StringBuilder sb = new StringBuilder("=== " + title + " ===\n");
        if (messages.isEmpty()) {
            return sb.append("No messages.").toString();
        }
        for (Message m : messages) {
            sb.append(m.fullDetails()).append("\n\n");
        }
        return sb.toString().trim();
    }

    // ------------------------------------------------------------------
    // Access to the arrays and test helpers
    // ------------------------------------------------------------------

    public static List<Message> getSentMessages() { return Collections.unmodifiableList(sentMessages); }
    public static List<Message> getDisregardedMessages() { return Collections.unmodifiableList(disregardedMessages); }
    public static List<Message> getStoredMessages() { return Collections.unmodifiableList(storedMessages); }
    public static List<String> getMessageHashes() { return Collections.unmodifiableList(messageHashes); }
    public static List<String> getMessageIDs() { return Collections.unmodifiableList(messageIDs); }

    /** Empties all five arrays. */
    public static void resetAll() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
    }

    private static boolean containsId(List<Message> messages, String id) {
        for (Message m : messages) {
            if (m.getMessageId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Minimal JSON reader: an array of flat objects with string/number values
    // ------------------------------------------------------------------
    private static class JsonReader {
        private final String text;
        private int pos = 0;

        JsonReader(String text) {
            this.text = text;
        }

        /** Reads [ {...}, {...} ] into a list of key/value maps. */
        List<Map<String, String>> readArray() {
            List<Map<String, String>> items = new ArrayList<>();
            skipWhitespace();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return items;
            }
            while (true) {
                items.add(readObject());
                skipWhitespace();
                char c = next();
                if (c == ']') {
                    return items;
                }
                if (c != ',') {
                    throw fail("Expected ',' or ']'");
                }
            }
        }

        private Map<String, String> readObject() {
            Map<String, String> fields = new LinkedHashMap<>();
            skipWhitespace();
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return fields;
            }
            while (true) {
                skipWhitespace();
                String key = readString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                String value = peek() == '"' ? readString() : readNumber();
                fields.put(key, value);
                skipWhitespace();
                char c = next();
                if (c == '}') {
                    return fields;
                }
                if (c != ',') {
                    throw fail("Expected ',' or '}'");
                }
            }
        }

        /** Reads a JSON string, turning escapes such as \" and \n back into characters. */
        private String readString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char escape = next();
                switch (escape) {
                    case '"':  sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/'); break;
                    case 'n':  sb.append('\n'); break;
                    case 'r':  sb.append('\r'); break;
                    case 't':  sb.append('\t'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    case 'u':
                        if (pos + 4 > text.length()) {
                            throw fail("Bad unicode escape");
                        }
                        sb.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default:
                        throw fail("Unknown escape \\" + escape);
                }
            }
        }

        private String readNumber() {
            int start = pos;
            while (pos < text.length() && "0123456789+-.eE".indexOf(text.charAt(pos)) >= 0) {
                pos++;
            }
            if (start == pos) {
                throw fail("Expected a value");
            }
            return text.substring(start, pos);
        }

        private void skipWhitespace() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            if (pos >= text.length()) {
                throw fail("Unexpected end of file");
            }
            return text.charAt(pos);
        }

        private char next() {
            char c = peek();
            pos++;
            return c;
        }

        private void expect(char expected) {
            if (next() != expected) {
                throw fail("Expected '" + expected + "'");
            }
        }

        private IllegalArgumentException fail(String message) {
            return new IllegalArgumentException(message + " at position " + pos);
        }
    }
}