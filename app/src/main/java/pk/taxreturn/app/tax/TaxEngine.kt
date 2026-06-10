package pk.taxreturn.app.tax

import pk.taxreturn.app.model.ReturnData
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

/**
 * Tax computation for a salaried individual, Tax Year 2026 (1 Jul 2025 - 30 Jun 2026),
 * per the Income Tax Ordinance 2001 as amended by the Finance Act 2025.
 *
 * Salaried slabs (Division I, Part I, First Schedule - where salary exceeds 75% of taxable income):
 *   up to 600,000                     : 0%
 *   600,001 - 1,200,000               : 1% of amount above 600,000
 *   1,200,001 - 2,200,000             : 6,000 + 11% of amount above 1,200,000
 *   2,200,001 - 3,200,000             : 116,000 + 23% of amount above 2,200,000
 *   3,200,001 - 4,100,000             : 346,000 + 30% of amount above 3,200,000
 *   above 4,100,000                   : 616,000 + 35% of amount above 4,100,000
 * Surcharge u/s 4AB: 9% of tax where taxable income > Rs 10,000,000.
 * Profit on debt u/s 7B: bank deposits 20%, National Savings/Govt securities 15%
 * (final tax for individuals where total profit on debt does not exceed Rs 5,000,000).
 * Dividend u/s 5: 15% standard.
 */
object TaxEngine {

    const val TAX_YEAR = "2026"
    const val SURCHARGE_THRESHOLD = 10_000_000L
    const val SURCHARGE_RATE = 0.09
    const val POD_BANK_RATE = 0.20
    const val POD_NSS_RATE = 0.15
    const val POD_FINAL_LIMIT = 5_000_000L
    const val DIVIDEND_RATE = 0.15

    data class Computation(
        val taxableSalary: Long,
        val propertyIncome: Long,
        val normalIncome: Long,          // before deductible allowances
        val taxableIncome: Long,         // after Zakat
        val isSalariedCase: Boolean,     // salary > 75% of taxable income
        val normalTaxGross: Long,        // slab tax before reductions
        val teacherReduction: Long,
        val surcharge: Long,
        val pensionCredit: Long,
        val donationCredit: Long,
        val normalTaxNet: Long,          // after reductions, surcharge, credits
        val finalTaxProfitOnDebt: Long,
        val finalTaxDividend: Long,
        val totalTaxChargeable: Long,
        val totalTaxPaid: Long,
        val balance: Long,               // positive = payable, negative = refundable
        val closingNetAssets: Long,
        val increaseInNetAssets: Long,
        val inflows: Long,
        val outflows: Long,
        val unreconciled: Long,
        val warnings: List<String>
    )

    fun salariedSlabTax(taxable: Long): Long {
        val t = taxable.toDouble()
        val tax = when {
            taxable <= 600_000L -> 0.0
            taxable <= 1_200_000L -> 0.01 * (t - 600_000)
            taxable <= 2_200_000L -> 6_000 + 0.11 * (t - 1_200_000)
            taxable <= 3_200_000L -> 116_000 + 0.23 * (t - 2_200_000)
            taxable <= 4_100_000L -> 346_000 + 0.30 * (t - 3_200_000)
            else -> 616_000 + 0.35 * (t - 4_100_000)
        }
        return tax.roundToLong()
    }

