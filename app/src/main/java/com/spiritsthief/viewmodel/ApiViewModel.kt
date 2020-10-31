package com.spiritsthief.viewmodel

import androidx.lifecycle.ViewModel
import com.spiritsthief.api.ApiClient

/**
 * Created by Dor Ayalon on 1/11/18.
 */
abstract class ApiViewModel: ViewModel() {
    protected val api by lazy {
        ApiClient.get()
    }
}