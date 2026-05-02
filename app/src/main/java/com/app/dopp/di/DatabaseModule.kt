package com.app.dopp.di

import android.content.Context
import androidx.room.Room
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.local.AppDatabase
import com.app.dopp.data.local.PhysicsDao
import com.app.dopp.data.remote.PhysicsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/your_username/your_repo/main/") // Замени на свой URL позже
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePhysicsApi(retrofit: Retrofit): PhysicsApi {
        return retrofit.create(PhysicsApi::class.java)
    }
    @Provides
    @Singleton
    fun provideScanner(@ApplicationContext context: Context): ScannerManager {
        return ScannerManager(context)
    }
}