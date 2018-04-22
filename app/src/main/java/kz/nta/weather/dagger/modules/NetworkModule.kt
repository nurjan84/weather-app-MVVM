package kz.nta.weather.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.rx_cache2.internal.RxCache
import io.victoralbertos.jolyglot.GsonSpeaker
import kz.nta.weather.BuildConfig
import kz.nta.weather.api.CacheProviders
import kz.nta.weather.dagger.scopes.AppScope
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


@Module
class NetworkModule{


    @AppScope
    @Provides
    fun rxCache(context: Context): CacheProviders {
        return RxCache.Builder()
                .setMaxMBPersistenceCache(5)
                .persistence(context.cacheDir, GsonSpeaker())
                .using(CacheProviders::class.java)
    }

    @AppScope
    @Provides
    fun interceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            chain.proceed(request.build())
        }
    }

    @AppScope
    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }


    @AppScope
    @Provides
    fun okHttpClient(interceptor: Interceptor, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return if(BuildConfig.DEBUG){
            OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(interceptor)
                    .build()
        }else{
            OkHttpClient().newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addNetworkInterceptor(interceptor)
                    .build()
        }
    }
}