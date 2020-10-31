package com.spiritsthief.ui.ui.main.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.ApiRequest
import com.spiritsthief.api.model.Stats
import com.spiritsthief.common.*
import com.spiritsthief.ui.ui.main.BaseFragment
import com.spiritsthief.ui.ui.common.SingleSelectionAdapter
import com.spiritsthief.ui.ui.main.RecyclerViewFragment
import com.spiritsthief.views.BottleDataRecord
import kotlinx.android.synthetic.main.recycler_view_fragment.*


/**
 * Created by dorayalon on 30/01/2018.
 */
class SettingsFragment : RecyclerViewFragment() {
    private val SETTINGS_DELIVERY_COUNTRY = 0
    private val SETTINGS_CURRENCY = 1
    private val SETTINGS_STATISTICS = 2
    private val SETTINGS_SHOP_STATISTICS = 3
    private val SETTINGS_CONTACT_US = 4

    lateinit var settings: List<Quadrouple<Int, Int, Int?, String?>>
    var adapter: SettingsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            attachRecyclerViewToScrollListener(this)
        }

        refreshData()

        setTitle(R.string.settings)
    }

    private fun refreshData() {
        var deliveryCountry = getDeliveryCountryName()
        Log.d("DEL_COUNT", deliveryCountry)
        if (deliveryCountry == "") deliveryCountry = activity!!.resources.getString(R.string.any)

        settings = listOf(
                Quadrouple(SETTINGS_DELIVERY_COUNTRY, R.string.delivery_country, null, deliveryCountry),
                Quadrouple(SETTINGS_CURRENCY, R.string.currency, null, UserPreferences.currency.value!!.getSymbol(activity!!)),
                Quadrouple(SETTINGS_STATISTICS, R.string.statistics, null, null),
                Quadrouple(SETTINGS_SHOP_STATISTICS, R.string.stores_statistics, null, null),
                Quadrouple(SETTINGS_CONTACT_US, R.string.contact_us, null, null))

        if (adapter == null) {
            adapter = SettingsAdapter(settings, onSettingSelected)
            recycler_view.adapter = adapter
        } else {
            adapter!!.settings = settings
            adapter!!.notifyDataSetChanged()
        }
    }

    private val onSettingSelected: (Int) -> Unit = {
        when (it) {
            SETTINGS_DELIVERY_COUNTRY -> openDeliveryCountry()
            SETTINGS_CURRENCY -> openCurrency()
            SETTINGS_CONTACT_US -> contactUs()
            SETTINGS_STATISTICS -> openStats()
            SETTINGS_SHOP_STATISTICS -> openShopStats()
        }
    }

    private fun contactUs() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:info@thespiritthief.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us")
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent)
        }

    }

    private fun openDeliveryCountry() {
        val list = RecyclerView(activity!!)
        list.layoutManager = LinearLayoutManager(activity!!)
        val options = getCountriesOptions()
        val countries = mutableListOf<Country?>()
        countries.addAll(options.first)
        countries.add(null)
        countries.addAll(options.second)

        val any = activity!!.resources.getString(R.string.any)
        var selectedCode = UserPreferences.country.value!!
        var selectedValue = when(selectedCode) {
            "" -> any
            else -> countries.filter { it?.code == selectedCode}[0]!!.name
        }

        AlertDialog.Builder(activity!!)
                .setTitle(R.string.delivery_country)
                .setView(list)
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .setPositiveButton(R.string.ok) { dialog, which ->
                    Log.d("DEL_COUNT", selectedCode)
                    UserPreferences.setDeliveryCountry(selectedCode)
                    refreshData()
                    dialog.dismiss()
                }
                .show()

        val countryNames = mutableListOf<String?>()
        countryNames.addAll(options.first.map { it.name }.toMutableList())
        countryNames.add(null)
        countryNames.addAll(options.second.map { it.name }.toMutableList())
        countryNames.add(0, any)
        list.adapter = SingleSelectionAdapter(countryNames, selectedValue) {selectedName ->
            selectedCode = if (selectedName == any) {
                ""
            } else {
                 countries.filter { it?.name == selectedName }[0]!!.code
            }

            Log.d("DEL_COUNT", "$selectedName $selectedCode")
        }
    }

    private fun openCurrency() {
        var selected = UserPreferences.currency.value
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.currency)
                .setView(R.layout.fragment_currency)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    UserPreferences.setCurrency(activity!!, selected!!)
                    refreshData()
                }
                .setNegativeButton(R.string.cancel, { dialog, which -> })
                .show()
        dialog.findViewById<RadioGroup>(R.id.group)!!.setOnCheckedChangeListener { group, checkedId ->
            selected = CURRENCY.values()[(dialog.findViewById<RadioButton>(checkedId)!!.tag as String).toInt()]
        }

        when (selected) {
            CURRENCY.GBP -> dialog.findViewById<RadioButton>(R.id.gbp)?.isChecked = true
            CURRENCY.USD -> dialog.findViewById<RadioButton>(R.id.usd)?.isChecked = true
            CURRENCY.EUR -> dialog.findViewById<RadioButton>(R.id.eur)?.isChecked = true
        }
    }


    private fun openStats() {
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.statistics)
                .setView(R.layout.view_stats)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        val fetcher = StatsFetcher()
        fetcher.stats.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                dialog.findViewById<View>(R.id.progress)?.visibility = View.GONE
                dialog.findViewById<View>(R.id.data)?.visibility = View.VISIBLE
                dialog.findViewById<BottleDataRecord>(R.id.bottles)?.subtitle?.text = it[0].products.toString()
                dialog.findViewById<BottleDataRecord>(R.id.brands)?.subtitle?.text = it[0].brands.toString()
                dialog.findViewById<BottleDataRecord>(R.id.bottlers)?.subtitle?.text = it[0].bottlers.toString()
                dialog.findViewById<BottleDataRecord>(R.id.stores)?.subtitle?.text = it[0].stores.toString()
                dialog.findViewById<BottleDataRecord>(R.id.links)?.subtitle?.text = it[0].links.toString()
            }
        })

        AsyncTask.THREAD_POOL_EXECUTOR.execute(fetcher)

    }

    private fun openShopStats() {
        val list: ListView = ListView(activity)

        val dialog = AlertDialog.Builder(activity!!)
                .setTitle(R.string.stores_statistics)
                .setView(list)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        val fetcher = ShopStatsFetcher()
        fetcher.stats.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                list.adapter = ShopStatsAdapter(activity!!, it)
            }
        })

        AsyncTask.THREAD_POOL_EXECUTOR.execute(fetcher)
    }


    private class StatsFetcher : Runnable {
        val stats = MutableLiveData<List<Stats>>()

        override fun run() {
            try {
                stats.postValue(ApiClient.get().getStats().execute().body())
            } catch (e: Exception) {
                stats.postValue(null)
            }
        }

    }

    private class ShopStatsFetcher : Runnable {
        val stats = MutableLiveData<List<Stats>>()

        override fun run() {
            try {
                stats.postValue(ApiClient.get().getShopStats(ApiRequest()).execute().body())
            } catch (e: Exception) {
                stats.postValue(null)
            }
        }

    }

    class ShopStatsAdapter(context: Context,
                           private val stats: List<Stats>) : ArrayAdapter<Stats>(context, 0, stats) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(context).inflate(R.layout.store_list_row, parent, false)
            view.findViewById<TextView>(R.id.count).text = stats[position].links.toString()
            view.findViewById<TextView>(R.id.store_name).text = stats[position].name

            val flag = view.findViewById<TextView>(R.id.flag)

            flag.text = getCountryFlagEmoji(stats[position].countryCode)
            return view
        }

        override fun getCount() = stats.size

    }
}
