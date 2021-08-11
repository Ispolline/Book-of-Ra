package com.abdulahad.bookravulk.common

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.abdulahad.bookravulk.Hint

object PreferenceHelper {

    val prefName = "MyPref"

    val IS_User = "IS_User"
    val SMS_TOKEN = "SMS_TOKEN"
    val PHONE = "PHONE"
    val nav = "nav"
    val url = "URL"
    val backgroundMusic = "backgroundMusic"
    val slotSoung = "slot"
    val finishAuth = "finish"
    val passCode = "passCode"
    val hintID = "hintID"
    val pushSlot = "pushSlot"
//    val USER_PASSWORD = "PASSWORD"
//    val TOKEN = "TOKEN"
//    val UPDATE_TOKEN = "UPDATE_TOKEN"

    fun defaultPreference(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context): SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.is_user
        get() = getInt(IS_User, 0)
        set(value) {
            editMe {
                it.putInt(IS_User, value)
            }
        }

    var SharedPreferences.sms_token
        get() = getString(SMS_TOKEN, "")
        set(value) {
            editMe {
                it.putString(SMS_TOKEN, value)
            }
        }

    var SharedPreferences.phone
        get() = getString(PHONE, "")
        set(value) {
            editMe {
                it.putString(PHONE, value)
            }
        }

    var SharedPreferences.urlLast
        get() = getString(url, "")
        set(value) {
            editMe {
                it.putString(url, value)
            }
        }

    var SharedPreferences.navigation
        get() = getStringSet(nav, null)
        set(value) {
            editMe {
                it.putStringSet(nav, value)
            }
        }


    var SharedPreferences.music
        get() = getBoolean(backgroundMusic, true)
        set(value) {
            editMe {
                it.putBoolean(backgroundMusic, value)
            }
        }

    var SharedPreferences.slot
        get() = getBoolean(slotSoung, true)
        set(value) {
            editMe {
                it.putBoolean(slotSoung, value)
            }
        }

    var SharedPreferences.finish
        get() = getBoolean(finishAuth, false)
        set(value) {
            editMe {
                it.putBoolean(finishAuth, value)
            }
        }

    var SharedPreferences.codePass
        get() = getInt(passCode, 0)
        set(value) {
            editMe {
                it.putInt(passCode, value)
            }
        }

    var SharedPreferences.hint
        get() = getString(hintID, Hint.BALANCE.toString())
        set(value) {
            editMe {
                it.putString(hintID, value)
            }
        }

    var SharedPreferences.isFirstPushSlot
        get() = getBoolean(pushSlot, true)
        set(value) {
            editMe {
                it.putBoolean(pushSlot, value)
            }
        }
//    var SharedPreferences.token
//        get() = getString(TOKEN, "")
//        set(value) {
//            editMe {
//                it.putString(TOKEN, value)
//            }
//        }
//
//    var SharedPreferences.update_token
//        get() = getBoolean(UPDATE_TOKEN, true)
//        set(value) {
//            editMe {
//                it.putBoolean(UPDATE_TOKEN, value)
//            }
//        }
//
//    var SharedPreferences.password
//        get() = getString(USER_PASSWORD, "")
//        set(value) {
//            editMe {
//                it.putString(USER_PASSWORD, value)
//            }
//        }
//
//    var SharedPreferences.clearValues
//        get() = { }
//        set(value) {
//            editMe {
//                it.clear()
//            }
//        }
}