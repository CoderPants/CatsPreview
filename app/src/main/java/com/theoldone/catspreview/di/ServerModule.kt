package com.theoldone.catspreview.di

import com.theoldone.catspreview.di.qualifiers.ApiKey
import com.theoldone.catspreview.di.qualifiers.AuthInterceptor
import com.theoldone.catspreview.di.qualifiers.Host
import com.theoldone.catspreview.server.CatsApi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ServerModule {
	@Provides
	@AuthInterceptor
	fun provideAuthInterceptor(@ApiKey apiKey: String) = Interceptor { chain ->
		val requestBuilder = chain.request().newBuilder()
		if (apiKey.isNotBlank())
			requestBuilder.header("x-api-key", apiKey)
		return@Interceptor chain.proceed(requestBuilder.build())
	}

	@Provides
	fun provideLoggerInterceptor(): Interceptor = HttpLoggingInterceptor().apply { level = BODY }

	@Provides
	fun provideOkHttpClient(@AuthInterceptor authInterceptor: Interceptor, loggerInterceptor: Interceptor) = OkHttpClient.Builder()
		.addInterceptor(authInterceptor)
		.addInterceptor(loggerInterceptor)
		.build()


	@Provides
	@Singleton
	fun provideRetrofit(@Host host: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
		.baseUrl(host)
		.client(client)
		.addConverterFactory(GsonConverterFactory.create())
		.build()

	@Provides
	fun provideCatsApi(retrofit: Retrofit): CatsApi = retrofit.create(CatsApi::class.java)
}