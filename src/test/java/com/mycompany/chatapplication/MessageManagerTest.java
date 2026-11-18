/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.chatapplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part 3 unit tests for the arrays and Stored Messages features,
 * using the five test data messages from the brief.
 */
class MessageManagerTest {

    private Path storageFile;
    private Message msg1;   // Sent
    private Message msg2;   // Stored
    private Message msg3;   // Disregard
    private Message msg4;   // Sent
    private Message msg5;   // Stored

    @BeforeEach
    void setUp(@TempDir Path tmp) {
        storageFile = tmp.resolve("stored.json");
        Message.setStoragePath(storageFile);
        MessageManager.resetAll();

        msg1 = create("1000000001", 0, "+27834557896", "Did you get the cake?");
        msg2 = create("1000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        msg3 = create("1000000003", 2, "+27834484567", "Yohoooo, I am at your gate.");
        msg4 = create("1000000004", 3, "0838884567", "It is dinner time!");
        msg5 = create("1000000005", 4, "+27838884567", "Ok, I am leaving without you.");

        // Flags from the test data: Sent, Stored, Disregard, Sent, Stored
        msg1.SentMessage(1);
        msg2.SentMessage(3);
        msg3.SentMessage(2);
        msg4.SentMessage(1);
        msg5.SentMessage(3);
    }

    /** Builds a message the way the developer test data describes it. */
    private Message create(String id, int number, String recipient, String text) {
        Message m = new Message(id, number, recipient, text);
        m.setSender("kyl_1");
        return m;
    }

    /** The message texts of an array, in order. */
    private static List<String> texts(List<Message> messages) {
        List<String> result = new ArrayList<>();
        for (Message m : messages) {
            result.add(m.getText());
        }
        return result;
    }

    // ---- Sent Messages array correctly populated (test data messages 1-4) ----
    @Test
    void sentMessagesArray_isCorrectlyPopulated() {
        assertEquals(List.of("Did you get the cake?", "It is dinner time!"),
                texts(MessageManager.getSentMessages()));
    }

    // ---- The other arrays are populated ----
    @Test
    void disregardedMessagesArray_isCorrectlyPopulated() {
        assertEquals(List.of("Yohoooo, I am at your gate."),
                texts(MessageManager.getDisregardedMessages()));
    }

    @Test
    void storedMessagesArray_isCorrectlyPopulated() {
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                texts(MessageManager.getStoredMessages()));
    }

    @Test
    void messageHashAndIdArrays_holdEveryMessage() {
        assertEquals(5, MessageManager.getMessageHashes().size());
        assertEquals(5, MessageManager.getMessageIDs().size());
        assertTrue(MessageManager.getMessageHashes().contains(msg3.getMessageHash()));
        assertTrue(MessageManager.getMessageIDs().contains("1000000004"));
    }

    // ---- Stored messages are read back from the JSON file ----
    @Test
    void storedMessagesArray_isReadFromJsonFile() {
        MessageManager.resetAll();
        MessageManager.loadStoredMessages();
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                texts(MessageManager.getStoredMessages()));
        assertEquals("kyl_1", MessageManager.getStoredMessages().get(0).getSender());
        assertEquals(msg2.getMessageHash(), MessageManager.getStoredMessages().get(0).getMessageHash());
        assertEquals(2, MessageManager.getMessageIDs().size());
    }

    @Test
    void loadingTwice_doesNotDuplicateStoredMessages() {
        MessageManager.loadStoredMessages();
        MessageManager.loadStoredMessages();
        assertEquals(2, MessageManager.getStoredMessages().size());
    }

    @Test
    void jsonFile_keepsSpecialCharactersWhenReadBack() {
        Message tricky = create("2000000001", 0, "+27831234567", "He said \"hi\"\nnew line \\ slash\ttab");
        tricky.SentMessage(3);
        MessageManager.resetAll();
        MessageManager.loadStoredMessages();
        assertEquals("He said \"hi\"\nnew line \\ slash\ttab",
                MessageManager.getStoredMessages().get(2).getText());
    }

    // ---- a. Sender and recipient of all stored messages ----
    @Test
    void displaySenderAndRecipient_listsStoredMessages() {
        assertEquals("Sender: kyl_1 | Recipient: +27838884567\n"
                + "Sender: kyl_1 | Recipient: +27838884567",
                MessageManager.displaySenderAndRecipient());
    }

    // ---- b. Display the longest message ----
    @Test
    void longestMessage_isReturned() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                MessageManager.longestStoredMessage());
    }

    @Test
    void longestMessage_noStoredMessages() {
        MessageManager.resetAll();
        assertEquals("No stored messages.", MessageManager.longestStoredMessage());
    }

    // ---- c. Search for a message ID ----
    @Test
    void searchForMessageId_testMessage4() {
        assertEquals("Recipient: 0838884567 | Message: It is dinner time!",
                MessageManager.searchByMessageId("1000000004"));
    }

    @Test
    void searchForMessageId_notFound() {
        assertEquals("Message ID not found.", MessageManager.searchByMessageId("9999999999"));
    }

    // ---- d. Search all messages sent or stored for a particular recipient ----
    @Test
    void searchByRecipient_returnsAllMessagesForThatNumber() {
        assertEquals(List.of("Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                MessageManager.searchByRecipient("+27838884567"));
    }

    @Test
    void searchByRecipient_unknownNumberReturnsNothing() {
        assertTrue(MessageManager.searchByRecipient("+27000000000").isEmpty());
    }

    // ---- e. Delete a message using the message hash ----
    @Test
    void deleteByHash_testMessage2() throws IOException {
        String hash = msg2.getMessageHash();
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" "
                + "successfully deleted.", MessageManager.deleteByHash(hash));

        // removed from the stored array, the hash and ID arrays and the JSON file
        assertEquals(List.of("Ok, I am leaving without you."), texts(MessageManager.getStoredMessages()));
        assertFalse(MessageManager.getMessageHashes().contains(hash));
        assertFalse(MessageManager.getMessageIDs().contains("1000000002"));
        assertFalse(Files.readString(storageFile).contains("1000000002"));
        assertTrue(Files.readString(storageFile).contains("1000000005"));
    }

    @Test
    void deleteByHash_unknownHash() {
        assertEquals("Message hash not found.", MessageManager.deleteByHash("00:0:NOTHERE"));
        assertEquals(2, MessageManager.getStoredMessages().size());
    }

    // ---- f. Display a report ----
    @Test
    void sentMessagesReport_showsHashRecipientAndMessage() {
        String report = MessageManager.sentMessagesReport();
        assertTrue(report.contains("Message Hash: " + msg1.getMessageHash()));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message Hash: " + msg4.getMessageHash()));
        assertTrue(report.contains("Recipient: 0838884567"));
        assertTrue(report.contains("Message: It is dinner time!"));
        assertFalse(report.contains("Where are you?"));
    }

    @Test
    void storedMessagesReport_showsFullDetails() {
        String report = MessageManager.storedMessagesReport();
        assertTrue(report.contains("Message ID: 1000000002"));
        assertTrue(report.contains("Message Hash: " + msg2.getMessageHash()));
        assertTrue(report.contains("Sender: kyl_1"));
        assertTrue(report.contains("Recipient: +27838884567"));
        assertTrue(report.contains("Message: Ok, I am leaving without you."));
        assertFalse(report.contains("Did you get the cake?"));
    }
}