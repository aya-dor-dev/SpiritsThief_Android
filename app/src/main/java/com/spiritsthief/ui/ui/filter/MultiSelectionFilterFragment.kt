package com.spiritsthief.ui.ui.filter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.spiritsthief.R
import com.spiritsthief.common.hide
import com.spiritsthief.ui.ui.common.MultiSelectionAdapter
import kotlinx.android.synthetic.main.fragment_filter_multi_selection.*

class MultiSelectionFilterFragment: Fragment() {
    companion object {
        val SELECTED = "selected"
        val TITLE = "title"

        fun getFragment(selected: ArrayList<String>,
                        title: String): MultiSelectionFilterFragment {
            val bundle = Bundle()
            bundle.putSerializable(SELECTED, selected)
            bundle.putString(TITLE, title)

            val fragment = MultiSelectionFilterFragment()
            fragment.arguments = bundle

            return fragment
        }
    }

    var loader: ((ArrayList<String>) -> Unit) -> Unit = {}
    var optionsSelected: (options: List<String>) -> Unit = {}
    var selected: ArrayList<String> = arrayListOf()
    var adapter: MultiSelectionAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_filter_multi_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview.layoutManager = LinearLayoutManager(activity!!)
        recyclerview.background = ColorDrawable(Color.parseColor("#ffffff"))
        selected = arguments!!.getSerializable(SELECTED) as ArrayList<String>
        (activity as AppCompatActivity).supportActionBar!!.title = arguments!!.getString(TITLE)

        loader {
            if (recyclerview != null) {
                recyclerview.layoutAnimation = AnimationUtils.loadLayoutAnimation(activity!!, R.anim.layout_animation_fallup)
                adapter = MultiSelectionAdapter(it, selected) {
                    optionsSelected(it)
                }
                adapter = adapter
                recyclerview.adapter = adapter
                progress.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.filter_multi_selection_menu, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return true
            }

        })
    }
}