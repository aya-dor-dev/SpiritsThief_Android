package com.spiritsthief.common

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.spiritsthief.R
import com.spiritsthief.SpiritsThiefApplication

/**
 * Created by dorayalon on 30/01/2018.
 */
object UserPreferences {
    private const val SP_NAME = "ST_SP"
    private const val FIRST_TIME = "first_time"
    private const val SELECTED_CURRENCY = "selected_currency"
    private const val DELIVERY_COUNTRY = "selected_delivery_country"

    var currency = MutableLiveData<CURRENCY>()
    var country = MutableLiveData<String>()
    var isFirstTime: Boolean = true
        set(value) {
            field = value
            getSharedPref().edit().putBoolean(FIRST_TIME, value).apply()
        }

    init {
        val sp = getSharedPref()

        currency.value = CURRENCY.values()[sp.getInt(SELECTED_CURRENCY, 0)]

        country.value = sp.getString(DELIVERY_COUNTRY, "")

        isFirstTime = sp.getBoolean(FIRST_TIME, true)
    }

    fun setCurrency(ctx: Context, currency: CURRENCY) {
        this.currency.value = currency
        getSharedPref().edit().putInt(SELECTED_CURRENCY, currency.ordinal).apply()
        AnalyticsHelper.setCurrency(currency.getName(ctx))
    }

    fun setDeliveryCountry(country: String) {
        this.country.value = country
        getSharedPref().edit().putString(DELIVERY_COUNTRY, country).apply()

        AnalyticsHelper.setDeliveryCountry(country)
    }

    private fun getSharedPref() =
            SpiritsThiefApplication.get().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
}

enum class CURRENCY {
    GBP,
    EUR,
    USD;

    fun getSymbol(ctx: Context) = when (this) {
        GBP -> ctx.resources.getString(R.string.currency_gbp_symbol)
        EUR -> ctx.resources.getString(R.string.currency_eur_symbol)
        else -> ctx.resources.getString(R.string.currency_usd_symbol)
    }


    fun getName(ctx: Context) = when (this) {
        GBP -> ctx.resources.getString(R.string.currency_gbp)
        EUR -> ctx.resources.getString(R.string.currency_eur)
        else -> ctx.resources.getString(R.string.currency_usd)
    }

    fun getIcon() = when (this) {
        GBP -> R.drawable.ic_currency_gbp
        EUR -> R.drawable.ic_currency_eur
        else -> R.drawable.ic_currency_usd
    }
}