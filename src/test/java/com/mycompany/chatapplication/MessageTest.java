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

import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the Message class, using the test data from the brief. */
class MessageTest {

    private static final String ID1 = "0012345678";
    private static final String ID2 = "9988776655";

    private Path storageFile;
    private Message msg1;   // Test case 1: valid number, will be sent
    private Message msg2;   // Test case 2: invalid number, will be discarded

    @BeforeEach
    void setUp(@TempDir Path tmp) {
        storageFile = tmp.resolve("stored.json");
        Message.resetSentMessages();
        Message.setStoragePath(storageFile);
        msg1 = new Message(ID1, 0, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg2 = new Message(ID2, 1, "08575975889", "Hi Keegan, did you receive the payment?");
    }

    // ---- The message should not be more than 250 characters ----
    @Test
    void messageLength_success() {
        assertEquals("Message ready to send.", msg1.checkMessageLength());
    }

    @Test
    void messageLength_failure() {
        Message tooLong = new Message(ID1, 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10, please reduce the size.",
                tooLong.checkMessageLength());
    }

    @Test
    void messageLength_exactly250IsAllowed() {
        Message limit = new Message(ID1, 0, "+27718693002", "a".repeat(250));
        assertEquals("Message ready to send.", limit.checkMessageLength());
    }

    // ---- The recipient number is correctly formatted (re-uses the Part 1 check) ----
    @Test
    void recipient_success() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    @Test
    void recipient_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                msg2.checkRecipientCell());
    }

    // ---- Message hash is correct ----
    @Test
    void messageHash_testCase1() {
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());
    }

    @Test
    void messageHash_remainingMessagesInLoop() {
        // Remaining hashes are checked in a loop (message number = loop counter)
        String[] ids = {ID1, ID2};
        String[] texts = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };
        String[] expected = {"00:0:HITONIGHT", "99:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], i, "+27718693002", texts[i]);
            assertEquals(expected[i], m.createMessageHash());
            assertEquals(expected[i], m.getMessageHash());
        }
    }

    // ---- Message ID is created ----
    @Test
    void messageId_isCreated() {
        Message m = new Message(0, "+27718693002", "Hello there");
        assertTrue(m.getMessageId().matches("\\d{10}"));
        assertTrue(m.checkMessageID());
        assertEquals("Message ID generated: " + m.getMessageId(), m.messageIdGenerated());
    }

    @Test
    void messageId_alwaysTenDigitsInLoop() {
        for (int i = 0; i < 100; i++) {
            assertTrue(Message.generateMessageId().matches("\\d{10}"));
        }
    }

    @Test
    void messageId_tooLongIsRejected() {
        Message m = new Message("12345678901", 0, "+27718693002", "Hello there");
        assertFalse(m.checkMessageID());
    }

    // ---- MessageSent ----
    @Test
    void sentMessage_send() {
        assertEquals("Message successfully sent.", msg1.SentMessage(1));
    }

    @Test
    void sentMessage_discard() {
        assertEquals("Press 0 to delete the message.", msg2.SentMessage(2));
    }

    @Test
    void sentMessage_store() {
        assertEquals("Message successfully stored.", msg1.SentMessage(3));
    }

    // ---- Message details display in the correct order ----
    @Test
    void details_inCorrectOrder() {
        String expected = "Message ID: " + ID1 + "\n"
                + "Message Hash: 00:0:HITONIGHT\n"
                + "Recipient: +27718693002\n"
                + "Message: Hi Mike, can you join us for dinner tonight?";
        assertEquals(expected, msg1.details());
    }

    // ---- Total messages sent ----
    @Test
    void totalMessages_countsOnlySentMessages() {
        msg1.SentMessage(1);   // sent
        msg2.SentMessage(2);   // discarded
        assertEquals(1, Message.returnTotalMessages());
        assertTrue(Message.printMessages().contains(ID1));
        assertFalse(Message.printMessages().contains(ID2));
    }

    @Test
    void totalMessages_allSent() {
        msg1.SentMessage(1);
        new Message(ID2, 1, "+27825550001", "Hi Keegan, did you receive the payment?").SentMessage(1);
        assertEquals(2, Message.returnTotalMessages());
    }

    // ---- Message is stored as a JSON file ----
    @Test
    void storeMessage_writesJsonFile() throws IOException {
        msg1.SentMessage(3);
        assertTrue(Files.exists(storageFile));
        String json = Files.readString(storageFile);
        assertTrue(json.contains(ID1));
        assertTrue(json.contains("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    void storeMessage_fileIsAJsonArray() throws IOException {
        msg1.SentMessage(3);
        String json = Files.readString(storageFile).trim();
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("\"messageHash\": \"00:0:HITONIGHT\""));
    }

    @Test
    void storeMessage_appendsToExistingFile() throws IOException {
        msg1.SentMessage(3);
        new Message(ID2, 1, "+27825550001", "Hi Keegan, did you receive the payment?").SentMessage(3);
        String json = Files.readString(storageFile);
        assertTrue(json.contains(ID1));
        assertTrue(json.contains(ID2));
    }
}