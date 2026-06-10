package pk.taxreturn.app.tax

import pk.taxreturn.app.model.ReturnData
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong

/**
 * Tax computation for Tax Year 2026 (1 Jul 2025 â 30 Jun 2026).
 * Income Tax Ordinance 2001, as amended by Finance Act 2025.
 *
 * VERIFY rates against the ordinance PDF in the workspace folder before release.
 *
 * âââ SALARIED individual (salary > 75% of taxable income) â Division I, First Schedule âââ
 *   â¤ 600,000              : 0%
 *   600,001 â 1,200,000    : 1%   of excess over 600,000
 *   1,200,001 â 2,200,000  : Rs 6,000 + 11% of excess over 1,200,000
 *   2,200,001 â 3,200,000  : Rs 116,000 + 23% of excess over 2,200,000
 *   3,200,001 â 4,100,000  : Rs 346,000 + 30% of excess over 3,200,000
 *   > 4,100,000            : Rs 616,000 + 35% of excess over 4,100,000
 *
 * âââ NON-SALARIED individual â Division II, First Schedule âââââââââââââââââââââââââââââââ
 *   â¤ 600,000              : 0%
 *   600,001 â 1,200,000    : 15% of excess over 600,000
 *   1,200,001 â 2,400,000  : Rs 90,000 + 20% of excess over 1,200,000
 *   2,400,001 â 3,600,000  : Rs 330,000 + 25% of excess over 2,400,000
 *   3,600,001 â 6,000,000  : Rs 630,000 + 30% of excess over 3,600,000
 *   > 6,000,000            : Rs 1,350,000 + 35% of excess over 6,000,000
 *
 * âââ CAPITAL GAINS â Section 37/37A âââââââââââââââââââââââââââââââââââââââââââââââââââââ
 *   Immovable property (open plot, built-up): final tax at rate depending on holding period.
 *   Listed securities: 15% (< 1yr), 12.5% (1â2yr), 0% (> 2yr) â Section 37A(6).
 *
 * âââ FINAL TAXES âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
 *   Profit on debt (bank)  : 20% u/s 7B
 *   Profit on debt (NSS)   : 15% u/s 7B
 *   Dividend               : 15% u/s 5
 *   Surcharge u/s 4AB      : 9% of tax where taxable income > Rs 10M
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
        // Income breakdown
        val taxableSalary: Long,
        val businessIncome: Long,
        val propertyIncome: Long,
        val otherNormalIncome: Long,
        val normalIncome: Long,
        val taxableIncome: Long,
        val isSalariedCase: Boolean,
        // Normal tax
        val normalTaxGross: Long,
        val teacherReduction: Long,
        val surcharge: Long,
        val pensionCredit: Long,
        val donationCredit: Long,
        val normalTaxNet: Long,
        // Final taxes
        val finalTaxProfitOnDebt: Long,
        val finalTaxDividend: Long,
        val finalTaxCapitalGains: Long,
        val totalTaxChargeable: Long,
        // Taxes paid
        val totalTaxPaid: Long,
        val balance: Long,
        // Wealth
        val closingNetAssets: Long,
        val increaseInNetAssets: Long,
        val inflows: Long,
        val outflows: Long,
        val unreconciled: Long,
        val warnings: List<String>
    )

    fun salariedSlabTax(taxable: Long): Long {
        val t = taxable.toDouble()
        return when {
            taxable <= 600_000L      -> 0.0
            taxable <= 1_200_000L    -> 0.01 * (t - 600_000)
            taxable <= 2_200_000L    -> 6_000 + 0.11 * (t - 1_200_000)
            taxable <= 3_200_000L    -> 116_000 + 0.23 * (t - 2_200_000)
            taxable <= 4_100_000L    -> 346_000 + 0.30 * (t - 3_200_000)
            else                     -> 616_000 + 0.35 * (t - 4_100_000)
        }.roundToLong()
    }

    fun nonSalariedSlabTax(taxable: Long): Long {
        // Division II â verify against 2026 ordinance PDF
        val t = taxable.toDouble()
        return when {
            taxable <= 600_000L      -> 0.0
            taxable <= 1_200_000L    -> 0.15 * (t - 600_000)
            taxable <= 2_400_000L    -> 90_000 + 0.20 * (t - 1_200_000)
            taxable <= 3_600_000L    -> 330_000 + 0.25 * (t - 2_400_000)
            taxable <= 6_000_000L    -> 630_000 + 0.30 * (t - 3_600_000)
            else                     -> 1_350_000 + 0.35 * (t - 6_000_000)
        }.roundToLong()
    }

    /**
     * Capital gain rate on immovable property (final tax).
     * Open Plot: 0% after 6 yrs | Built: 0% after 5 yrs.
     * Verify exact breakpoints against Section 37A / First Schedule Part IV.
     */
    fun propertyCapitalGainRate(holdingYears: Int, propertyType: String): Double {
        return when (propertyType) {
            "Open Plot" -> when {
                holdingYears < 1  -> 0.15
                holdingYears < 2  -> 0.125
                holdingYears < 3  -> 0.10
                holdingYears < 4  -> 0.075
                holdingYears < 5  -> 0.05
                holdingYears < 6  -> 0.025
                else              -> 0.0
            }
            else -> when { // Built Residential or Built Commercial
                holdingYears < 1  -> 0.15
                holdingYears < 2  -> 0.125
                holdingYears < 3  -> 0.10
                holdingYears < 4  -> 0.075
                holdingYears < 5  -> 0.05
                else              -> 0.0
            }
        }
    }

    fun compute(d: ReturnData): Computation {
        val warnings = mutableListOf<String>()

        // ââ Salary ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        val taxableSalary = if (d.hasSalary) {
            val gross = if (d.salarySimpleMode) {
                d.grossSalary
            } else {
                d.basicSalary + d.houseRentAllowance + d.medicalAllowance +
                        d.conveyanceAllowance + d.otherAllowances +
                        d.bonusPerformancePay + d.perquisitesAndBenefits
            }
            max(0L, gross - d.exemptMedicalAllowance - d.exemptHouseRentAllowance - d.otherExemptSalary)
        } else 0L

        // ââ Business ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        val businessIncome = if (d.hasBusiness) d.businessNetIncome else 0L

        // ââ Property ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        val propertyIncome = if (d.hasProperty) d.propertyNetIncome else 0L

        // ââ Other sources (normal rates) ââââââââââââââââââââââââââââââââââââââ
        val otherNormalIncome = if (d.hasOtherSources) d.otherIncome + d.foreignIncome else 0L

        // ââ Normal regime income ââââââââââââââââââââââââââââââââââââââââââââââ
        val normalIncome = taxableSalary + businessIncome + propertyIncome + otherNormalIncome
        val taxableIncome = max(0L, normalIncome - d.zakat - d.workerWelfareFund)

        // ââ Salaried vs non-salaried ââââââââââââââââââââââââââââââââââââââââââ
        val isSalariedCase = taxableIncome == 0L ||
                taxableSalary.toDouble() > 0.75 * taxableIncome
        if (!isSalariedCase) {
            warnings.add(
                "Salary is not more than 75% of taxable income â non-salaried (higher) rates apply. " +
                "Rates have been computed using non-salaried slabs."
            )
        }

        // ââ Normal tax ââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        val normalTaxGross = if (isSalariedCase) salariedSlabTax(taxableIncome)
                             else nonSalariedSlabTax(taxableIncome)

        val teacherReduction = if (d.isTeacherOrResearcher && taxableIncome > 0 && taxableSalary > 0) {
            (0.25 * normalTaxGross * (taxableSalary.toDouble() / taxableIncome)).roundToLong()
        } else 0L

        val taxAfterReduction = max(0L, normalTaxGross - teacherReduction)

        val surcharge = if (taxableIncome > SURCHARGE_THRESHOLD)
            (SURCHARGE_RATE * taxAfterReduction).roundToLong() else 0L

        val avgRate = if (taxableIncome > 0) taxAfterReduction.toDouble() / taxableIncome else 0.0
        val eligiblePension = min(d.pensionFundContribution, (0.20 * taxableIncome).roundToLong())
        val pensionCredit = (eligiblePension * avgRate).roundToLong()
        val eligibleDonation = min(d.charitableDonations, (0.30 * taxableIncome).roundToLong())
        val donationCredit = (eligibleDonation * avgRate).roundToLong()

        val normalTaxNet = max(0L, taxAfterReduction + surcharge - pensionCredit - donationCredit)

        // ââ Final taxes âââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        val totalPod = d.profitOnDebtBank + d.profitOnDebtNss
        if (totalPod > POD_FINAL_LIMIT) {
            warnings.add(
                "Profit on debt exceeds Rs 5,000,000 â it falls outside the final tax regime u/s 7B " +
                "and is taxable at normal rates. Consult a tax adviser."
            )
        }
        val finalTaxProfitOnDebt = if (d.hasOtherSources)
            (d.profitOnDebtBank * POD_BANK_RATE + d.profitOnDebtNss * POD_NSS_RATE).roundToLong()
        else 0L

        val finalTaxDividend = if (d.hasOtherSources)
            (d.dividend * DIVIDEND_RATE).roundToLong() else 0L

        // Capital gains â final tax
        val propGainRate = propertyCapitalGainRate(d.propertyHoldingYears, d.propertyType)
        val finalTaxPropertyGain = if (d.hasCapitalGains)
            (d.propertyGainAmount * propGainRate).roundToLong() else 0L
        val finalTaxSecurities = if (d.hasCapitalGains)
            (d.securitiesGainLessThan1Yr * 0.15 + d.securitiesGain1To2Yr * 0.125).roundToLong()
        else 0L
        val finalTaxCapitalGains = finalTaxPropertyGain + finalTaxSecurities

        if (d.hasCapitalGains && d.securitiesGainAbove2Yr > 0) {
            warnings.add("Securities gains held >2 years: exempt from tax (Rs ${fmt(d.securitiesGainAbove2Yr)} not included in computation).")
        }
        if (d.hasCapitalGains && propGainRate == 0.0 && d.propertyGainAmount > 0) {
            warnings.add("Property held ${d.propertyHoldingYears}+ years: gain is exempt from tax (Rs ${fmt(d.propertyGainImount)} not included).")
        }

        val totalTaxChargeable = normalTaxNet + finalTaxProfitOnDebt + finalTaxDividend + finalTaxCapitalGains

        // ââ Taxes paid / withheld âââââââââââââââââââââââââââââââââââââââââââââ
        val totalTaxPaid = d.taxDeductedSalary + d.taxDeductedBusiness + d.taxDeductedRent +
                d.taxDeductedProfit + d.taxDeductedDividend + d.taxDeductedCapitalGains +
                d.taxPhone + d.taxElectricity + d.taxVehicle +
                d.taxPropertyPurchase + d.taxPropertySale + d.taxOtherAdjustable

        val balance = totalTaxChargeable - totalTaxPaid

        // ââ Wealth reconciliation âââââââââââââââââââââââââââââââââââââââââââââ
        val closingNetAssets = d.closingNetAssets
        val increase = closingNetAssets - d.openingNetAssets
        val inflows = taxableIncome + d.zakat + d.workerWelfareFund +
                d.exemptMedicalAllowance + d.exemptHouseRentAllowance + d.otherExemptSalary +
                totalPod + d.dividend + d.agriculturalIncome +
                d.foreignRemittance + d.giftsInheritanceOther
        val outflows = d.personalExpenses + totalTaxChargeable
        val unreconciled = increase - (inflows - outflows)
        if (unreconciled != 0L) {
            warnings.add(
                "Wealth statement is out of balance by Rs ${fmt(unreconciled)}. " +
                "Inflows minus outflows must equal the increase in net assets."
            )
        }

        return Computation(
            taxableSalary, businessIncome, propertyIncome, otherNormalIncome,
            normalIncome, taxableIncome, isSalariedCase,
            normalTaxGross, teacherReduction, surcharge, pensionCredit, donationCredit, normalTaxNet,
            finalTaxProfitOnDebt, finalTaxDividend, finalTaxCapitalGains,
            totalTaxChargeable, totalTaxPaid, balance,
            closingNetAssets, increase, inflows, outflows, unreconciled, warnings
        )
    }

    private fun fmt(v: Long): String = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(v)
}
