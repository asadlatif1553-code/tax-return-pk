package pk.taxreturn.app.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import pk.taxreturn.app.model.ReturnData
import pk.taxreturn.app.tax.TaxEngine

class TaxViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("tax_return_2026", Context.MODE_PRIVATE)

    var data by mutableStateOf(ReturnData.fromJson(prefs.getString("data", null)))
        private set

    var computation by mutableStateOf(TaxEngine.compute(data))
        private set

    fun update(block: (ReturnData) -> ReturnData) {
        data = block(data)
        computation = TaxEngine.compute(data)
        prefs.edit().putString("data", data.toJson()).apply()
    }

    fun reset() {
        update { ReturnData() }
    }
}
