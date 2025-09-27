package com.tarotreader.di

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.repository.FirebaseTarotRepository
import com.tarotreader.data.repository.TarotRepository
import com.google.firebase.firestore.FirebaseFirestore

object RepositoryModule {
    private var localRepository: TarotRepository? = null
    private var firebaseRepository: FirebaseTarotRepository? = null
    
    fun provideLocalRepository(dao: TarotDao): TarotRepository {
        if (localRepository == null) {
            localRepository = TarotRepository(dao)
        }
        return localRepository!!
    }
    
    fun provideFirebaseRepository(firestore: FirebaseFirestore): FirebaseTarotRepository {
        if (firebaseRepository == null) {
            firebaseRepository = FirebaseTarotRepository(firestore)
        }
        return firebaseRepository!!
    }
}