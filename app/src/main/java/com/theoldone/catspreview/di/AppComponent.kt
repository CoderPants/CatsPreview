package com.theoldone.catspreview.di

import android.content.Context
import com.theoldone.catspreview.fragments.CatsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

	fun inject(fragment: CatsFragment)

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun context(context: Context): Builder

		fun build(): AppComponent
	}
}