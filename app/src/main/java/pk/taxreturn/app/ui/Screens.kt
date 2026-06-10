package pk.taxreturn.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pk.taxreturn.app.model.ReturnData

@Composable
fun ScreenColumn(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        content()
        Spacer(Modifier.height(90.dp))
    }
}

@Composable
fun PersonalScreen(vm: TaxViewModel) {
    val d = vm.data
    ScreenColumn {
        SectionCard("Taxpayer Profile — Tax Year 2026") {
            Text(
                "Period: 1 July 2025 to 30 June 2026 • Return u/s 114, Wealth Statement u/s 116",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedTextField(
                value = d.name,
                onValueChange = { v -> vm.update { it.copy(name = v) } },
                label = { Text("Full name (as per CNIC)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = d.cnic,
                onValueChange = { v -> vm.update { it.copy(cnic = v.filter { c -> c.isDigit() }.take(13)) } },
                label = { Text("CNIC (13 digits, no dashes)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Full-time teacher / researcher", style = MaterialTheme.typography.bodyMedium)
                    Text("25% reduction in tax on salary (2nd Schedule, Part III)",
                        style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = d.isTeacherOrResearcher,
                    onCheckedChange = { v -> vm.update { it.copy(isTeacherOrResearcher = v) } })
            }
        }
        SectionCard("How this app works") {
            Text(
                "1. Enter your income, taxes withheld and assets in each tab.\n" +
                "2. The app computes your tax under the Income Tax Ordinance 2001 " +
                "(Finance Act 2025 rates for salaried individuals).\n" +
                "3. The Summary tab gives an IRIS-ready figure for every field, " +
                "which you then enter on iris.fbr.gov.pk to file.\n\n" +
                "This app is a preparation aid — it does not transmit anything to FBR.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun IncomeScreen(vm: TaxViewModel) {
    val d = vm.data
    ScreenColumn {
        SectionCard("Salary — Section 12 (per salary certificate)") {
            MoneyField("Gross annual salary", d.grossSalary) { v -> vm.update { it.copy(grossSalary = v) } }
            MoneyField("Exempt medical allowance (max 10% of basic)", d.exemptMedicalAllowance) { v ->
                vm.update { it.copy(exemptMedicalAllowance = v) } }
            MoneyField("Other exempt salary components", d.otherExemptSalary) { v ->
                vm.update { it.copy(otherExemptSalary = v) } }
            MoneyField("Tax deducted by employer u/s 149", d.taxDeductedSalary) { v ->
                vm.update { it.copy(taxDeductedSalary = v) } }
        }
        SectionCard("Property — Sections 15 / 15A") {
            MoneyField("Rent received in the year", d.rentReceived) { v -> vm.update { it.copy(rentReceived = v) } }
            MoneyField("Admissible deductions u/s 15A", d.propertyDeductions) { v ->
                vm.update { it.copy(propertyDeductions = v) } }
            MoneyField("Tax withheld on rent u/s 155", d.taxDeductedRent) { v ->
                vm.update { it.copy(taxDeductedRent = v) } }
        }
        SectionCard("Profit on debt & dividend (final tax)") {
            MoneyField("Bank / savings account profit (20%)", d.profitOnDebtBank) { v ->
                vm.update { it.copy(profitOnDebtBank = v) } }
            MoneyField("National Savings / Govt securities profit (15%)", d.profitOnDebtNss) { v ->
                vm.update { it.copy(profitOnDebtNss = v) } }
            MoneyField("Tax withheld on profit u/s 151", d.taxDeductedProfit) { v ->
                vm.update { it.copy(taxDeductedProfit = v) } }
            MoneyField("Dividend received (15%)", d.dividend) { v -> vm.update { it.copy(dividend = v) } }
            MoneyField("Tax withheld on dividend u/s 150", d.taxDeductedDividend) { v ->
                vm.update { it.copy(taxDeductedDividend = v) } }
        }
        SectionCard("Other income — Section 39 (normal rates)") {
            MoneyField("Any other taxable income", d.otherIncome) { v -> vm.update { it.copy(otherIncome = v) } }
        }
    }
}

@Composable
fun DeductionsScreen(vm: TaxViewModel) {
    val d = vm.data
    ScreenColumn {
        SectionCard("Deductible allowance") {
            MoneyField("Zakat paid under Zakat & Ushr Ordinance — s.60", d.zakat) { v ->
                vm.update { it.copy(zakat = v) } }
        }
        SectionCard("Tax credits (at average rate of tax)") {
            MoneyField("Approved pension fund (VPS) contribution — s.63", d.pensionFundContribution) { v ->
                vm.update { it.copy(pensionFundContribution = v) } }
            Text("Credit allowed on up to 20% of taxable income",
                style = MaterialTheme.typography.bodySmall)
            MoneyField("Donations to approved NPOs — s.61", d.charitableDonations) { v ->
                vm.update { it.copy(charitableDonations = v) } }
            Text("Credit allowed on up to 30% of taxable income",
                style = MaterialTheme.typography.bodySmall)
        }
        SectionCard("Other adjustable taxes paid (claim against liability)") {
            MoneyField("Telephone / internet — u/s 236", d.taxPhone) { v -> vm.update { it.copy(taxPhone = v) } }
            MoneyField("Electricity bills — u/s 235", d.taxElectricity) { v ->
                vm.update { it.copy(taxElectricity = v) } }
            MoneyField("Vehicle token / purchase — u/s 234 / 231B", d.taxVehicle) { v ->
                vm.update { it.copy(taxVehicle = v) } }
            MoneyField("On purchase of property — u/s 236K", d.taxPropertyPurchase) { v ->
                vm.update { it.copy(taxPropertyPurchase = v) } }
            MoneyField("On sale of property — u/s 236C", d.taxPropertySale) { v ->
                vm.update { it.copy(taxPropertySale = v) } }
            MoneyField("Other adjustable withholding tax", d.taxOtherAdjustable) { v ->
                vm.update { it.copy(taxOtherAdjustable = v) } }
        }
    }
}

@Composable
fun WealthScreen(vm: TaxViewModel) {
    val d = vm.data
    val c = vm.computation
    ScreenColumn {
        SectionCard("Wealth Statement u/s 116 — as at 30 June 2026") {
            MoneyField("Net assets at 30 June 2025 (opening)", d.openingNetAssets) { v ->
                vm.update { it.copy(openingNetAssets = v) } }
            Divider()
            MoneyField("Immovable property (at cost)", d.assetProperty) { v ->
                vm.update { it.copy(assetProperty = v) } }
            MoneyField("Motor vehicles (at cost)", d.assetVehicles) { v ->
                vm.update { it.copy(assetVehicles = v) } }
            MoneyField("Bank balances", d.assetBankBalances) { v ->
                vm.update { it.copy(assetBankBalances = v) } }
            MoneyField("Cash in hand", d.assetCash) { v -> vm.update { it.copy(assetCash = v) } }
            MoneyField("Investments (shares, funds, NSS…)", d.assetInvestments) { v ->
                vm.update { it.copy(assetInvestments = v) } }
            MoneyField("Other assets", d.assetOther) { v -> vm.update { it.copy(assetOther = v) } }
            MoneyField("Liabilities (loans, payables)", d.liabilities) { v ->
                vm.update { it.copy(liabilities = v) } }
        }
        SectionCard("Reconciliation inputs") {
            MoneyField("Personal / household expenses for the year", d.personalExpenses) { v ->
                vm.update { it.copy(personalExpenses = v) } }
            MoneyField("Foreign remittance — s.111(4)", d.foreignRemittance) { v ->
                vm.update { it.copy(foreignRemittance = v) } }
            MoneyField("Gifts / inheritance / other inflows", d.giftsInheritanceOther) { v ->
                vm.update { it.copy(giftsInheritanceOther = v) } }
        }
        SectionCard("Reconciliation") {
            SummaryRow("Closing net assets", c.closingNetAssets)
            SummaryRow("Opening net assets", d.openingNetAssets)
            SummaryRow("Increase / (decrease)", c.increaseInNetAssets, negativeInBrackets = true)
            Divider()
            SummaryRow("Total inflows (income + exempt + remittance)", c.inflows)
            SummaryRow("Total outflows (expenses + tax)", c.outflows)
            SummaryRow("Unreconciled amount", c.unreconciled, bold = true, negativeInBrackets = true)
            if (c.unreconciled != 0L) {
                Text(
                    "Must be zero before filing. Adjust expenses, cash or opening balance.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun WarningsCard(warnings: List<String>) {
    if (warnings.isEmpty()) return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Review before filing", fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer)
            warnings.forEach {
                Text("• $it", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
