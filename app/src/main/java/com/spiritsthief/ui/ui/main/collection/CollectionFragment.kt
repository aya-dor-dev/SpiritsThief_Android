package com.spiritsthief.ui.ui.main.wishlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spiritsthief.R
import com.spiritsthief.api.model.Bottle
import com.spiritsthief.repository.UserCollections
import com.spiritsthief.ui.adapter.BottlesCollectionListAdapter
import com.spiritsthief.ui.adapter.PagedListHandler
import com.spiritsthief.ui.ui.BottlesListFragment
import kotlinx.android.synthetic.main.bottles_list_container.*
import kotlinx.android.synthetic.main.recycler_view_fragment.*

class CollectionFragment: BottlesListFragment<BottlesCollectionListAdapter>() {

    lateinit var collectionViewModel: CollectionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_simple_bottles_list, container, false)

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        collectionViewModel = ViewModelProviders.of(this).get(CollectionViewModel::class.java)

        UserCollections.collection.observe(this, Observer {
            collectionViewModel.performSearch(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachRecyclerViewToScrollListener(bottles_list)
        toolbar.title = getString(R.string.collection)
        registerViewModel(collectionViewModel)
    }

    override fun getLayoutManager() = GridLayoutManager(activity, 2)


    override fun getAdapter(list: MutableList<Bottle>, onBottleClick: (Bottle, CardView) -> Unit, pagedListHandler: PagedListHandler?) =
            BottlesCollectionListAdapter(list, onBottleClick, viewModel)

    override fun getNoResultsErrorMessage() = R.string.collection_empty
    override fun getNoResultsErrorIcon() = 0
}