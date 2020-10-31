package com.spiritsthief.ui.ui.main.search

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.spiritsthief.R
import com.spiritsthief.api.ApiClient
import com.spiritsthief.api.model.AutoCompleteOption
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.api.model.SearchRequest
import com.spiritsthief.common.AnalyticsHelper
import com.spiritsthief.common.SimpleTextWatcher
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.ui.adapter.BottlesListAdapter
import com.spiritsthief.ui.adapter.PagedListHandler
import com.spiritsthief.ui.scanner.barcode.BarcodeCaptureActivity
import com.spiritsthief.ui.ui.BottlesListFragment
import com.spiritsthief.ui.ui.filter.FilterActivity
import com.spiritsthief.ui.util.StartSnapHelper
import kotlinx.android.synthetic.main.bottles_list_container.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search_frame.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchFragment : BottlesListFragment<BottlesListAdapter>() {

    private val REQ_FILTER = 1111
    private val REQ_SCAN_BARCODE = 1112

    var autoCompleteApiRequest: Deferred<List<AutoCompleteOption>>? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>
    lateinit var categoryAdapter: CategoryAdapter
    lateinit var searchViewModel: SearchViewModel
    private var searchOpened = false

    private val sortFrag = SortFragment()

    private val searchTextListener = object : SimpleTextWatcher() {
        override fun onTextChanged(q: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (q != null && q.length > 1) {
                getAutoCompleteOptions(q.toString())
            } else {
                search.setAdapter(null)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_search, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        searchViewModel = ViewModelProviders.of(activity!!).get(SearchViewModel::class.java)
        registerViewModel(searchViewModel)

        UserPreferences.country.observe(this, Observer {
            searchViewModel.apply {
                searchRequest.deliveryCountry = when (it) {
                    "" -> listOf()
                    else -> listOf(UserPreferences.country.value!!)
                }
                clear()
                performSearch()
            }
        })

        UserPreferences.currency.observe(this, Observer {
            searchViewModel.apply {
                searchRequest.currency = it.name
                clear()
                performSearch()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        search.threshold = 1
        attachRecyclerViewToScrollListener(bottles_list)
        setSearchFunctionality()
        setSort()
        initCategoriesView()
        searchViewModel.performSearch()

        scan_barcode.setOnClickListener {
            startActivityForResult(Intent(activity, BarcodeCaptureActivity::class.java), REQ_SCAN_BARCODE)
        }

        filter.setOnClickListener {
            val intent = Intent(activity, FilterActivity::class.java)
            intent.putExtras(FilterActivity.getBundle(searchViewModel.searchRequest))
            startActivityForResult(intent, REQ_FILTER)
            activity!!.overridePendingTransition(R.anim.slide_in_from_left, R.anim.stay)
        }
    }

    override fun getAdapter(list: MutableList<Bottle>, onBottleClick: (Bottle, CardView) -> Unit, pagedListHandler: PagedListHandler?) =
            BottlesListAdapter(list, onBottleClick, viewModel)

    private fun initCategoriesView() {
        categoryAdapter = CategoryAdapter(searchViewModel.searchRequest.category[0]) { selectedCategory: String, position: Int ->
            searchViewModel.searchRequest.apply {
                category.clear()
                category.add(selectedCategory)
                sortBy = "RANDOM"
            }
            clear()
            activity!!.runOnUiThread { categories.smoothScrollToPosition(if (position == 0) position else position - 1) }
            searchViewModel.performSearch()
        }

        StartSnapHelper().attachToRecyclerView(categories)
        categories.adapter = categoryAdapter
        categories.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun clear() {
        super.clear()
        closeBottomSheet()
        autoCompleteApiRequest?.cancel()
        hideSoftKeybard()
        search.dismissDropDown()
        search.removeTextChangedListener(searchTextListener)
    }

    override fun loadingBottles() {
        super.loadingBottles()
        count.text = "..."

        val filters = searchViewModel.searchRequest.getCountOfFilters()
        filter_counter.animate().alpha(if (filters == 0) 0.0f else 1.0f).setDuration(300).start()
        filter_counter.text = filters.toString()

        if (searchViewModel.searchRequest.category.isNotEmpty()) {
            var catPos = categoryAdapter.changeSelected(searchViewModel.searchRequest.category[0])
            if (catPos < categoryAdapter.itemCount - 1 && catPos > 1) catPos += 1
            categories.layoutManager?.scrollToPosition(catPos)
        }
    }

    override fun bottlesLoaded() {
        super.bottlesLoaded()
        count.text = String.format(getString(R.string.results_count), searchViewModel.count)
        if (searchViewModel.count!! > 0) {
            if (searchViewModel.searchRequest.isBarcodeSearch()) {
                var catPos = categoryAdapter.changeSelected(searchViewModel.bottles!![0].category)
                if (catPos < categoryAdapter.itemCount - 1 && catPos > 1) catPos += 1
                categories.layoutManager?.scrollToPosition(catPos)
            }
        }
    }

    override fun getNoResultsErrorMessage() = R.string.search_zero_results
    override fun getNoResultsErrorIcon() = R.drawable.albert_sad

    private fun setSearchFunctionality() {
        search.setOnItemClickListener { adapterView, view, i, l ->
            clear()
            searchViewModel.searchRequest.name = search.text.toString()
            searchViewModel.performSearch()
            autoCompleteApiRequest?.cancel()
        }

        search.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                clear()
                searchViewModel.searchRequest.barcode = ""
                searchViewModel.searchRequest.name = search.text.toString()
                searchViewModel.performSearch()
                handled = true
            }

            handled
        }

        search.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                search.addTextChangedListener(searchTextListener)
            }
            false
        }

        open_search.setOnClickListener {
            openSearch()
        }

        search_hint.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            openSearch()
            true
        }

        search_hint.setOnClickListener {
            openSearch()
        }

        close_search.setOnClickListener {
            closeSearch()
        }
    }

    private fun openSearch() {
        app_bar_layout.setExpanded(false, true)
        scrollListener?.onScrollDown()
        searchOpened = true
        if (search_container.visibility != View.VISIBLE) {
            val cx = search_container.width / 2
            val cy = search_container.height / 2

            val startAnimX = open_search.x + (open_search.width / 2)
            val startAnimY = open_search.y + (open_search.height / 2)

            // get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat() * 2

            // create the animator for this view (the start radius is zero)
            val anim = ViewAnimationUtils.createCircularReveal(search_container, startAnimX.toInt(), startAnimY.toInt(), 0f, finalRadius)
            anim.duration = 300L
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}

                override fun onAnimationEnd(animation: Animator?) {
                    search.post {
                        search.requestFocusFromTouch()
                        search.requestFocusFromTouch()
                        val lManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        lManager.showSoftInput(search, 0)
//                        search.addTextChangedListener(searchTextListener)
                    }
                    search.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 0f, 0f, 0))
                }

                override fun onAnimationCancel(animation: Animator?) {}

                override fun onAnimationStart(animation: Animator?) {}
            })

            // make the view visible and start the animation
            search_container.visibility = View.VISIBLE
            anim.start()
        }
    }

    private fun closeSearch() {
        scrollListener?.onScrollUp()
        app_bar_layout.setExpanded(true, true)
        searchOpened = false
        if (search.text.toString().isNotEmpty()) {
            search.setText("")
            return
        }

        if (search_container.visibility != View.VISIBLE) return

        val startAnimX = open_search.x + (open_search.width / 2)
        val startAnimY = open_search.y + (open_search.height / 2)

        val cx = search_container.width / 2
        val cy = search_container.height / 2
        // get the final radius for the clipping circle
        val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat() * 2

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(search_container, startAnimX.toInt(), startAnimY.toInt(), finalRadius, 0f)
        anim.duration = 300L
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                search_container.visibility = View.INVISIBLE
                search.setText("")
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        anim.start()
        val view = activity!!.currentFocus
        if (view != null) {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun getAutoCompleteOptions(q: String) = GlobalScope.launch(Dispatchers.Main) {
        autoCompleteApiRequest?.cancel()
        autoCompleteApiRequest = ApiClient.get().getAutocompleteOptions(q)
        val res = try {
            autoCompleteApiRequest!!.await()
        } catch (e: Exception) {
            null
        }

        res?.let {
            val options = it.map { it.name }.toMutableList()
            options.add(0, q)
            val adapter = ArrayAdapter<String>(activity,
                    android.R.layout.simple_dropdown_item_1line, options)
            search.setAdapter<ArrayAdapter<String>>(adapter)
            search.showDropDown()
        }
    }

    fun setSort() {
        childFragmentManager.beginTransaction().replace(R.id.bottom_sheet_content, sortFrag).commit()
        close_bottom_sheet.setOnClickListener { closeBottomSheet() }
        sort.setOnClickListener {
            when (sheetBehavior.state) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    openBottomSheet()
                }
                else -> closeBottomSheet()
            }
        }
    }

    private fun closeBottomSheet(): Boolean {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            scrollListener?.onScrollUp()
            return true
        }

        return false
    }

    private fun openBottomSheet() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            scrollListener?.onScrollDown()
        }
    }

    override fun onBackPressed(): Boolean {
        if (closeBottomSheet()) {
            return true
        } else if (searchOpened) {
            closeSearch()
            return true
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_SCAN_BARCODE -> {
                    val barcode: Barcode = data!!.extras.getParcelable(BarcodeCaptureActivity.BarcodeObject) as Barcode
                    searchViewModel.performBarcodeSearch(barcode.rawValue)
                }
                REQ_FILTER -> {
                    val sr = data!!.extras.getSerializable(FilterActivity.EXTRA_SEARCH_REQUEST) as SearchRequest
                    if (!searchViewModel.searchRequest.equals(sr)) {
                        AnalyticsHelper.searchApplyFilter(searchViewModel.searchRequest)
                        searchViewModel.searchRequest = sr
                        searchViewModel.performSearch()
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}