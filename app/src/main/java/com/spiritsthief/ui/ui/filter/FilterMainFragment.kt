package com.spiritsthief.ui.ui.filter

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.ApiRequest
import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.common.Quadrouple
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.common.toStringEllipsize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class FilterMainFragment : BaseFilterFragment() {
    val FILTER_CATEGORY = 0
    val FILTER_SUB_CATEGORY = 1
    val FILTER_COUNTRY = 2
    val FILTER_REGION = 3
    val FILTER_BRAND = 4
    val FILTER_BOTTLED_BY = 5
    val FILTER_PRICE = 6
    val FILTER_ABV = 7
    val FILTER_AGE = 8
    val FILTER_VINTAGE = 9
    val FILTER_BOTTLING_YEAR = 10
    val FILTER_MATURED_IN = 11
    val FILTER_CASK_SIZE = 12
    val FILTER_WOOD_TYPE = 13
    val FILTER_REFILL = 14
    val FILTER_ONLY_VERIFIED = 15

    var adapter: FilterAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        val recyclerView = RecyclerView(activity!!)
        recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        recyclerView.layoutManager = LinearLayoutManager(activity!!)

        return recyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.filter_main_menu, menu)
        menu?.findItem(R.id.clear)?.actionView?.findViewById<Button>(R.id.clear_btn)?.setOnClickListener {
            filterViewModel.clear()
            initAdapter()
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.filter)

        initAdapter()
    }

    fun initAdapter() {
        if (adapter == null) {
            adapter = FilterAdapter(createFilterData(), onFilterClicked)
            (view as RecyclerView).adapter = adapter
        } else {
            adapter?.apply {
                list = createFilterData()
                notifyDataSetChanged()
            }
        }
    }

    private fun createFilterData(): List<Quadrouple<FilterAdapter.TYPE, Int, String, String>?> {
        val data = mutableListOf<Quadrouple<FilterAdapter.TYPE, Int, String, String>?>()

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_CATEGORY,
                getString(R.string.filter_title_category),
                filterViewModel.searchRequest.category.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_SUB_CATEGORY,
                getString(R.string.filter_title_sub_category),
                filterViewModel.searchRequest.subcategory.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_COUNTRY,
                getString(R.string.filter_title_country),
                filterViewModel.searchRequest.country.toStringEllipsize(resources)))

        val regionEnabled = filterViewModel.searchRequest.country.size == 1 && filterViewModel.searchRequest.country[0].equals("scotland", ignoreCase = true)
        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_REGION,
                getString(R.string.filter_title_region),
                if (regionEnabled) filterViewModel.searchRequest.region.toStringEllipsize(resources) else null))

        data.add(null)

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_BRAND,
                getString(R.string.filter_title_brand),
                filterViewModel.searchRequest.brand.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_BOTTLED_BY,
                getString(R.string.filter_title_bottler),
                filterViewModel.searchRequest.bottler.toStringEllipsize(resources)))

        data.add(null)

        data.add(Quadrouple(FilterAdapter.TYPE.TOGGLE,
                FILTER_ONLY_VERIFIED,
                getString(R.string.filter_only_verified),
                filterViewModel.searchRequest.onlyVerified().toString()))

        data.add(null)

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_PRICE,
                getString(R.string.filter_title_price),
                getRangeTextValue(filterViewModel.searchRequest.minPrice, filterViewModel.searchRequest.maxPrice,
                        R.string.filter_less_than, R.string.filter_more_than,
                        UserPreferences.currency.value!!.getSymbol(context!!), "")))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_ABV,
                getString(R.string.filter_title_abv),
                getRangeTextValue(filterViewModel.searchRequest.minABV, filterViewModel.searchRequest.maxABV,
                        R.string.filter_less_than, R.string.filter_more_than,
                        "", "%")))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_AGE,
                getString(R.string.filter_title_age),
                getRangeTextValue(filterViewModel.searchRequest.minAge, filterViewModel.searchRequest.maxAge,
                        R.string.filter_less_than, R.string.filter_more_than,
                        "", "YO")))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_VINTAGE,
                getString(R.string.filter_title_vintage),
                getRangeTextValue(filterViewModel.searchRequest.minDistillationYear, filterViewModel.searchRequest.maxDistillationYear,
                        R.string.filter_before, R.string.filter_after,
                        "", "")))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_BOTTLING_YEAR,
                getString(R.string.filter_title_bottling_year),
                getRangeTextValue(filterViewModel.searchRequest.minBottlingYear, filterViewModel.searchRequest.maxBottlingYear,
                        R.string.filter_before, R.string.filter_after,
                        "", "")))

        data.add(null)

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_MATURED_IN,
                getString(R.string.filter_title_matured_in),
                filterViewModel.searchRequest.exLiquid.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_WOOD_TYPE,
                getString(R.string.filter_title_wood_type),
                filterViewModel.searchRequest.wood.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_CASK_SIZE,
                getString(R.string.filter_title_cask_size),
                filterViewModel.searchRequest.caskSize.toStringEllipsize(resources)))

        data.add(Quadrouple(FilterAdapter.TYPE.DATA_VALUE,
                FILTER_REFILL,
                getString(R.string.filter_title_cask_refill),
                filterViewModel.searchRequest.refill.toStringEllipsize(resources)))

        data.add(null)

        return data
    }

    val onFilterClicked: (Int) -> Unit = { filter ->

        val ft = activity!!.supportFragmentManager.beginTransaction().addToBackStack("").setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
        when (filter) {
            FILTER_CATEGORY -> {
                val fragment = SIngleSelectionFilterFragment.getFragment(filterViewModel.searchRequest.category[0], getString(R.string.filter_title_category))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.category.clear()
                    filterViewModel.searchRequest.category.add(it)
                }

                fragment.loader = {
                    it(arrayListOf("Whisky", "Rum", "Gin", "Cognac", "Vodka", "Other Spirits"))
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_SUB_CATEGORY -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.subcategory), getString(R.string.filter_title_sub_category))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.subcategory = it.toMutableList()
                }

                fragment.loader = { result ->
                    filterViewModel.getSubCategories {
                        result(ArrayList(it.subcategories))
                    }
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_COUNTRY -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.country), getString(R.string.filter_title_country))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.country = it.toMutableList()
                }

                fragment.loader = {
                    openCountriesFilter(it)
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_REGION -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.region), getString(R.string.filter_title_region))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.region = it.toMutableList()
                }

                fragment.loader = {
                    openRegions(it)
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_BRAND -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.brand), getString(R.string.filter_title_brand))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.brand = it.toMutableList()
                }

                fragment.loader = {
                    openBrandsFilter(it)
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_BOTTLED_BY -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.bottler), getString(R.string.filter_title_bottler))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.bottler = it.toMutableList()
                }

                fragment.loader = {
                    openBottlerFilter(it)
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_PRICE -> {
                val displayedValues = mutableListOf<Int>()
                for (i in 1..100) {
                    displayedValues.add(i * 10)
                }
                for (i in 11..1000) {
                    displayedValues.add(i * 100)
                }
                RangeDialog(activity!!, R.string.price, filterViewModel.searchRequest.minPrice, filterViewModel.searchRequest.maxPrice, displayedValues, UserPreferences.currency.value!!.getSymbol(activity!!), "") { min, max ->
                    filterViewModel.searchRequest.minPrice = min
                    filterViewModel.searchRequest.maxPrice = max
                    initAdapter()
                }
            }
            FILTER_ABV -> {
                val displayedValues = mutableListOf<Int>()
                for (i in 10..100) {
                    displayedValues.add(i)
                }

                RangeDialog(activity!!, R.string.abv, filterViewModel.searchRequest.minABV, filterViewModel.searchRequest.maxABV, displayedValues, "", "%") { min, max ->
                    filterViewModel.searchRequest.minABV = min
                    filterViewModel.searchRequest.maxABV = max
                    initAdapter()
                }
            }
            FILTER_AGE -> {
                val displayedValues = mutableListOf<Int>()
                for (i in 0..Calendar.getInstance().get(Calendar.YEAR) - 1920) {
                    displayedValues.add(i)
                }
                RangeDialog(activity!!, R.string.age, filterViewModel.searchRequest.minAge, filterViewModel.searchRequest.maxAge, displayedValues, "", "yo") { min, max ->
                    filterViewModel.searchRequest.minAge = min
                    filterViewModel.searchRequest.maxAge = max
                    initAdapter()
                }
            }
            FILTER_VINTAGE -> {
                val displayedValues = mutableListOf<Int>()
                for (i in 1920..Calendar.getInstance().get(Calendar.YEAR)) {
                    displayedValues.add(i)
                }
                RangeDialog(activity!!, R.string.vintage, filterViewModel.searchRequest.minDistillationYear, filterViewModel.searchRequest.maxDistillationYear, displayedValues, "", "") { min, max ->
                    filterViewModel.searchRequest.minDistillationYear = min
                    filterViewModel.searchRequest.maxDistillationYear = max
                    initAdapter()
                }
            }
            FILTER_BOTTLING_YEAR -> {
                val displayedValues = mutableListOf<Int>()
                for (i in 1920..Calendar.getInstance().get(Calendar.YEAR)) {
                    displayedValues.add(i)
                }
                RangeDialog(activity!!, R.string.bottling_year, filterViewModel.searchRequest.minBottlingYear, filterViewModel.searchRequest.maxBottlingYear, displayedValues, "", "") { min, max ->
                    filterViewModel.searchRequest.minBottlingYear = min
                    filterViewModel.searchRequest.maxBottlingYear = max
                    initAdapter()
                }
            }
            FILTER_MATURED_IN -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.exLiquid), getString(R.string.filter_title_matured_in))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.exLiquid = it
                }

                fragment.loader = { result ->
                    filterViewModel.getCasks {
                        result(ArrayList(it.exLiquid))
                    }
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_CASK_SIZE -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.caskSize), getString(R.string.filter_title_cask_size))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.caskSize = it
                }

                fragment.loader = { result ->
                    filterViewModel.getCasks {
                        result(ArrayList(it.caskSize))
                    }
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_WOOD_TYPE -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.wood), getString(R.string.filter_title_wood_type))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.wood = it
                }

                fragment.loader = { result ->
                    filterViewModel.getCasks {
                        result(ArrayList(it.wood))
                    }
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_REFILL -> {
                val fragment = MultiSelectionFilterFragment.getFragment(ArrayList(filterViewModel.searchRequest.refill), getString(R.string.filter_title_cask_refill))
                fragment.optionsSelected = {
                    filterViewModel.searchRequest.refill = it
                }

                fragment.loader = { result ->
                    filterViewModel.getCasks {
                        result(ArrayList(it.refill))
                    }
                }
                ft.add(R.id.content, fragment).commit()
            }
            FILTER_ONLY_VERIFIED -> {
                filterViewModel.searchRequest.onlyVerified(!filterViewModel.searchRequest.onlyVerified())
            }
        }
    }

    private fun getRangeTextValue(min: Int, max: Int, minRes: Int, maxRes: Int, prefix: String, suffix: String)
            : String {
        if (min == max) {
            if (min == SearchRequest.DEFAULT_VAL) {
                return ""
            } else {
                return String.format(context!!.getString(R.string.filter_exactly), prefix, max, suffix)
            }
        } else if (min == SearchRequest.DEFAULT_VAL) {
            return String.format(context!!.getString(minRes), prefix, max, suffix)
        } else if (max == SearchRequest.DEFAULT_VAL) {
            return String.format(context!!.getString(maxRes), prefix, min, suffix)
        } else {
            return "$prefix$min$suffix - $prefix$max$suffix"
        }
    }

    private fun openCountriesFilter(loaded: (ArrayList<String>) -> Unit) = GlobalScope.launch(Dispatchers.Main) {
        val res = try {
            val sr = ApiRequest().apply {
                category = filterViewModel.searchRequest.category
                subcategory = filterViewModel.searchRequest.subcategory
                setSort("country", ApiRequest.SORT_ASC)
            }

            ApiClient.get().getCountries(sr).await()
        } catch (e: Exception) {
            null
        }

        res?.let {
            val options = ArrayList(it.results.sortedBy { it.count }.reversed().map { it.name }.toSet())
            loaded(options)
        }
    }

    private fun openRegions(loaded: (ArrayList<String>)
    -> Unit
    ) = GlobalScope.launch(Dispatchers.Main) {
        val res = try {
            val sr = ApiRequest().apply {
                category = filterViewModel.searchRequest.category
                subcategory = filterViewModel.searchRequest.subcategory
                country = filterViewModel.searchRequest.country
                setSort("region", ApiRequest.SORT_ASC)
            }

            ApiClient.get().getRegions(sr).await()
        } catch (e: Exception) {
            null
        }

        res?.let {
            val options = ArrayList(it.results.map { it.name }.toSet())
            loaded(options)
        }
    }

    private fun openBrandsFilter(loaded: (ArrayList<String>)
    -> Unit
    ) = GlobalScope.launch(Dispatchers.Main) {
        val res = try {
            val sr = ApiRequest().apply {
                category = filterViewModel.searchRequest.category
                subcategory = filterViewModel.searchRequest.subcategory
                setSort("brand", ApiRequest.SORT_ASC)
            }
            ApiClient.get().getDistilleries(sr).await()
        } catch (e: Exception) {
            null
        }

        res?.let {
            val options = ArrayList(it.results.map { it.name }.filter { it.isNotEmpty() }.toSet())
            loaded(options)
        }
    }

    private fun openBottlerFilter(loaded: (ArrayList<String>)
    -> Unit
    ) = GlobalScope.launch(Dispatchers.Main) {
        val res = try {
            val sr = ApiRequest().apply {
                category = filterViewModel.searchRequest.category
                subcategory = filterViewModel.searchRequest.subcategory
                setSort("bottler", ApiRequest.SORT_ASC)
            }
            ApiClient.get().getBottlers(sr).await()
        } catch (e: Exception) {
            null
        }

        res?.let {
            val options = ArrayList(it.results.map { it.name }.filter { it.isNotEmpty() }.toSet())
            loaded(options)
        }
    }
}