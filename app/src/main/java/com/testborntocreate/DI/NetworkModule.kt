package com.testborntocreate.DI

import android.content.Context
import android.util.Log
import com.testborntocreate.BuildConfig
import com.testborntocreate.Data.Remote.APIService
import com.testborntocreate.Utils.isOnline
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = arrayOf(AppModule::class))
class NetworkModule {

    companion object {
        const val BASE_URL = "http://excercise.born-to-create.de/"
    }

    @Singleton
    @Provides
    fun provideAPI(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    fun provideCache(cache: File): Cache = Cache(cache, 10 * 1024 * 1024)

    @Singleton
    @Provides
    fun provideFile(context: Context): File = File(context.cacheDir, "okhttp_cache")

    @Singleton
    @Provides
    fun provideInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            Log.v("LoggingInterceptor", it)
        })
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor, cache: Cache, context: Context): OkHttpClient {

        val onlineInterceptor = Interceptor { chain ->
            var request = chain.request()
            request = if (isOnline(context)) {
                request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build()
            } else {
                request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                    .build()
            }
            chain.proceed(request)
        }

        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(onlineInterceptor)
                .build()
        } else {
            OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(onlineInterceptor)
                .build()
        }
    }
}