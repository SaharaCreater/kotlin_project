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
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

class HostSelectionInterceptor(private val authPreferences: AuthPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val customUrl = authPreferences.serverUrl?.toHttpUrlOrNull()
        val request = if (customUrl != null) {
            val newUrl = chain.request().url.newBuilder()
                .scheme(customUrl.scheme)
                .host(customUrl.host)
                .port(customUrl.port)
                .build()
            chain.request().newBuilder().url(newUrl).build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authPreferences: AuthPreferences): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HostSelectionInterceptor(authPreferences))
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
