package com.tarotreader

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.tarotreader.di.DatabaseModule

class TarotReaderApplication : Application() {
    lateinit var firestore: FirebaseFirestore
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()
        
        // Initialize database
        DatabaseModule.provideDatabaseInitializer(this).initializeDatabase()
    }
}