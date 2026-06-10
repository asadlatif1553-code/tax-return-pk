package pk.taxreturn.app.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import pk.taxreturn.app.model.ReturnData
import pk.taxreturn.app.model.UserData
import pk.taxreturn.app.tax.TaxEngine

class TaxViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("tax_return_2026", Context.MODE_PRIVATE)

    var data by mutableStateOf(ReturnData.fromJson(prefs.getString("data", null)))
        private set

    var computation by mutableStateOf(TaxEngine.compute(data))
        private set

    var loggedInUser: UserData? by mutableStateOf(UserData.load(app))
        private set

    val isLoggedIn: Boolean get() = loggedInUser != null

    fun update(block: (ReturnData) -> ReturnData) {
        data = block(data)
        computation = TaxEngine.compute(data)
        prefs.edit().putString("data", data.toJson()).apply()
    }

    fun login(user: UserData) {
        loggedInUser = user
        // Pre-fill name/CNIC from user profile if empty
        if (data.name.isEmpty()) update { it.copy(name = user.fullName, cnic = user.cnic) }
    }

    fun logout(context: Context) {
        UserData.clear(context)
        loggedInUser = null
    }

    fun reset() {
        val user = loggedInUser
        update { ReturnData(name = user?.fullName ?: "", cnic = user?.cnic ?: "") }
    }
}
