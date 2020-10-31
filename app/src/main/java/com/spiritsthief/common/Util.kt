package com.spiritsthief.common

import android.content.res.Resources
import android.icu.util.ULocale.getCountry
import android.icu.util.ULocale.getISO3Country
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


/**
 * Created by dorayalon on 08/02/2018.
 */

fun DP(dp: Int) = Resources.getSystem().displayMetrics.density * dp

fun getListedSelection(selection: List<String>, defaultValue: String = ""): String {
    val text = StringBuilder()
    for (v in selection) {
        text.append(v).append(", ")
    }

    var string = text.toString()
    if (string.isNotEmpty()) {
        string = string.removeRange(string.length - 2, string.length)
    }

    if (string.isEmpty()) {
        return defaultValue
    }
    return string
}

fun getCountriesOptions(): Pair<List<Country>, List<Country>> {
    val specialCountryCodes = listOf("FR", "GB", "US", "CA", "DE", "NL", "SP", "JP", "IT", "BE", "FR", "IE", "AU")
    val specialCountries = mutableListOf<Country>()
    val countries = mutableListOf<Country>()

    // Get ISO countries, create Country object and
    // store in the collection.
    val isoCountries = Locale.getISOCountries()
    for (country in isoCountries) {
        val locale = Locale("en", country)
        val iso = locale.isO3Country
        val code = locale.country
        val name = locale.displayCountry

        if ("" != iso && "" != code && "" != name) {
            if (specialCountryCodes.contains(code)) {
                specialCountries.add(Country(iso, code, name))
            } else {
                countries.add(Country(iso, code, name))
            }
        }
    }

    // Sort the country by their name and then display the content
    // of countries collection object.
    return Pair(specialCountries.sortedBy { it.name }, countries.sortedBy { it.name })
}

fun getCountries(): MutableList<Country> {
    val countries = mutableListOf<Country>()

    // Get ISO countries, create Country object and
    // store in the collection.
    val isoCountries = Locale.getISOCountries()
    for (country in isoCountries) {
        val locale = Locale("en", country)
        val iso = locale.isO3Country
        val code = locale.country
        val name = locale.displayCountry

        if ("" != iso && "" != code && "" != name) {
            countries.add(Country(iso, code, name))
        }
    }

    // Sort the country by their name and then display the content
    // of countries collection object.
    return countries.sortedBy { it.name }.toMutableList()
}

fun getCountryFlagEmoji(country: String?): String {
    if (country == null || country.isEmpty()) return ""

    val flagOffset = 0x1F1E6
    val asciiOffset = 0x41

    val firstChar = Character.codePointAt(country, 0) - asciiOffset + flagOffset
    val secondChar = Character.codePointAt(country, 1) - asciiOffset + flagOffset

    val flag = String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))

    return flag
}


fun getDeliveryCountryName() = getListedSelection(getCountries().filter { UserPreferences.country.value!! == it.code }.map { it.name })


public class Country(val iso: String,
                     val code: String,
                     val name: String): Serializable

