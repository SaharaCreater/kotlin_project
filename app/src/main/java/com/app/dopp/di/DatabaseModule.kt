package com.app.dopp.di

import android.content.Context
import androidx.room.Room
import com.app.dopp.BuildConfig
import com.app.dopp.data.AuthPreferences
import com.app.dopp.data.ScannerManager
import com.app.dopp.data.local.AppDatabase
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_1_2
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_2_3
import com.app.dopp.data.local.AppDatabase.Companion.MIGRATION_3_4
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authPreferences: AuthPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val customUrl = authPreferences.serverUrl
                val requestWithHost = if (!customUrl.isNullOrBlank()) {
                    try {
                        val parsed = java.net.URI(customUrl.trimEnd('/'))
                        val scheme = parsed.scheme ?: "https"
                        val host = parsed.host
                        if (host != null) {
                            val port = if (parsed.port != -1) parsed.port
                                       else if (scheme == "https") 443 else 80
                            val newUrl = chain.request().url.newBuilder()
                                .scheme(scheme)
                                .host(host)
                                .port(port)
                                .build()
                            chain.request().newBuilder().url(newUrl).build()
                        } else chain.request()
                    } catch (_: Exception) {
                        chain.request()
                    }
                } else chain.request()

                val token = authPreferences.token
                val finalRequest = if (token != null) {
                    requestWithHost.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else requestWithHost

                chain.proceed(finalRequest)
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
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
