package com.abdulahad.bookravulk.common

import android.content.Context
import android.telephony.TelephonyManager
import java.util.*

class Utils {
    fun getUserCountry(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            if (simCountry != null && simCountry.length == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US)
            }
            else if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) { // Device is not 3G (would be unreliable)
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US)
                }
            }
        }
        catch (e: Exception) {
        }
        return null
    }
}