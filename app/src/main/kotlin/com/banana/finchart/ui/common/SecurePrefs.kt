package com.banana.finchart.ui.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class SecurePrefs(context: Context) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_SCI_CHARTS_API = "sci_charts_api_key"
    }

    private val sharedPrefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context.applicationContext,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getSciChartsApiKey(): String? = sharedPrefs.getString(KEY_SCI_CHARTS_API, null)

    fun saveSciChartsApiKey(key: String) {
        sharedPrefs.edit { putString(KEY_SCI_CHARTS_API, key) }
    }

    fun clearSciChartsApiKey() {
        sharedPrefs.edit { remove(KEY_SCI_CHARTS_API) }
    }
}