    fun compute(d: ReturnData): Computation {
        val warnings = mutableListOf<String>()

        // --- Income under normal tax regime ---
        val taxableSalary = max(0L, d.grossSalary - d.exemptMedicalAllowance - d.otherExemptSalary)
        val propertyIncome = max(0L, d.rentReceived - d.propertyDeductions)
        val normalIncome = taxableSalary + propertyIncome + d.otherIncome
        val taxableIncome = max(0L, normalIncome - d.zakat)

        val isSalariedCase = taxableIncome == 0L || taxableSalary.toDouble() > 0.75 * taxableIncome
        if (!isSalariedCase) {
            warnings.add(
                "Salary is not more than 75% of taxable income - salaried slab rates may not apply. " +
                "Non-salaried (higher) rates would apply; consult a tax adviser."
            )
        }

        // --- Normal tax ---
        val normalTaxGross = salariedSlabTax(taxableIncome)

        // Teacher/researcher 25% reduction on tax attributable to salary
        val teacherReduction = if (d.isTeacherOrResearcher && taxableIncome > 0) {
            (0.25 * normalTaxGross * (taxableSalary.toDouble() / taxableIncome)).roundToLong()
        } else 0L

        val taxAfterReduction = max(0L, normalTaxGross - teacherReduction)

        // Surcharge u/s 4AB (9% where taxable income > 10m)
        val surcharge = if (taxableIncome > SURCHARGE_THRESHOLD) {
            (SURCHARGE_RATE * taxAfterReduction).roundToLong()
        } else 0L

        // --- Tax credits at average rate of tax (Sections 61 & 63) ---
        val avgRate = if (taxableIncome > 0) taxAfterReduction.toDouble() / taxableIncome else 0.0
        val eligiblePension = min(d.pensionFundContribution, (0.20 * taxableIncome).roundToLong())
        val pensionCredit = (eligiblePension * avgRate).roundToLong()
        val eligibleDonation = min(d.charitableDonations, (0.30 * taxableIncome).roundToLong())
        val donationCredit = (eligibleDonation * avgRate).roundToLong()

        val normalTaxNet = max(0L, taxAfterReduction + surcharge - pensionCredit - donationCredit)

        // --- Final taxes ---
        val totalPod = d.profitOnDebtBank + d.profitOnDebtNss
        if (totalPod > POD_FINAL_LIMIT) {
            warnings.add(
                "Profit on debt exceeds Rs 5,000,000 - it falls outside the final tax regime (u/s 7B) " +
                "and is taxable at normal rates. This computation treats it as final tax; review with an adviser."
            )
        }
        val finalTaxProfitOnDebt =
            (d.profitOnDebtBank * POD_BANK_RATE + d.profitOnDebtNss * POD_NSS_RATE).roundToLong()
        val finalTaxDividend = (d.dividend * DIVIDEND_RATE).roundToLong()

        val totalTaxChargeable = normalTaxNet + finalTaxProfitOnDebt + finalTaxDividend

        // --- Taxes already paid / withheld ---
        val totalTaxPaid = d.taxDeductedSalary + d.taxDeductedRent + d.taxDeductedProfit +
                d.taxDeductedDividend + d.taxPhone + d.taxElectricity + d.taxVehicle +
                d.taxPropertyPurchase + d.taxPropertySale + d.taxOtherAdjustable

        val balance = totalTaxChargeable - totalTaxPaid

        // --- Wealth reconciliation (Section 116) ---
        val closingNetAssets = d.closingNetAssets
        val increase = closingNetAssets - d.openingNetAssets
        val inflows = taxableIncome + d.zakat +                       // income declared (before Zakat)
                d.exemptMedicalAllowance + d.otherExemptSalary +      // exempt income
                totalPod + d.dividend +                               // income under final tax
                d.foreignRemittance + d.giftsInheritanceOther
        val outflows = d.personalExpenses + totalTaxChargeable
        val unreconciled = increase - (inflows - outflows)
        if (unreconciled != 0L) {
            warnings.add(
                "Wealth statement is out of balance by Rs ${"%,d".format(unreconciled)}. " +
                "Inflows minus outflows must equal the increase in net assets before filing."
            )
        }

        return Computation(
            taxableSalary, propertyIncome, normalIncome, taxableIncome, isSalariedCase,
            normalTaxGross, teacherReduction, surcharge, pensionCredit, donationCredit,
            normalTaxNet, finalTaxProfitOnDebt, finalTaxDividend, totalTaxChargeable,
            totalTaxPaid, balance, closingNetAssets, increase, inflows, outflows,
            unreconciled, warnings
        )
    }
}
