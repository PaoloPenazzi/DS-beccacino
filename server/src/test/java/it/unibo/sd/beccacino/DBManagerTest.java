package it.unibo.sd.beccacino;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {
    DBManager dbManager = new DBManager();

    @AfterEach
    void clearCollection() {
        this.dbManager.getDB().getCollection("players").drop();
    }

    @BeforeEach
    void addItems() {
        this.dbManager.insertDocument(new Document("player_id", "45012"), "players");
        this.dbManager.insertDocument(new Document("player_id", "333"), "players");
        this.dbManager.insertDocument(new Document("player_id", "4"), "players");
    }

    @Test void testCreateDBManager() {
        assertEquals("beccacino", this.dbManager.getDB().getName());
    }

    @Test void testRemoveDocuments() {
        this.dbManager.removeDocument("player_id", "333", "players");
        assertEquals(2, dbManager.getDB().getCollection("players").countDocuments());
    }

    @Test void testRetrieveAllDocuments() {
        ArrayList<Document> playersList = this.dbManager.retrieveAllDocuments("players");
        assertEquals(3, playersList.size());
    }

    @Test void testInsertDocuments() {
        assertEquals(3, dbManager.getDB().getCollection("players").countDocuments());
    }
}