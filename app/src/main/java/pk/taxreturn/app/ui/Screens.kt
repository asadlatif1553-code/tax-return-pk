@file:OptIn(ExperimentalMaterial3Api::class)

package pk.taxreturn.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// PERSONAL / PROFILE SCREEN
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(vm: TaxViewModel, onLogout: () -> Unit) {
    val d = vm.data
    val user = vm.loggedInUser

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        if (user != null) {
            Card(
                Modifier.fillMaxWidth().padding(bottom = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, null, Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(user.fullName, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("CNIC: ${user.cnic}", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(user.email, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    TextButton(onClick = onLogout) { Text("Logout") }
                }
            }
        }

        SectionCard("Taxpayer Profile â Tax Year 2026") {
            Text(
                "Period: 1 July 2025 â 30 June 2026  â¢  Return u/s 114, Wealth Statement u/s 116",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = d.name,
                onValueChange = { v -> vm.update { it.copy(name = v) } },
                label = { Text("Full name (as per CNIC)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = d.cnic,
                onValueChange = { v -> vm.update { it.copy(cnic = v.filter(Char::isDigit).take(13)) } },
                label = { Text("CNIC (13 digits, no dashes)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = d.ntn,
                onValueChange = { v -> vm.update { it.copy(ntn = v) } },
                label = { Text("NTN (if registered)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Full-time teacher / researcher", style = MaterialTheme.typography.bodyMedium)
                    Text("25% reduction in tax on salary (2nd Schedule, Part III)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = d.isTeacherOrResearcher,
                    onCheckedChange = { v -> vm.update { it.copy(isTeacherOrResearcher = v) } }
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Non-resident individual", style = MaterialTheme.typography.bodyMedium)
                    Text("Different rules may apply â consult a tax adviser",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = d.isNonResident,
                    onCheckedChange = { v -> vm.update { it.copy(isNonResident = v) } }
                )
            }
        }

        SectionCard("How this app works") {
            Text(
                "1. Go to the Income tab and select all sources that apply.\n" +
                "2. Fill in each income section.\n" +
                "3. Add deductions (Zakat, pension fund, donations) in the Deductions tab.\n" +
                "4. Complete your Wealth Statement in the Wealth tab.\n" +
                "5. The Summary tab gives IRIS-ready figures â enter them at iris.fbr.gov.pk to file.\n\n" +
                "This app is a computation and preparation aid. It does not transmit data to FBR.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { vm.reset() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Reset all data for this return") }

        Spacer(Modifier.height(90.dp))
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// DEDUCTIONS SCREEN
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun DeductionsScreen(vm: TaxViewModel) {
    val d = vm.data
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        SectionCard("Deductible Allowances") {
            MoneyField("Zakat paid under Zakat & Ushr Ordinance â s.60", d.zakat) { v ->
                vm.update { it.copy(zakat = v) }
            }
            MoneyField("Worker Welfare Fund contribution â s.60A", d.workerWelfareFund) { v ->
                vm.update { it.copy(workerWelfareFund = v) }
            }
        }

        SectionCard("Tax Credits (at average rate of tax)") {
            MoneyField("Approved pension / VPS fund contribution â s.63", d.pensionFundContribution) { v ->
                vm.update { it.copy(pensionFundContribution = v) }
            }
            Text("Credit allowed on up to 20% of taxable income",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp))
            MoneyField("Donations to approved NPOs / charities â s.61", d.charitableDonations) { v ->
                vm.update { it.copy(charitableDonations = v) }
            }
            Text("Credit allowed on up to 30% of taxable income",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        SectionCard("Adjustable Withholding Taxes (claim against liability)") {
            Text("Enter amounts deducted from payments you made during the year.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp))
            MoneyField("Telephone / internet bills â u/s 236", d.taxPhone) { v ->
                vm.update { it.copy(taxPhone = v) }
            }
            MoneyField("Electricity bills (domestic) â u/s 235", d.taxElectricity) { v ->
                vm.update { it.copy(taxElectricity = v) }
            }
            MoneyField("Vehicle token tax / purchase â u/s 234 / 231B", d.taxVehicle) { v ->
                vm.update { it.copy(taxVehicle = v) }
            }
            MoneyField("On purchase of immovable property â u/s 236K", d.taxPropertyPurchase) { v ->
                vm.update { it.copy(taxPropertyPurchase = v) }
            }
            MoneyField("On sale of immovable property â u/s 236C", d.taxPropertySale) { v ->
                vm.update { it.copy(taxPropertySale = v) }
            }
            MoneyField("Other adjustable withholding tax", d.taxOtherAdjustable) { v ->
                vm.update { it.copy(taxOtherAdjustable = v) }
            }
        }

        Spacer(Modifier.height(90.dp))
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// WEALTH SCREEN
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun WealthScreen(vm: TaxViewModel) {
    val d = vm.data
    val c = vm.computation
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        SectionCard("Wealth Statement u/s 116 â as at 30 June 2026") {
            Text("Enter the value of all your assets and liabilities as at 30 June 2026.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            MoneyField("Net assets at 30 June 2025 (opening balance)", d.openingNetAssets) { v ->
                vm.update { it.copy(openingNetAssets = v) }
            }
            Divider()
            Text("ASSETS (at cost / market value)", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
            MoneyField("Immovable property (at cost)", d.assetProperty) { v ->
                vm.update { it.copy(assetProperty = v) }
            }
            MoneyField("Motor vehicles (at cost)", d.assetVehicles) { v ->
                vm.update { it.copy(assetVehicles = v) }
            }
            MoneyField("Bank balances (all accounts)", d.assetBankBalances) { v ->
                vm.update { it.copy(assetBankBalances = v) }
            }
            MoneyField("Cash in hand", d.assetCash) { v ->
                vm.update { it.copy(assetCash = v) }
            }
            MoneyField("Investments (shares, funds, NSS, bonds...)", d.assetInvestments) { v ->
                vm.update { it.copy(assetInvestments = v) }
            }
            MoneyField("Capital invested in business", d.assetBusiness) { v ->
                vm.update { it.copy(assetBusiness = v) }
            }
            MoneyField("Other assets (jewellery, household, loans given...)", d.assetOther) { v ->
                vm.update { it.copy(assetOther = v) }
            }
            Divider()
            SummaryRow("Total assets", c.closingNetAssets + d.liabilities)
            MoneyField("Less: Liabilities (loans, bank finance, payables)", d.liabilities) { v ->
                vm.update { it.copy(liabilities = v) }
            }
            Divider()
            SummaryRow("Closing net assets (30 Jun 2026)", c.closingNetAssets, bold = true)
        }

        SectionCard("Reconciliation Inputs") {
            MoneyField("Personal / household expenses during the year", d.personalExpenses) { v ->
                vm.update { it.copy(personalExpenses = v) }
            }
            MoneyField("Foreign remittance received â s.111(4)", d.foreignRemittance) { v ->
                vm.update { it.copy(foreignRemittance = v) }
            }
            MoneyField("Gifts / inheritance / other non-income inflows", d.giftsInheritanceOther) { v ->
                vm.update { it.copy(giftsInheritanceOther = v) }
            }
        }

        SectionCard("Reconciliation") {
            SummaryRow("Opening net assets (30 Jun 2025)", d.openingNetAssets)
            SummaryRow("Closing net assets (30 Jun 2026)", c.closingNetAssets)
            SummaryRow("Increase / (decrease) in net assets", c.increaseInNetAssets, negativeInBrackets = true)
            Divider()
            SummaryRow("Total inflows (income + exempt + gifts + remittance)", c.inflows)
            SummaryRow("Total outflows (expenses + tax paid)", c.outflows)
            SummaryRow("Net inflows minus outflows", c.inflows - c.outflows)
            Divider()
            val unreconciled = c.unreconciled
            SummaryRow("Unreconciled amount", unreconciled, bold = true, negativeInBrackets = true)
            if (unreconciled != 0L) {
                Text(
                    "Must be zero before filing. Adjust personal expenses, opening balance or inflows.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Text(
                    "â  Reconciled â wealth statement balances.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(90.dp))
    }
}
