package pk.taxreturn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import pk.taxreturn.app.ui.DeductionsScreen
import pk.taxreturn.app.ui.IncomeScreen
import pk.taxreturn.app.ui.PersonalScreen
import pk.taxreturn.app.ui.SummaryScreen
import pk.taxreturn.app.ui.TaxViewModel
import pk.taxreturn.app.ui.WealthScreen

private val GreenLight = lightColorScheme(
    primary = Color(0xFF0B5E2A),
    secondary = Color(0xFF2E7D32),
    tertiary = Color(0xFF558B2F)
)
private val GreenDark = darkColorScheme(
    primary = Color(0xFF7BC67E),
    secondary = Color(0xFF81C784),
    tertiary = Color(0xFFAED581)
)

data class Tab(val title: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) GreenDark else GreenLight) {
                val vm: TaxViewModel = viewModel()
                var tab by rememberSaveable { mutableIntStateOf(0) }
                val tabs = listOf(
                    Tab("Profile", Icons.Filled.Person),
                    Tab("Income", Icons.Filled.Receipt),
                    Tab("Deductions", Icons.Filled.Savings),
                    Tab("Wealth", Icons.Filled.AccountBalance),
                    Tab("Summary", Icons.Filled.Calculate)
                )
                Surface {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Tax Return PK — TY 2026") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar {
                                tabs.forEachIndexed { i, t ->
                                    NavigationBarItem(
                                        selected = tab == i,
                                        onClick = { tab = i },
                                        icon = { Icon(t.icon, contentDescription = t.title) },
                                        label = { Text(t.title) }
                                    )
                                }
                            }
                        }
                    ) { pad ->
                        androidx.compose.foundation.layout.Box(Modifier.padding(pad)) {
                            when (tab) {
                                0 -> PersonalScreen(vm)
                                1 -> IncomeScreen(vm)
                                2 -> DeductionsScreen(vm)
                                3 -> WealthScreen(vm)
                                else -> SummaryScreen(vm)
                            }
                        }
                    }
                }
            }
        }
    }
}
