@file:OptIn(ExperimentalMaterial3Api::class)

package pk.taxreturn.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ââ Accent colours for income sources ââââââââââââââââââââââââââââââââââââââââ
private val ColorSalary       = Color(0xFF1565C0)
private val ColorBusiness     = Color(0xFFE65100)
private val ColorProperty     = Color(0xFF2E7D32)
private val ColorCapitalGains = Color(0xFF6A1B9A)
private val ColorOther        = Color(0xFF00695C)

@Composable
fun IncomeScreen(vm: TaxViewModel) {
    val d = vm.data

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        // ââ Source selector ââââââââââââââââââââââââââââââââââââââââââââââââ
        Text(
            "Select your income sources for TY 2026",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        SourceCard(
            icon = Icons.Filled.Work, title = "Salary",
            subtitle = "Employment income â Section 12",
            accentColor = ColorSalary, selected = d.hasSalary,
            onClick = { vm.update { it.copy(hasSalary = !it.hasSalary) } }
        )
        Spacer(Modifier.height(8.dp))
        SourceCard(
            icon = Icons.Filled.Business, title = "Business / Profession",
            subtitle = "Trading, services, manufacturing â Sections 18-36B",
            accentColor = ColorBusiness, selected = d.hasBusiness,
            onClick = { vm.update { it.copy(hasBusiness = !it.hasBusiness) } }
        )
        Spacer(Modifier.height(8.dp))
        SourceCard(
            icon = Icons.Filled.Home, title = "Property / Rental",
            subtitle = "Rent from immovable property â Sections 15/15A",
            accentColor = ColorProperty, selected = d.hasProperty,
            onClick = { vm.update { it.copy(hasProperty = !it.hasProperty) } }
        )
        Spacer(Modifier.height(8.dp))
        SourceCard(
            icon = Icons.Filled.TrendingUp, title = "Capital Gains",
            subtitle = "Property disposal, listed securities â Sections 37/37A",
            accentColor = ColorCapitalGains, selected = d.hasCapitalGains,
            onClick = { vm.update { it.copy(hasCapitalGains = !it.hasCapitalGains) } }
        )
        Spacer(Modifier.height(8.dp))
        SourceCard(
            icon = Icons.Filled.AccountBalance, title = "Other Sources",
            subtitle = "Profit on debt, dividends, foreign income â Section 39",
            accentColor = ColorOther, selected = d.hasOtherSources,
            onClick = { vm.update { it.copy(hasOtherSources = !it.hasOtherSources) } }
        )

        if (!d.hasSalary && !d.hasBusiness && !d.hasProperty && !d.hasCapitalGains && !d.hasOtherSources) {
            Card(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.TouchApp, null, Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("Tap a card above to add an income source",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
        }

        if (d.hasSalary)       SalarySection(vm)
        if (d.hasBusiness)     BusinessSection(vm)
        if (d.hasProperty)     PropertySection(vm)
        if (d.hasCapitalGains) CapitalGainsSection(vm)
        if (d.hasOtherSources) OtherSourcesSection(vm)

        Spacer(Modifier.height(90.dp))
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// SALARY
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun SalarySection(vm: TaxViewModel) {
    val d = vm.data
    SectionCard("Salary Income â Section 12") {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Mode:", style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = 10.dp))
            FilterChip(
                selected = d.salarySimpleMode,
                onClick = { vm.update { it.copy(salarySimpleMode = true) } },
                label = { Text("Simple") }
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = !d.salarySimpleMode,
                onClick = { vm.update { it.copy(salarySimpleMode = false) } },
                label = { Text("Advanced") }
            )
        }
        Spacer(Modifier.height(6.dp))

        if (d.salarySimpleMode) {
            // ââ SIMPLE ââââââââââââââââââââââââââââââââââââââââââââââââââââ
            Text("Enter total taxable salary from your salary certificate.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            MoneyField("Gross annual salary (all taxable components)", d.grossSalary) { v ->
                vm.update { it.copy(grossSalary = v) }
            }
            MoneyField("Exempt medical allowance (max 10% of basic)", d.exemptMedicalAllowance) { v ->
                vm.update { it.copy(exemptMedicalAllowance = v) }
            }
            MoneyField("Other exempt salary components", d.otherExemptSalary) { v ->
                vm.update { it.copy(otherExemptSalary = v) }
            }
            MoneyField("Tax deducted by employer u/s 149", d.taxDeductedSalary) { v ->
                vm.update { it.copy(taxDeductedSalary = v) }
            }
        } else {
            // ââ ADVANCED ââââââââââââââââââââââââââââââââââââââââââââââââââ
            Text("Break down salary components per your salary certificate.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))

            Text("TAXABLE COMPONENTS", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
            MoneyField("Basic salary", d.basicSalary) { v -> vm.update { it.copy(basicSalary = v) } }
            MoneyField("House rent allowance (HRA)", d.houseRentAllowance) { v -> vm.update { it.copy(houseRentAllowance = v) } }
            MoneyField("Medical allowance", d.medicalAllowance) { v -> vm.update { it.copy(medicalAllowance = v) } }
            MoneyField("Conveyance allowance", d.conveyanceAllowance) { v -> vm.update { it.copy(conveyanceAllowance = v) } }
            MoneyField("Other allowances", d.otherAllowances) { v -> vm.update { it.copy(otherAllowances = v) } }
            MoneyField("Bonus / performance pay", d.bonusPerformancePay) { v -> vm.update { it.copy(bonusPerformancePay = v) } }
            MoneyField("Perquisites & benefits (car, club, housing)", d.perquisitesAndBenefits) { v ->
                vm.update { it.copy(perquisitesAndBenefits = v) }
            }

            Spacer(Modifier.height(6.dp))
            Text("EXEMPT COMPONENTS", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(vertical = 4.dp))
            MoneyField("Exempt medical allowance (max 10% of basic)", d.exemptMedicalAllowance) { v ->
                vm.update { it.copy(exemptMedicalAllowance = v) }
            }
            MoneyField("Exempt HRA (as per certificate)", d.exemptHouseRentAllowance) { v ->
                vm.update { it.copy(exemptHouseRentAllowance = v) }
            }
            MoneyField("Other exempt components", d.otherExemptSalary) { v ->
                vm.update { it.copy(otherExemptSalary = v) }
            }

            val computedTaxable = maxOf(
                0L,
                d.basicSalary + d.houseRentAllowance + d.medicalAllowance +
                        d.conveyanceAllowance + d.otherAllowances + d.bonusPerformancePay +
                        d.perquisitesAndBenefits - d.exemptMedicalAllowance -
                        d.exemptHouseRentAllowance - d.otherExemptSalary
            )
            Divider()
            PLRow("Taxable salary", computedTaxable, isResult = true)

            Spacer(Modifier.height(6.dp))
            Text("WITHHOLDING TAX", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
            MoneyField("Tax deducted by employer u/s 149", d.taxDeductedSalary) { v ->
                vm.update { it.copy(taxDeductedSalary = v) }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Full-time teacher / researcher",
                        style = MaterialTheme.typography.bodyMedium)
                    Text("25% tax reduction on salary (2nd Schedule III)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = d.isTeacherOrResearcher,
                    onCheckedChange = { v -> vm.update { it.copy(isTeacherOrResearcher = v) } }
                )
            }
        }
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// BUSINESS
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun BusinessSection(vm: TaxViewModel) {
    val d = vm.data
    SectionCard("Business / Profession â Sections 18-36B") {

        DropdownField("Type of entity", d.businessType,
            listOf("Sole Proprietor", "AOP Share", "Partnership Share")
        ) { v -> vm.update { it.copy(businessType = v) } }

        DropdownField("Nature of business", d.businessNature,
            listOf("Trading", "Services", "Manufacturing", "Construction", "Retail", "Agriculture")
        ) { v -> vm.update { it.copy(businessNature = v) } }

        OutlinedTextField(
            value = d.businessName,
            onValueChange = { v -> vm.update { it.copy(businessName = v) } },
            label = { Text("Business / Trade name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(Modifier.height(8.dp))
        Text("INCOME STATEMENT â Profit & Loss", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))

        MoneyField("Revenue / Gross sales / Turnover", d.businessRevenue) { v ->
            vm.update { it.copy(businessRevenue = v) }
        }

        Spacer(Modifier.height(4.dp))
        Text("DIRECT EXPENSES (Cost of Sales / COGS)", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Cost of goods sold / Direct labour & material", d.businessCostOfSales) { v ->
            vm.update { it.copy(businessCostOfSales = v) }
        }

        Divider()
        val grossProfit = d.businessRevenue - d.businessCostOfSales
        PLRow("Gross Profit", maxOf(0L, grossProfit), isResult = true)
        if (grossProfit < 0) Text("â  Revenue is less than cost of sales",
            color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

        Spacer(Modifier.height(4.dp))
        Text("INDIRECT EXPENSES (Administrative & Operating)", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Admin, salaries, rent, utilities", d.businessAdminExpenses) { v ->
            vm.update { it.copy(businessAdminExpenses = v) }
        }
        MoneyField("Finance charges / bank interest on loans", d.businessFinanceCharges) { v ->
            vm.update { it.copy(businessFinanceCharges = v) }
        }
        MoneyField("Marketing, miscellaneous & other indirect", d.businessOtherIndirectExp) { v ->
            vm.update { it.copy(businessOtherIndirectExp = v) }
        }

        Spacer(Modifier.height(4.dp))
        Text("TAX DEDUCTIONS (u/s 22-25)", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Depreciation u/s 22 (normal allowance)", d.businessDepreciation) { v ->
            vm.update { it.copy(businessDepreciation = v) }
        }
        MoneyField("Initial allowance u/s 23 (25% on new plant & machinery)", d.businessInitialAllowance) { v ->
            vm.update { it.copy(businessInitialAllowance = v) }
        }
        MoneyField("Pre-commencement expenditure u/s 25", d.businessPrecommencement) { v ->
            vm.update { it.copy(businessPrecommencement = v) }
        }
        MoneyField("Other allowable deductions", d.businessOtherDeductions) { v ->
            vm.update { it.copy(businessOtherDeductions = v) }
        }

        Divider()
        val netIncome = maxOf(
            0L,
            d.businessRevenue - d.businessCostOfSales - d.businessAdminExpenses -
                    d.businessFinanceCharges - d.businessOtherIndirectExp -
                    d.businessDepreciation - d.businessInitialAllowance -
                    d.businessPrecommencement - d.businessOtherDeductions
        )
        PLRow("Net Business Income (Taxable)", netIncome, isResult = true)

        Spacer(Modifier.height(8.dp))
        Text("WITHHOLDING TAX ON BUSINESS RECEIPTS", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Tax deducted on business receipts u/s 153", d.taxDeductedBusiness) { v ->
            vm.update { it.copy(taxDeductedBusiness = v) }
        }
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// PROPERTY
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun PropertySection(vm: TaxViewModel) {
    val d = vm.data
    SectionCard("Property / Rental Income â Sections 15/15A") {
        MoneyField("Gross annual rent received from all properties", d.rentReceived) { v ->
            vm.update { it.copy(rentReceived = v) }
        }

        Spacer(Modifier.height(6.dp))
        Text("ADMISSIBLE DEDUCTIONS â Section 15A", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Property tax paid to local authority [s.15A(a)]", d.propertyTax) { v ->
            vm.update { it.copy(propertyTax = v) }
        }
        MoneyField("Insurance premium on building [s.15A(b)]", d.propertyInsurance) { v ->
            vm.update { it.copy(propertyInsurance = v) }
        }
        MoneyField("Repairs & maintenance [s.15A(c) â 1/5 of rent or actual]", d.propertyMaintenance) { v ->
            vm.update { it.copy(propertyMaintenance = v) }
        }
        MoneyField("Interest on loan for property [s.15A(d)]", d.propertyInterestOnLoan) { v ->
            vm.update { it.copy(propertyInterestOnLoan = v) }
        }
        MoneyField("Other admissible deductions", d.propertyOtherDeductions) { v ->
            vm.update { it.copy(propertyOtherDeductions = v) }
        }

        Divider()
        val netProperty = maxOf(
            0L,
            d.rentReceived - d.propertyTax - d.propertyInsurance -
                    d.propertyMaintenance - d.propertyInterestOnLoan - d.propertyOtherDeductions
        )
        PLRow("Net property income (taxable at normal rates)", netProperty, isResult = true)

        Spacer(Modifier.height(6.dp))
        Text("WITHHOLDING TAX", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Tax withheld by tenant u/s 155", d.taxDeductedRent) { v ->
            vm.update { it.copy(taxDeductedRent = v) }
        }
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// CAPITAL GAINS
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun CapitalGainsSection(vm: TaxViewModel) {
    val d = vm.data
    SectionCard("Capital Gains â Sections 37/37A") {

        Text("IMMOVABLE PROPERTY DISPOSAL â Section 37A(8) [Final Tax]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))

        DropdownField("Property type", d.propertyType,
            listOf("Open Plot", "Built Residential", "Built Commercial")
        ) { v -> vm.update { it.copy(propertyType = v) } }

        val holdingOptions = listOf("< 1 year", "1â2 years", "2â3 years", "3â4 years",
            "4â5 years", "5â6 years", "6+ years")
        DropdownField("Holding period", holdingOptions.getOrElse(d.propertyHoldingYears) { "< 1 year" },
            holdingOptions
        ) { v -> vm.update { it.copy(propertyHoldingYears = holdingOptions.indexOf(v).coerceAtLeast(0)) } }

        val rate = when (d.propertyType) {
            "Open Plot" -> when (d.propertyHoldingYears) {
                0 -> 15.0; 1 -> 12.5; 2 -> 10.0; 3 -> 7.5; 4 -> 5.0; 5 -> 2.5; else -> 0.0
            }
            else -> when (d.propertyHoldingYears) {
                0 -> 15.0; 1 -> 12.5; 2 -> 10.0; 3 -> 7.5; 4 -> 5.0; else -> 0.0
            }
        }

        Card(
            Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (rate == 0.0) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            )
        ) {
            Text(
                if (rate == 0.0) "â  Exempt â gain is not taxable at this holding period"
                else "Tax rate: $rate%  (final tax on capital gain)",
                Modifier.padding(12.dp),
                fontWeight = FontWeight.SemiBold,
                color = if (rate == 0.0) Color(0xFF2E7D32) else Color(0xFFE65100),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        MoneyField("Capital gain on disposal (sale price â cost/FMV)", d.propertyGainAmount) { v ->
            vm.update { it.copy(propertyGainAmount = v) }
        }
        if (rate > 0 && d.propertyGainAmount > 0) {
            val tax = (d.propertyGainAmount * rate / 100).toLong()
            Text("Estimated capital gains tax: Rs ${fmt(tax)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp))
        }

        HorizontalDivider(Modifier.padding(vertical = 10.dp))

        Text("LISTED SECURITIES â Section 37A(6) [Final Tax]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Gain on securities held < 1 year  (15% final tax)", d.securitiesGainLessThan1Yr) { v ->
            vm.update { it.copy(securitiesGainLessThan1Yr = v) }
        }
        MoneyField("Gain on securities held 1-2 years (12.5% final tax)", d.securitiesGain1To2Yr) { v ->
            vm.update { it.copy(securitiesGain1To2Yr = v) }
        }
        MoneyField("Gain on securities held > 2 years  (0% - exempt)", d.securitiesGainAbove2Yr) { v ->
            vm.update { it.copy(securitiesGainAbove2Yr = v) }
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        MoneyField("Withholding tax deducted (u/s 236C / 233)", d.taxDeductedCapitalGains) { v ->
            vm.update { it.copy(taxDeductedCapitalGains = v) }
        }
    }
}

// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
// OTHER SOURCES
// âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
@Composable
fun OtherSourcesSection(vm: TaxViewModel) {
    val d = vm.data
    SectionCard("Other Sources â Section 39 / Final Taxes") {

        Text("PP¯FIT ON DEBT â Section 7B [Final Tax]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Bank / savings account profit  (20% final tax)", d.profitOnDebtBank) { v ->
            vm.update { it.copy(profitOnDebtBank = v) }
        }
        MoneyField("National Savings / Govt securities (15% final tax)", d.profitOnDebtNss) { v ->
            vm.update { it.copy(profitOnDebtNss = v) }
        }
        MoneyField("Tax withheld on profit u/s 151", d.taxDeductedProfit) { v ->
            vm.update { it.copy(taxDeductedProfit = v) }
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("DIVIDEND INCOME â Section 5 [Final Tax]",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Dividend received (15% final tax)", d.dividend) { v ->
            vm.update { it.copy(dividend = v) }
        }
        MoneyField("Tax withheld by company u/s 150", d.taxDeductedDividend) { v ->
            vm.update { it.copy(taxDeductedDividend = v) }
        }

        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Text("OTHER INCOME â Normal Tax Rates", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(vertical = 4.dp))
        MoneyField("Foreign source income (normal rates)", d.foreignIncome) { v ->
            vm.update { it.copy(foreignIncome = v) }
        }
        MoneyField("Agricultural income (exempt from federal tax - for record only)", d.agriculturalIncome) { v ->
            vm.update { it.copy(agriculturalIncome = v) }
        }
        MoneyField("Any other taxable income - Section 39", d.otherIncome) { v ->
            vm.update { it.copy(otherIncome = v) }
        }
    }
}
