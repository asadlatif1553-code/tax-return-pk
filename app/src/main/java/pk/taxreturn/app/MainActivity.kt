package pk.taxreturn.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import pk.taxreturn.app.ui.*

// ââ Brand colour scheme âââââââââââââââââââââââââââââââââââââââââââââââââââââââ
private val GreenLight = lightColorScheme(
    primary         = Color(0xFF1B5E20),
    onPrimary       = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    secondary       = Color(0xFF2E7D32),
    tertiary        = Color(0xFF00695C),
    background      = Color(0xFFF9FBF9),
    surface         = Color(0xFFFFFFFF),
    surfaceVariant  = Color(0xFFEDF7EE)
)
private val GreenDark = darkColorScheme(
    primary         = Color(0xFF81C784),
    onPrimary       = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    secondary       = Color(0xFF80CBC4),
    tertiary        = Color(0xFF80CBC4)
)

data class NavTab(val title: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) GreenDark else GreenLight) {
                val vm: TaxViewModel = viewModel()
                val ctx = LocalContext.current

                if (!vm.isLoggedIn) {
                    // Show login / register screen
                    LoginScreen(onLoggedIn = { user -> vm.login(user) })
                } else {
                    // Main app
                    var tab by rememberSaveable { mutableIntStateOf(0) }
                    val tabs = listOf(
                        NavTab("Profile",     Icons.Filled.Person),
                        NavTab("Income",      Icons.Filled.Receipt),
                        NavTab("Deductions",  Icons.Filled.Savings),
                        NavTab("Wealth",      Icons.Filled.AccountBalance),
                        NavTab("Summary",     Icons.Filled.Calculate)
                    )

                    // Live tax summary badge
                    val c = vm.computation
                    val badgeLabel = if (c.totalTaxChargeable == 0L) "" else
                        if (c.balance < 0) "â© ${fmtK(-c.balance)}" else "â ${fmtK(c.balance)}"

                    Surface {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = {
                                        androidx.compose.foundation.layout.Column {
                                            Text("Tax Return PK â TY 2026")
                                            if (vm.data.name.isNotEmpty())
                                                Text(
                                                    vm.data.name,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                                )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor    = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    actions = {
                                        if (badgeLabel.isNotEmpty()) {
                                            Badge(
                                                containerColor = if (c.balance < 0) Color(0xFF2E7D32) else Color(0xFFE65100)
                                            ) {
                                                Text(badgeLabel, color = Color.White)
                                            }
                                        }
                                    }
                                )
                            },
                            bottomBar = {
                                NavigationBar {
                                    tabs.forEachIndexed { i, t ->
                                        NavigationBarItem(
                                            selected = tab == i,
                                            onClick  = { tab = i },
                                            icon     = {
                                                // Show dot on Summary if there are warnings
                                                if (i == 4 && c.warnings.isNotEmpty()) {
                                                    BadgedBox(badge = {
                                                        Badge { Text("${c.warnings.size}") }
                                                    }) {
                                                        Icon(t.icon, contentDescription = t.title)
                                                    }
                                                } else {
                                                    Icon(t.icon, contentDescription = t.title)
                                                }
                                            },
                                            label    = { Text(t.title) }
                                        )
                                    }
                                }
                            }
                        ) { pad ->
                            Box(Modifier.padding(pad)) {
                                when (tab) {
                                    0 -> PersonalScreen(vm, onLogout = { vm.logout(ctx) })
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
}

/** Format large numbers as e.g. "1.2M" or "350K" for the top-bar badge. */
private fun fmtK(v: Long): String = when {
    v >= 1_000_000 -> "%.1fM".format(v / 1_000_000.0)
    v >= 1_000     -> "${v / 1_000}K"
    else           -> v.toString()
}
