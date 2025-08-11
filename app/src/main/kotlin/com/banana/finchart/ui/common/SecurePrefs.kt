package com.banana.finchart.ui.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurePrefs(context: Context) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_SCI_CHARTS_API = "sci_charts_api_key"
    }

    private val sharedPrefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getSciChartsApiKey(): String? = sharedPrefs.getString(KEY_SCI_CHARTS_API, null)

    fun saveSciChartsApiKey(key: String) {
        sharedPrefs.edit().putString(KEY_SCI_CHARTS_API, key).apply()
    }

    fun clearSciChartsApiKey() {
        sharedPrefs.edit().remove(KEY_SCI_CHARTS_API).apply()
    }
}