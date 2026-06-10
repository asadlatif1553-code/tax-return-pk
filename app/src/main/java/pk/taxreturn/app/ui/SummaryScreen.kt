package pk.taxreturn.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import pk.taxreturn.app.model.ReturnData
import pk.taxreturn.app.tax.TaxEngine

@Composable
fun SummaryScreen(vm: TaxViewModel) {
    val d = vm.data
    val c = vm.computation
    val ctx = LocalContext.current

    ScreenColumn {
        WarningsCard(c.warnings)

        SectionCard("Income — enter in IRIS return (TY ${TaxEngine.TAX_YEAR})") {
            SummaryRow("Salary income u/s 12 (taxable)", c.taxableSalary)
            SummaryRow("Income from property (net)", c.propertyIncome)
            SummaryRow("Income from other sources (normal)", d.otherIncome)
            SummaryRow("Total income (normal regime)", c.normalIncome, bold = true)
            SummaryRow("Less: Zakat u/s 60", -d.zakat, negativeInBrackets = true)
            SummaryRow("Taxable income", c.taxableIncome, bold = true)
        }

        SectionCard("Tax computation") {
            SummaryRow("Tax on taxable income (salaried slabs)", c.normalTaxGross)
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
            SummaryRow("Final tax: profit on debt u/s 7B", c.finalTaxProfitOnDebt)
            SummaryRow("Final tax: dividend u/s 5", c.finalTaxDividend)
            SummaryRow("Total tax chargeable", c.totalTaxChargeable, bold = true)
        }

        SectionCard("Taxes paid / withheld") {
            SummaryRow("Salary u/s 149", d.taxDeductedSalary)
            SummaryRow("Rent u/s 155", d.taxDeductedRent)
            SummaryRow("Profit on debt u/s 151", d.taxDeductedProfit)
            SummaryRow("Dividend u/s 150", d.taxDeductedDividend)
            SummaryRow("Other adjustable taxes",
                d.taxPhone + d.taxElectricity + d.taxVehicle +
                        d.taxPropertyPurchase + d.taxPropertySale + d.taxOtherAdjustable)
            SummaryRow("Total taxes paid", c.totalTaxPaid, bold = true)
            Divider()
            val label = if (c.balance >= 0) "TAX PAYABLE with return" else "REFUNDABLE / carry forward"
            SummaryRow(label, if (c.balance >= 0) c.balance else -c.balance, bold = true)
        }

        SectionCard("Wealth statement u/s 116") {
            SummaryRow("Closing net assets (30-Jun-2026)", c.closingNetAssets)
            SummaryRow("Opening net assets (30-Jun-2025)", d.openingNetAssets)
            SummaryRow("Increase in net assets", c.increaseInNetAssets, negativeInBrackets = true)
            SummaryRow("Unreconciled amount", c.unreconciled, bold = true, negativeInBrackets = true)
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://iris.fbr.gov.pk")))
                },
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
            "Disclaimer: This app is a computation and preparation aid based on the Income Tax " +
            "Ordinance 2001 (Finance Act 2025 rates). It does not file your return. Verify all " +
            "figures on iris.fbr.gov.pk and consult a tax adviser for complex cases.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

fun buildSummaryText(d: ReturnData, c: TaxEngine.Computation): String = buildString {
    appendLine("INCOME TAX RETURN SUMMARY — TAX YEAR ${TaxEngine.TAX_YEAR}")
    appendLine("Name: ${d.name}   CNIC: ${d.cnic}")
    appendLine("--------------------------------------------")
    appendLine("INCOME (IRIS — Normal regime)")
    appendLine("Salary u/s 12:                 Rs ${fmt(c.taxableSalary)}")
    appendLine("Property income (net):         Rs ${fmt(c.propertyIncome)}")
    appendLine("Other sources:                 Rs ${fmt(d.otherIncome)}")
    appendLine("Zakat u/s 60:                  Rs ${fmt(d.zakat)}")
    appendLine("TAXABLE INCOME:                Rs ${fmt(c.taxableIncome)}")
    appendLine("--------------------------------------------")
    appendLine("TAX")
    appendLine("Tax on taxable income:         Rs ${fmt(c.normalTaxGross)}")
    if (c.teacherReduction > 0) appendLine("Teacher reduction (25%):      -Rs ${fmt(c.teacherReduction)}")
    if (c.surcharge > 0) appendLine("Surcharge u/s 4AB (9%):       +Rs ${fmt(c.surcharge)}")
    if (c.pensionCredit > 0) appendLine("Pension credit u/s 63:        -Rs ${fmt(c.pensionCredit)}")
    if (c.donationCredit > 0) appendLine("Donation credit u/s 61:       -Rs ${fmt(c.donationCredit)}")
    appendLine("Final tax (7B profit on debt): Rs ${fmt(c.finalTaxProfitOnDebt)}")
    appendLine("Final tax (dividend):          Rs ${fmt(c.finalTaxDividend)}")
    appendLine("TOTAL TAX CHARGEABLE:          Rs ${fmt(c.totalTaxChargeable)}")
    appendLine("Total taxes paid/withheld:     Rs ${fmt(c.totalTaxPaid)}")
    appendLine(if (c.balance >= 0) "TAX PAYABLE:                   Rs ${fmt(c.balance)}"
               else "REFUNDABLE:                    Rs ${fmt(-c.balance)}")
    appendLine("--------------------------------------------")
    appendLine("WEALTH STATEMENT u/s 116")
    appendLine("Opening net assets:            Rs ${fmt(d.openingNetAssets)}")
    appendLine("Closing net assets:            Rs ${fmt(c.closingNetAssets)}")
    appendLine("Increase in net assets:        Rs ${fmt(c.increaseInNetAssets)}")
    appendLine("Unreconciled amount:           Rs ${fmt(c.unreconciled)}")
    appendLine("--------------------------------------------")
    appendLine("Prepared with Tax Return PK (computation aid). File at https://iris.fbr.gov.pk")
}
