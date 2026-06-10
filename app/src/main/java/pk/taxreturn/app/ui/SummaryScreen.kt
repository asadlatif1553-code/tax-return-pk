package pk.taxreturn.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pk.taxreturn.app.model.ReturnData
import pk.taxreturn.app.tax.TaxEngine

@Composable
fun SummaryScreen(vm: TaxViewModel) {
    val d = vm.data
    val c = vm.computation
    val ctx = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        WarningsCard(c.warnings)

        // ââ Tax status banner ââââââââââââââââââââââââââââââââââââââââââââââ
        val isRefund = c.balance < 0
        Card(
            Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRefund) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    if (isRefund) "Refund Due" else "Tax Payable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isRefund) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
                Text(
                    "Rs ${fmt(if (isRefund) -c.balance else c.balance)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isRefund) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
                Text(
                    if (isRefund) "You are entitled to a refund or carry-forward."
                    else "Deposit this amount with return at iris.fbr.gov.pk.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ââ Income breakdown âââââââââââââââââââââââââââââââââââââââââââââââ
        SectionCard("Income â Enter in IRIS (TY ${TaxEngine.TAX_YEAR})") {
            if (d.hasSalary)    SummaryRow("Salary income u/s 12 (taxable)",     c.taxableSalary)
            if (d.hasBusiness)  SummaryRow("Business / profession income",        c.businessIncome)
            if (d.hasProperty)  SummaryRow("Income from property (net)",          c.propertyIncome)
            if (d.hasOtherSources) SummaryRow("Other income â Section 39",         c.otherNormalIncome)
            Divider()
            SummaryRow("Total income (normal regime)", c.normalIncome, bold = true)
            if (d.zakat > 0) SummaryRow("Less: Zakat u/s 60", -d.zakat, negativeInBrackets = true)
            if (d.workerWelfareFund > 0) SummaryRow("Less: WWF u/s 60A", -d.workerWelfareFund, negativeInBrackets = true)
            SummaryRow("Taxable income", c.taxableIncome, bold = true)
            if (c.isSalariedCase) {
                Text("â Salaried slabs apply (salary > 75% of taxable income)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp))
            } else {
                Text("â  Non-salaried slabs apply (salary â¤ 75% of taxable income)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }

        // ââ Tax computation ââââââââââââââââââââââââââââââââââââââââââââââââ
        SectionCard("Tax Computation") {
            val slabLabel = if (c.isSalariedCase) "Salaried slabs (Division I)" else "Non-salaried slabs (Division II)"
            SummaryRow("Tax on taxable income ($slabLabel)", c.normalTaxGross)
            if (c.teacherReduction > 0)
                SummaryRow("Less: Teacher/researcher reduction (25%)", -c.teacherReduction, negativeInBrackets = true)
            if (c.surcharge > 0)
                SummaryRow("Add: Surcharge u/s 4AB (9%)", c.surcharge)
            if (c.pensionCredit > 0)
                SummaryRow("Less: Pension fund credit u/s 63", -c.pensionCredit, negativeInBrackets = true)
            if (c.donationCredit > 0)
                SummaryRow("Less: Donation credit u/s 61", -c.donationCredit, negativeInBrackets = true)
            SummaryRow("Tax under normal regime", c.normalTaxNet, bold = true)
            Divider()
            if (c.finalTaxProfitOnDebt > 0)
                SummaryRow("Final tax: profit on debt u/s 7B", c.finalTaxProfitOnDebt)
            if (c.finalTaxDividend > 0)
                SummaryRow("Final tax: dividend u/s 5", c.finalTaxDividend)
            if (c.finalTaxCapitalGains > 0)
                SummaryRow("Final tax: capital gains", c.finalTaxCapitalGains)
            Divider()
            SummaryRow("Total tax chargeable", c.totalTaxChargeable, bold = true)
        }

        // ââ Taxes paid âââââââââââââââââââââââââââââââââââââââââââââââââââââ
        SectionCard("Taxes Paid / Withheld") {
            if (d.taxDeductedSalary > 0)         SummaryRow("Salary u/s 149",                 d.taxDeductedSalary)
            if (d.taxDeductedBusiness > 0)        SummaryRow("Business receipts u/s 153",       d.taxDeductedBusiness)
            if (d.taxDeductedRent > 0)            SummaryRow("Rent u/s 155",                    d.taxDeductedRent)
            if (d.taxDeductedProfit > 0)          SummaryRow("Profit on debt u/s 151",          d.taxDeductedProfit)
            if (d.taxDeductedDividend > 0)        SummaryRow("Dividend u/s 150",                d.taxDeductedDividend)
            if (d.taxDeductedCapitalGains > 0)    SummaryRow("Capital gains u/s 236C/233",      d.taxDeductedCapitalGains)
            val otherWHT = d.taxPhone + d.taxElectricity + d.taxVehicle +
                    d.taxPropertyPurchase + d.taxPropertySale + d.taxOtherAdjustable
            if (otherWHT > 0) SummaryRow("Other adjustable taxes", otherWHT)
            Divider()
            SummaryRow("Total taxes paid/withheld", c.totalTaxPaid, bold = true)
            Spacer(Modifier.height(6.dp))
            val label = if (c.balance >= 0) "TAX PAYABLE with return" else "REFUNDABLE / carry forward"
            SummaryRow(label, if (c.balance >= 0) c.balance else -c.balance, bold = true)
        }

        // ââ Wealth statement ââââââââââââââââââââââââââââââââââââââââââââââââ
        SectionCard("Wealth Statement u/s 116") {
            SummaryRow("Opening net assets (30 Jun 2025)",  d.openingNetAssets)
            SummaryRow("Closing net assets (30 Jun 2026)",  c.closingNetAssets)
            SummaryRow("Increase in net assets",            c.increaseInNetAssets, negativeInBrackets = true)
            SummaryRow("Unreconciled amount",               c.unreconciled, bold = true, negativeInBrackets = true)
        }

        // ââ Action buttons ââââââââââââââââââââââââââââââââââââââââââââââââââ
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://iris.fbr.gov.pk"))) },
                modifier = Modifier.weight(1f)
            ) { Text("Open IRIS to file") }
            OutlinedButton(
                onClick = {
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Income Tax Return Summary TY ${TaxEngine.TAX_YEAR}")
                        putExtra(Intent.EXTRA_TEXT, buildSummaryText(d, c))
                    }
                    ctx.startActivity(Intent.createChooser(share, "Share summary"))
                },
                modifier = Modifier.weight(1f)
            ) { Text("Share summary") }
        }

        Text(
            "Disclaimer: This app is a computation and preparation aid. Rates are per the Income Tax " +
            "Ordinance 2001 (Finance Act 2025). Verify figures on iris.fbr.gov.pk. Not a filing service.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(90.dp))
    }
}

fun buildSummaryText(d: ReturnData, c: TaxEngine.Computation): String = buildString {
    appendLine("INCOME TAX RETURN SUMMARY â TAX YEAR ${TaxEngine.TAX_YEAR}")
    appendLine("Name: ${d.name}   CNIC: ${d.cnic}   NTN: ${d.ntn}")
    appendLine("=".repeat(50))
    appendLine("INCOME (Normal Regime)")
    if (d.hasSalary)       appendLine("Salary u/s 12:              Rs ${fmt(c.taxableSalary)}")
    if (d.hasBusiness)     appendLine("Business income:            Rs ${fmt(c.businessIncome)}")
    if (d.hasProperty)     appendLine("Property income (net):      Rs ${fmt(c.propertyIncome)}")
    if (d.hasOtherSources) appendLine("Other income:               Rs ${fmt(c.otherNormalIncome)}")
    if (d.zakat > 0)       appendLine("Less: Zakat u/s 60:         Rs (${fmt(d.zakat)})")
    appendLine("TAXABLE INCOME:             Rs ${fmt(c.taxableIncome)}")
    appendLine("-".repeat(50))
    appendLine("TAX COMPUTATION")
    appendLine("Slab tax (${if (c.isSalariedCase) "salaried" else "non-salaried"}): Rs ${fmt(c.normalTaxGross)}")
    if (c.teacherReduction > 0) appendLine("Teacher reduction (25%):   -Rs ${fmt(c.teacherReduction)}")
    if (c.surcharge > 0)        appendLine("Surcharge u/s 4AB (9%):   +Rs ${fmt(c.surcharge)}")
    if (c.pensionCredit > 0)    appendLine("Pension credit u/s 63:    -Rs ${fmt(c.pensionCredit)}")
    if (c.donationCredit > 0)   appendLine("Donation credit u/s 61:   -Rs ${fmt(c.donationCredit)}")
    if (c.finalTaxProfitOnDebt > 0) appendLine("Final tax (7B POD):         Rs ${fmt(c.finalTaxProfitOnDebt)}")
    if (c.finalTaxDividend > 0)     appendLine("Final tax (dividend):       Rs ${fmt(c.finalTaxDividend)}")
    if (c.finalTaxCapitalGains > 0) appendLine("Final tax (cap gains):      Rs ${fmt(c.finalTaxCapitalGains)}")
    appendLine("TOTAL TAX CHARGEABLE:       Rs ${fmt(c.totalTaxChargeable)}")
    appendLine("Taxes paid/withheld:        Rs ${fmt(c.totalTaxPaid)}")
    appendLine(if (c.balance >= 0) "TAX PAYABLE:                Rs ${fmt(c.balance)}"
               else                "REFUNDABLE:                 Rs ${fmt(-c.balance)}")
    appendLine("-".repeat(50))
    appendLine("WEALTH STATEMENT u/s 116")
    appendLine("Opening net assets:         Rs ${fmt(d.openingNetAssets)}")
    appendLine("Closing net assets:         Rs ${fmt(c.closingNetAssets)}")
    appendLine("Increase in net assets:     Rs ${fmt(c.increaseInNetAssets)}")
    appendLine("Unreconciled amount:        Rs ${fmt(c.unreconciled)}")
    appendLine("-".repeat(50))
    appendLine("Prepared with Tax Return PK (computation aid only).")
    appendLine("File at https://iris.fbr.gov.pk")
}
