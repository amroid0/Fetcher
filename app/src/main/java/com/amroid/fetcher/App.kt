package com.amroid.fetcher

import android.app.Application
import com.amroid.fetcher.data.di.AppModule
import com.amroid.fetcher.data.di.IAppModule

class App : Application() {
  lateinit var appModule: IAppModule

  override fun onCreate() {
    super.onCreate()
    appModule = AppModule(this)
  }
}