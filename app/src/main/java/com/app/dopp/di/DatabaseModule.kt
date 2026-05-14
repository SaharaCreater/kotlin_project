package com.app.dopp.di

import android.content.Context
import androidx.room.Room
import com.app.dopp.BuildConfig
import com.app.dopp.data.AuthPreferences
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.local.AppDatabase
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_1_2
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_2_3
import com.app.dopp.data.local.PhysicsDao
import com.app.dopp.data.local.ProgressDao
import com.app.dopp.data.remote.PhysicsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authPreferences: AuthPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = authPreferences.token
                val request = if (token != null) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .client(okHttpClient)
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
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun providePhysicsDao(database: AppDatabase): PhysicsDao {
        return database.dao()
    }

    @Provides
    @Singleton
    fun provideProgressDao(database: AppDatabase): ProgressDao {
        return database.progressDao()
    }
}
