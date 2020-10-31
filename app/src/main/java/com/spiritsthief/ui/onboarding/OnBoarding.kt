package com.spiritsthief.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.spiritsthief.R
import com.spiritsthief.common.*
import com.spiritsthief.ui.SplashActivity
import com.spiritsthief.ui.ui.common.SingleSelectionAdapter
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoarding : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        val onNext: () -> Unit = {
            if (pager.currentItem != 2) {
                pager.setCurrentItem(pager.currentItem+1, true)
            } else {
                finish()
                startActivity(Intent(this, SplashActivity::class.java))
            }
        }
        pager.adapter = Adapter(supportFragmentManager, onNext)
        pager.setOnTouchListener { v, event -> true }
    }

    class Adapter(fm: FragmentManager, val onNext: () -> Unit): FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val frag: OnBoardingFragment
            when (position) {
                0 -> {
                    frag = ChooseCurrencyFragment()
                    frag.onNext = onNext
                }
                1 -> {
                    frag = ChooseDeliveryCountry()
                    frag.onNext = onNext
                }
                else -> {
                    frag = Availability()
                    frag.onNext = onNext
                }
            }

            return frag
        }

        override fun getCount() = 3

        abstract class OnBoardingFragment: Fragment() {
            var displayFinish = false
            var onNext: () -> Unit = {}
                set(value) {
                    field = value
                    displayFinish = true
                }

            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
                    inflater.inflate(R.layout.fragment_onboarding, container, false)
        }

        class ChooseCurrencyFragment: OnBoardingFragment() {

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                view.findViewById<TextView>(R.id.title).setText(R.string.oncoarding_choose_currency)
                view.findViewById<TextView>(R.id.subtitle).setText(R.string.onboarding_currency_subtitle)
                view.findViewById<LinearLayout>(R.id.content).addView(LayoutInflater.from(activity!!).inflate(R.layout.fragment_currency,
                        view.findViewById<LinearLayout>(R.id.content), false))
                val next = view.findViewById<Button>(R.id.next)
                next.text = "Next"
                next.setOnClickListener { onNext() }

                when (UserPreferences.currency.value) {
                    CURRENCY.GBP -> view.findViewById<RadioButton>(R.id.gbp)?.isChecked = true
                    CURRENCY.USD -> view.findViewById<RadioButton>(R.id.usd)?.isChecked = true
                    CURRENCY.EUR -> view.findViewById<RadioButton>(R.id.eur)?.isChecked = true
                }
            }
        }

        class ChooseDeliveryCountry: OnBoardingFragment() {
            lateinit var country: Country

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                val next = view.findViewById<Button>(R.id.next)

                val list = RecyclerView(activity!!)
                list.layoutManager = LinearLayoutManager(activity!!)

                val options = getCountriesOptions()
                val countries = mutableListOf<Country?>()
                countries.addAll(options.first)
                countries.add(null)
                countries.addAll(options.second)
                val any = view.context.resources.getString(R.string.any)
                var selectedValue = any

                val countryNames = mutableListOf<String?>()
                countryNames.addAll(options.first.map { it.name }.toMutableList())
                countryNames.add(null)
                countryNames.addAll(options.second.map { it.name }.toMutableList())
                countryNames.add(0, any)

                list.adapter = SingleSelectionAdapter(countryNames, selectedValue) {
                    selectedValue = if (it == any) "" else it
                }

                view.findViewById<TextView>(R.id.title).text = getString(R.string.onboarding_delivery_country)
                view.findViewById<TextView>(R.id.subtitle).text = getString(R.string.onboarding_delivery_countrt_subtitle)
                view.findViewById<LinearLayout>(R.id.content).addView(list)
                next.text = "Next"
                next.setOnClickListener {
                    val selectedCountry = when (selectedValue) {
                        any -> ""
                        else ->  countries.filter { it?.name == selectedValue}[0]!!.code
                    }
                    UserPreferences.setDeliveryCountry(selectedCountry)
                    UserPreferences.isFirstTime = false
                    onNext() }
            }
        }

        class Availability: OnBoardingFragment() {
            lateinit var country: Country

            override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
                    inflater.inflate(R.layout.fragment_onboarding_availability, container, false)

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                val next = view.findViewById<Button>(R.id.next)
                next.text = "Finish"
                next.setOnClickListener { onNext() }
            }
        }
    }
}

