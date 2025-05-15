package com.example.agilelifemanagement.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifiers for different HTTP client configurations
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class AuthenticatedClient

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class UnauthenticatedClient

/**
 * Network related constants
 */
private const val CONNECT_TIMEOUT = 15L
private const val READ_TIMEOUT = 15L
private const val WRITE_TIMEOUT = 15L

/**
 * Module that provides network-related dependencies including
 * HTTP client configurations for the application.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides the base JSON configuration for serialization/deserialization
     */
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    /**
     * Provides a network connection checker
     */
    @Singleton
    @Provides
    fun provideNetworkConnectivityChecker(
        @ApplicationContext context: Context
    ): NetworkConnectivityChecker {
        return NetworkConnectivityCheckerImpl(context)
    }

    /**
     * Provides the basic Ktor HttpClient with common configurations
     */
    @UnauthenticatedClient
    @Singleton
    @Provides
    fun provideHttpClient(json: Json): HttpClient {
        return HttpClient(Android) {
            // Configure timeout
            engine {
                connectTimeout = TimeUnit.SECONDS.toMillis(CONNECT_TIMEOUT)
                socketTimeout = TimeUnit.SECONDS.toMillis(READ_TIMEOUT)
            }

            // Install ContentNegotiation plugin for JSON serialization/deserialization
            install(ContentNegotiation) {
                json(json)
            }

            // Configure logging for network requests/responses
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("Ktor").d(message)
                    }
                }
                level = LogLevel.HEADERS
            }

            // Observer for all responses
            install(ResponseObserver) {
                onResponse { response ->
                    Timber.tag("Ktor").d("Response: ${response.status.value}")
                }
            }

            // Default request configuration
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }

    /**
     * Provides an authenticated HttpClient that includes auth token in requests
     */
    @AuthenticatedClient
    @Singleton
    @Provides
    fun provideAuthenticatedHttpClient(
        @UnauthenticatedClient baseClient: HttpClient,
        authTokenProvider: AuthTokenProvider
    ): HttpClient {
        return baseClient.config {
            install(DefaultRequest) {
                // Add authentication header to each request
                header(HttpHeaders.Authorization, "Bearer ${authTokenProvider.getToken()}")
            }
        }
    }

    /**
     * Provides an auth token provider
     */
    @Singleton
    @Provides
    fun provideAuthTokenProvider(): AuthTokenProvider {
        return AuthTokenProviderImpl()
    }
}

/**
 * Interface for checking network connectivity
 */
interface NetworkConnectivityChecker {
    fun isNetworkAvailable(): Boolean
}

/**
 * Implementation of network connectivity checker
 */
class NetworkConnectivityCheckerImpl @Inject constructor(
    private val context: Context
) : NetworkConnectivityChecker {

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) 
                as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }
}

/**
 * Interface for providing authentication tokens
 */
interface AuthTokenProvider {
    fun getToken(): String?
    fun setToken(token: String?)
    fun clearToken()
}

/**
 * Implementation of auth token provider
 */
class AuthTokenProviderImpl @Inject constructor() : AuthTokenProvider {
    private var token: String? = null

    override fun getToken(): String? = token

    override fun setToken(token: String?) {
        this.token = token
    }

    override fun clearToken() {
        token = null
    }
}
