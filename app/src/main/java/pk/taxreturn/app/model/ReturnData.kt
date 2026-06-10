package pk.taxreturn.app.model

import org.json.JSONObject

/**
 * All data entered by the taxpayer for one return (Tax Year 2026: 01-Jul-2025 to 30-Jun-2026).
 * Amounts are in whole Pakistani Rupees.
 */
data class ReturnData(
    // ---- Personal ----
    var name: String = "",
    var cnic: String = "",
    var isTeacherOrResearcher: Boolean = false,   // 25% tax reduction, Part III, Second Schedule

    // ---- Salary (Section 12) ----
    var grossSalary: Long = 0,                    // total taxable salary per salary certificate
    var exemptMedicalAllowance: Long = 0,         // exempt up to 10% of basic salary (Cl. 139, Part I, 2nd Sch.)
    var otherExemptSalary: Long = 0,              // any other exempt component
    var taxDeductedSalary: Long = 0,              // tax withheld by employer u/s 149

    // ---- Property income (Sections 15/15A) ----
    var rentReceived: Long = 0,
    var propertyDeductions: Long = 0,             // admissible deductions u/s 15A
    var taxDeductedRent: Long = 0,                // withheld u/s 155

    // ---- Other sources / final tax ----
    var profitOnDebtBank: Long = 0,               // bank/saving account profit (u/s 7B @ 20%)
    var profitOnDebtNss: Long = 0,                // National Savings / govt securities (15%)
    var taxDeductedProfit: Long = 0,              // withheld u/s 151
    var dividend: Long = 0,                       // u/s 5 (standard 15%)
    var taxDeductedDividend: Long = 0,            // withheld u/s 150
    var otherIncome: Long = 0,                    // any other income taxed at normal rates (Section 39)

    // ---- Deductible allowances & tax credits ----
    var zakat: Long = 0,                          // Section 60 (paid under Zakat & Ushr Ordinance 1980)
    var pensionFundContribution: Long = 0,        // Section 63 (VPS) - credit capped at 20% of taxable income
    var charitableDonations: Long = 0,            // Section 61 - credit capped at 30% of taxable income

    // ---- Other adjustable withholding taxes ----
    var taxPhone: Long = 0,                       // u/s 236(1)(a) telephone/internet
    var taxElectricity: Long = 0,                 // u/s 235 electricity (domestic)
    var taxVehicle: Long = 0,                     // u/s 234 vehicle token / 231B purchase
    var taxPropertyPurchase: Long = 0,            // u/s 236K
    var taxPropertySale: Long = 0,                // u/s 236C
    var taxOtherAdjustable: Long = 0,

    // ---- Wealth statement (Section 116) ----
    var openingNetAssets: Long = 0,               // net assets as at 30-Jun-2025 (last year's closing)
    var assetProperty: Long = 0,
    var assetVehicles: Long = 0,
    var assetBankBalances: Long = 0,
    var assetCash: Long = 0,
    var assetInvestments: Long = 0,
    var assetOther: Long = 0,
    var liabilities: Long = 0,
    var personalExpenses: Long = 0,               // total household/personal expenditure for the year
    var foreignRemittance: Long = 0,              // exempt inflow u/s 111(4)
    var giftsInheritanceOther: Long = 0           // other non-income inflows
) {
    val totalAssets: Long
        get() = assetProperty + assetVehicles + assetBankBalances +
                assetCash + assetInvestments + assetOther

    val closingNetAssets: Long
        get() = totalAssets - liabilities

    fun toJson(): String {
        val o = JSONObject()
        o.put("name", name); o.put("cnic", cnic)
        o.put("isTeacherOrResearcher", isTeacherOrResearcher)
        o.put("grossSalary", grossSalary)
        o.put("exemptMedicalAllowance", exemptMedicalAllowance)
        o.put("otherExemptSalary", otherExemptSalary)
        o.put("taxDeductedSalary", taxDeductedSalary)
        o.put("rentReceived", rentReceived)
        o.put("propertyDeductions", propertyDeductions)
        o.put("taxDeductedRent", taxDeductedRent)
        o.put("profitOnDebtBank", profitOnDebtBank)
        o.put("profitOnDebtNss", profitOnDebtNss)
        o.put("taxDeductedProfit", taxDeductedProfit)
        o.put("dividend", dividend)
        o.put("taxDeductedDividend", taxDeductedDividend)
        o.put("otherIncome", otherIncome)
        o.put("zakat", zakat)
        o.put("pensionFundContribution", pensionFundContribution)
        o.put("charitableDonations", charitableDonations)
        o.put("taxPhone", taxPhone)
        o.put("taxElectricity", taxElectricity)
        o.put("taxVehicle", taxVehicle)
        o.put("taxPropertyPurchase", taxPropertyPurchase)
        o.put("taxPropertySale", taxPropertySale)
        o.put("taxOtherAdjustable", taxOtherAdjustable)
        o.put("openingNetAssets", openingNetAssets)
        o.put("assetProperty", assetProperty)
        o.put("assetVehicles", assetVehicles)
        o.put("assetBankBalances", assetBankBalances)
        o.put("assetCash", assetCash)
        o.put("assetInvestments", assetInvestments)
        o.put("assetOther", assetOther)
        o.put("liabilities", liabilities)
        o.put("personalExpenses", personalExpenses)
        o.put("foreignRemittance", foreignRemittance)
        o.put("giftsInheritanceOther", giftsInheritanceOther)
        return o.toString()
    }

    companion object {
        fun fromJson(s: String?): ReturnData {
            if (s.isNullOrBlank()) return ReturnData()
            return try {
                val o = JSONObject(s)
                ReturnData(
                    name = o.optString("name"),
                    cnic = o.optString("cnic"),
                    isTeacherOrResearcher = o.optBoolean("isTeacherOrResearcher"),
                    grossSalary = o.optLong("grossSalary"),
                    exemptMedicalAllowance = o.optLong("exemptMedicalAllowance"),
                    otherExemptSalary = o.optLong("otherExemptSalary"),
                    taxDeductedSalary = o.optLong("taxDeductedSalary"),
                    rentReceived = o.optLong("rentReceived"),
                    propertyDeductions = o.optLong("propertyDeductions"),
                    taxDeductedRent = o.optLong("taxDeductedRent"),
                    profitOnDebtBank = o.optLong("profitOnDebtBank"),
                    profitOnDebtNss = o.optLong("profitOnDebtNss"),
                    taxDeductedProfit = o.optLong("taxDeductedProfit"),
                    dividend = o.optLong("dividend"),
                    taxDeductedDividend = o.optLong("taxDeductedDividend"),
                    otherIncome = o.optLong("otherIncome"),
                    zakat = o.optLong("zakat"),
                    pensionFundContribution = o.optLong("pensionFundContribution"),
                    charitableDonations = o.optLong("charitableDonations"),
                    taxPhone = o.optLong("taxPhone"),
                    taxElectricity = o.optLong("taxElectricity"),
                    taxVehicle = o.optLong("taxVehicle"),
                    taxPropertyPurchase = o.optLong("taxPropertyPurchase"),
                    taxPropertySale = o.optLong("taxPropertySale"),
                    taxOtherAdjustable = o.optLong("taxOtherAdjustable"),
                    openingNetAssets = o.optLong("openingNetAssets"),
                    assetProperty = o.optLong("assetProperty"),
                    assetVehicles = o.optLong("assetVehicles"),
                    assetBankBalances = o.optLong("assetBankBalances"),
                    assetCash = o.optLong("assetCash"),
                    assetInvestments = o.optLong("assetInvestments"),
                    assetOther = o.optLong("assetOther"),
                    liabilities = o.optLong("liabilities"),
                    personalExpenses = o.optLong("personalExpenses"),
                    foreignRemittance = o.optLong("foreignRemittance"),
                    giftsInheritanceOther = o.optLong("giftsInheritanceOther")
                )
            } catch (e: Exception) {
                ReturnData()
            }
        }
    }
}
