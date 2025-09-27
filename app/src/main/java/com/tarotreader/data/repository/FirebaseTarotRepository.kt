package com.tarotreader.data.repository

import com.tarotreader.data.model.*
import com.tarotreader.domain.repository.TarotRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseTarotRepository(private val firestore: FirebaseFirestore) : TarotRepository {
    
    override fun getAllCards(): Flow<List<TarotCard>> = flow {
        val cards = firestore.collection("cards")
            .get()
            .await()
            .toObjects(TarotCard::class.java)
        emit(cards)
    }
    
    override fun getCardsByDeck(deckId: String): Flow<List<TarotCard>> = flow {
        val cards = firestore.collection("cards")
            .whereEqualTo("deckId", deckId)
            .get()
            .await()
            .toObjects(TarotCard::class.java)
        emit(cards)
    }
    
    override suspend fun getCardById(cardId: String): TarotCard? {
        return try {
            firestore.collection("cards")
                .document(cardId)
                .get()
                .await()
                .toObject(TarotCard::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun insertCard(card: TarotCard) {
        firestore.collection("cards")
            .document(card.id)
            .set(card)
            .await()
    }
    
    override suspend fun insertCards(cards: List<TarotCard>) {
        val batch = firestore.batch()
        cards.forEach { card ->
            val docRef = firestore.collection("cards").document(card.id)
            batch.set(docRef, card)
        }
        batch.commit().await()
    }
    
    override fun getAllDecks(): Flow<List<TarotDeck>> = flow {
        val decks = firestore.collection("decks")
            .get()
            .await()
            .toObjects(TarotDeck::class.java)
        emit(decks)
    }
    
    override suspend fun getDeckById(deckId: String): TarotDeck? {
        return try {
            firestore.collection("decks")
                .document(deckId)
                .get()
                .await()
                .toObject(TarotDeck::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun insertDeck(deck: TarotDeck) {
        firestore.collection("decks")
            .document(deck.id)
            .set(deck)
            .await()
    }
    
    override suspend fun insertDecks(decks: List<TarotDeck>) {
        val batch = firestore.batch()
        decks.forEach { deck ->
            val docRef = firestore.collection("decks").document(deck.id)
            batch.set(docRef, deck)
        }
        batch.commit().await()
    }
    
    override fun getAllSpreads(): Flow<List<TarotSpread>> = flow {
        val spreads = firestore.collection("spreads")
            .get()
            .await()
            .toObjects(TarotSpread::class.java)
        emit(spreads)
    }
    
    override suspend fun getSpreadById(spreadId: String): TarotSpread? {
        return try {
            firestore.collection("spreads")
                .document(spreadId)
                .get()
                .await()
                .toObject(TarotSpread::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun insertSpread(spread: TarotSpread) {
        firestore.collection("spreads")
            .document(spread.id)
            .set(spread)
            .await()
    }
    
    override suspend fun insertSpreads(spreads: List<TarotSpread>) {
        val batch = firestore.batch()
        spreads.forEach { spread ->
            val docRef = firestore.collection("spreads").document(spread.id)
            batch.set(docRef, spread)
        }
        batch.commit().await()
    }
    
    override fun getAllReadings(): Flow<List<Reading>> = flow {
        val readings = firestore.collection("readings")
            .get()
            .await()
            .toObjects(Reading::class.java)
        emit(readings)
    }
    
    override suspend fun getReadingById(readingId: String): Reading? {
        return try {
            firestore.collection("readings")
                .document(readingId)
                .get()
                .await()
                .toObject(Reading::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun insertReading(reading: Reading) {
        firestore.collection("readings")
            .document(reading.id)
            .set(reading)
            .await()
    }
}