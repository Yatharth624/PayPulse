package com.paypulse.ai.ui.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paypulse.ai.data.model.LoanItem

object LoanStorageManager {
    private const val PREFS_NAME = "paypulse_loans_prefs"
    private const val KEY_LOANS = "stored_loans_json"
    private val gson = Gson()

    fun saveLoans(context: Context, loans: List<LoanItem>) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = gson.toJson(loans)
            prefs.edit().putString(KEY_LOANS, json).apply()
        } catch (_: Exception) {}
    }

    fun loadLoans(context: Context): List<LoanItem>? {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_LOANS, null) ?: return null
            val type = object : TypeToken<List<LoanItem>>() {}.type
            gson.fromJson<List<LoanItem>>(json, type)
        } catch (e: Exception) {
            null
        }
    }
}
