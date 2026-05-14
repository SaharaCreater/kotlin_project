package com.app.dopp.di

import android.content.Context
import androidx.room.Room
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.local.AppDatabase
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_1_2
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
        val baseUrl = System.getenv("REPLIT_DEV_DOMAIN")
            ?.let { "https://$it/" }
            ?: "http://10.0.2.2:5000/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
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

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "physics_database"
        ).addMigrations(MIGRATION_1_2).build()
    }

    @Provides
    @Singleton
    fun providePhysicsDao(database: AppDatabase): PhysicsDao {
        return database.dao()
    }
}
