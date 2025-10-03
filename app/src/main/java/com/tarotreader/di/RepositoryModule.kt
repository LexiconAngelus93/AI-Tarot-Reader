package com.tarotreader.di

import com.tarotreader.data.database.TarotDao
import com.tarotreader.data.repository.FirebaseTarotRepository
import com.tarotreader.data.repository.TarotRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideLocalRepository(dao: TarotDao): TarotRepository {
        return TarotRepository(dao)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseRepository(firestore: FirebaseFirestore): FirebaseTarotRepository {
        return FirebaseTarotRepository(firestore)
    }
}