package com.spiritsthief.common

import com.spiritsthief.api.model.AutoCompleteOption

/**
 * Created by dorayalon on 26/01/2018.
 */
interface AutocompleteHandler {
    fun onOptionSelected(q: AutoCompleteOption)
}