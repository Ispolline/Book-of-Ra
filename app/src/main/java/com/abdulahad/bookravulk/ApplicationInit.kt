package com.abdulahad.bookravulk

import android.app.Application
import com.appsflyer.AppsFlyerLib
import com.yandex.metrica.YandexMetrica

import com.yandex.metrica.YandexMetricaConfig




class ApplicationInit: Application() {

    private val API_key = "b3187452-82fe-49ce-9a51-f7c2df1e64ad"

    companion object{
        @get:Synchronized
        lateinit  var initializer: ApplicationInit
            private set
    }

    override fun onCreate() {
        super.onCreate()
        initializer = this

        // Creating an extended library configuration.
        // Creating an extended library configuration.
        val config = YandexMetricaConfig.newConfigBuilder(API_key).build()
        // Initializing the AppMetrica SDK.
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)

        AppsFlyerLib.getInstance().init("bSiQz4zRRTErHDbyxPM6fg", null, this)
        AppsFlyerLib.getInstance().start(this)
    }

}
