package com.theoldone.catspreview.di

import android.app.Application
import com.theoldone.catspreview.CatsApplication
import com.theoldone.catspreview.di.qualifiers.ApiKey
import com.theoldone.catspreview.di.qualifiers.Host
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
	modules = [
		AppModule::class,
		ServerModule::class,
		AndroidSupportInjectionModule::class,
		ActivityBuildersModule::class,
		ViewModelFactoryModule::class
	]
)
@Singleton
interface AppComponent : AndroidInjector<CatsApplication> {

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		@BindsInstance
		fun apiKey(@ApiKey apiKey: String): Builder

		@BindsInstance
		fun host(@Host host: String): Builder

		fun build(): AppComponent
	}
}