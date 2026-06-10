package pk.taxreturn.app.model

import org.json.JSONObject

/**
 * All data entered by the taxpayer for Tax Year 2026 (01 Jul 2025 â 30 Jun 2026).
 * Amounts are in whole Pakistani Rupees.
 */
data class ReturnData(

    // ââ Income source flags âââââââââââââââââââââââââââââââââââââââââââââââââââ
    var hasSalary: Boolean = false,
    var hasBusiness: Boolean = false,
    var hasProperty: Boolean = false,
    var hasCapitalGains: Boolean = false,
    var hasOtherSources: Boolean = false,

    // ââ Profile ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
    var name: String = "",
    var cnic: String = "",
    var isTeacherOrResearcher: Boolean = false,   // 25% reduction, 2nd Schedule Part III
    var isNonResident: Boolean = false,
    var ntn: String = "",

    // ââ SALARY â Section 12 âââââââââââââââââââââââââââââââââââââââââââââââââââ
    var salarySimpleMode: Boolean = true,
    // Simple
    var grossSalary: Long = 0,
    var taxDeductedSalary: Long = 0,              // u/s 149
    // Advanced (additional fields unlocked in advanced mode)
    var basicSalary: Long = 0,
    var houseRentAllowance: Long = 0,
    var medicalAllowance: Long = 0,
    var conveyanceAllowance: Long = 0,
    var otherAllowances: Long = 0,
    var bonusPerformancePay: Long = 0,
    var perquisitesAndBenefits: Long = 0,
    var exemptMedicalAllowance: Long = 0,         // exempt up to 10% of basic (Cl.139, 2nd Sch.)
    var exemptHouseRentAllowance: Long = 0,       // exempt portion
    var otherExemptSalary: Long = 0,

    // ââ BUSINESS INCOME â Sections 18-36B ââââââââââââââââââââââââââââââââââââ
    var businessType: String = "Sole Proprietor",  // Sole Proprietor | AOP Share | Partnership Share
    var businessName: String = "",
    var businessNature: String = "Trading",        // Trading | Services | Manufacturing | Construction
    // P&L
    var businessRevenue: Long = 0,
    var businessCostOfSales: Long = 0,             // direct expenses / COGS
    var businessGrossProfit: Long = 0,             // auto = revenue - cost of sales (not stored, computed)
    var businessAdminExpenses: Long = 0,           // indirect: salaries, rent, utilities
    var businessFinanceCharges: Long = 0,          // bank charges, interest on loans
    var businessOtherIndirectExp: Long = 0,        // marketing, depreciation not claimed under s.22
    var businessDepreciation: Long = 0,            // u/s 22 normal depreciation
    var businessInitialAllowance: Long = 0,        // u/s 23 (25% on new plant/machinery)
    var businessPrecommencement: Long = 0,         // u/s 25
    var businessOtherDeductions: Long = 0,         // any other allowable deduction
    var taxDeductedBusiness: Long = 0,             // WHT on business receipts (e.g. s.153)

    // ââ PROPERTY INCOME â Sections 15/15A ââââââââââââââââââââââââââââââââââââ
    var rentReceived: Long = 0,
    var propertyTax: Long = 0,                     // s.15A(a)
    var propertyInsurance: Long = 0,               // s.15A(b)
    var propertyMaintenance: Long = 0,             // 1/5 of rent u/s 15A(c) â auto or manual
    var propertyInterestOnLoan: Long = 0,          // s.15A(d)
    var propertyOtherDeductions: Long = 0,
    var taxDeductedRent: Long = 0,                 // u/s 155

    // ââ CAPITAL GAINS âââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
    // Immovable property â Section 37 / 37A(8) (final tax)
    var propertyGainAmount: Long = 0,
    var propertyHoldingYears: Int = 0,             // full years held (determines rate)
    var propertyType: String = "Open Plot",        // Open Plot | Built Residential | Built Commercial
    // Listed securities â Section 37A(6) (final tax)
    var securitiesGainLessThan1Yr: Long = 0,       // 15% final
    var securitiesGain1To2Yr: Long = 0,            // 12.5% final
    var securitiesGainAbove2Yr: Long = 0,          // 0% (exempt)
    var taxDeductedCapitalGains: Long = 0,         // u/s 236C (property) or u/s 233 (securities)

    // ââ OTHER SOURCES â Section 39 / final taxes âââââââââââââââââââââââââââââ
    var profitOnDebtBank: Long = 0,               // u/s 7B @ 20% (bank/savings)
    var profitOnDebtNss: Long = 0,                // u/s 7B @ 15% (NSS/Govt securities)
    var taxDeductedProfit: Long = 0,              // u/s 151
    var dividend: Long = 0,                       // u/s 5 @ 15%
    var taxDeductedDividend: Long = 0,            // u/s 150
    var foreignIncome: Long = 0,                  // taxed at normal rates if remitted
    var agriculturalIncome: Long = 0,             // s.41 â agricultural income (exempt from federal)
    var otherIncome: Long = 0,                    // Section 39 catch-all

    // ââ DEDUCTIBLE ALLOWANCES & TAX CREDITS ââââââââââââââââââââââââââââââââââ
    var zakat: Long = 0,                          // s.60
    var pensionFundContribution: Long = 0,        // s.63 VPS â credit capped at 20% of TI
    var charitableDonations: Long = 0,            // s.61 â credit capped at 30% of TI
    var workerWelfareFund: Long = 0,              // s.60A

    // ââ OTHER ADJUSTABLE WITHHOLDING TAXES ââââââââââââââââââââââââââââââââââââ
    var taxPhone: Long = 0,                       // u/s 236
    var taxElectricity: Long = 0,                 // u/s 235
    var taxVehicle: Long = 0,                     // u/s 234 / 231B
    var taxPropertyPurchase: Long = 0,            // u/s 236K
    var taxPropertySale: Long = 0,                // u/s 236C
    var taxOtherAdjustable: Long = 0,

    // ââ WEALTH STATEMENT â Section 116 ââââââââââââââââââââââââââââââââââââââââ
    var openingNetAssets: Long = 0,
    var assetProperty: Long = 0,
    var assetVehicles: Long = 0,
    var assetBankBalances: Long = 0,
    var assetCash: Long = 0,
    var assetInvestments: Long = 0,
    var assetBusiness: Long = 0,                  // capital invested in business
    var assetOther: Long = 0,
    var liabilities: Long = 0,
    var personalExpenses: Long = 0,
    var foreignRemittance: Long = 0,              // s.111(4)
    var giftsInheritanceOther: Long = 0

) {
    val totalAssets: Long
        get() = assetProperty + assetVehicles + assetBankBalances +
                assetCash + assetInvestments + assetBusiness + assetOther

    val closingNetAssets: Long
        get() = totalAssets - liabilities

    // Computed business gross profit (for display)
    val businessGrossProfitCalc: Long
        get() = businessRevenue - businessCostOfSales

    // Net business income (before tax deductions/allowances)
    val businessNetIncome: Long
        get() = maxOf(0L, businessRevenue - businessCostOfSales -
                businessAdminExpenses - businessFinanceCharges -
                businessOtherIndirectExp - businessDepreciation -
                businessInitialAllowance - businessPrecommencement - businessOtherDeductions)

    // Net property income
    val propertyNetIncome: Long
        get() = maxOf(0L, rentReceived - propertyTax - propertyInsurance -
                propertyMaintenance - propertyInterestOnLoan - propertyOtherDeductions)

    fun toJson(): String {
        val o = JSONObject()
        o.put("hasSalary", hasSalary)
        o.put("hasBusiness", hasBusiness)
        o.put("hasProperty", hasProperty)
        o.put("hasCapitalGains", hasCapitalGains)
        o.put("hasOtherSources", hasOtherSources)
        o.put("name", name); o.put("cnic", cnic); o.put("ntn", ntn)
        o.put("isTeacherOrResearcher", isTeacherOrResearcher)
        o.put("isNonResident", isNonResident)
        o.put("salarySimpleMode", salarySimpleMode)
        o.put("grossSalary", grossSalary)
        o.put("taxDeductedSalary", taxDeductedSalary)
        o.put("basicSalary", basicSalary)
        o.put("houseRentAllowance", houseRentAllowance)
        o.put("medicalAllowance", medicalAllowance)
        o.put("conveyanceAllowance", conveyanceAllowance)
        o.put("otherAllowances", otherAllowances)
        o.put("bonusPerformancePay", bonusPerformancePay)
        o.put("perquisitesAndBenefits", perquisitesAndBenefits)
        o.put("exemptMedicalAllowance", exemptMedicalAllowance)
        o.put("exemptHouseRentAllowance", exemptHouseRentAllowance)
        o.put("otherExemptSalary", otherExemptSalary)
        o.put("businessType", businessType); o.put("businessName", businessName)
        o.put("businessNature", businessNature)
        o.put("businessRevenue", businessRevenue)
        o.put("businessCostOfSales", businessCostOfSales)
        o.put("businessAdminExpenses", businessAdminExpenses)
        o.put("businessFinanceCharges", businessFinanceCharges)
        o.put("businessOtherIndirectExp", businessOtherIndirectExp)
        o.put("businessDepreciation", businessDepreciation)
        o.put("businessInitialAllowance", businessInitialAllowance)
        o.put("businessPrecommencement", businessPrecommencement)
        o.put("businessOtherDeductions", businessOtherDeductions)
        o.put("taxDeductedBusiness", taxDeductedBusiness)
        o.put("rentReceived", rentReceived)
        o.put("propertyTax", propertyTax)
        o.put("propertyInsurance", propertyInsurance)
        o.put("propertyMaintenance", propertyMaintenance)
        o.put("propertyInterestOnLoan", propertyInterestOnLoan)
        o.put("propertyOtherDeductions", propertyOtherDeductions)
        o.put("taxDeductedRent", taxDeductedRent)
        o.put("propertyGainAmount", propertyGainAmount)
        o.put("propertyHoldingYears", propertyHoldingYears)
        o.put("propertyType", propertyType)
        o.put("securitiesGainLessThan1Yr", securitiesGainLessThan1Yr)
        o.put("securitiesGain1To2Yr", securitiesGain1To2Yr)
        o.put("securitiesGainAbove2Yr", securitiesGainAbove2Yr)
        o.put("taxDeductedCapitalGains", taxDeductedCapitalGains)
        o.put("profitOnDebtBank", profitOnDebtBank)
        o.put("profitOnDebtNss", profitOnDebtNss)
        o.put("taxDeductedProfit", taxDeductedProfit)
        o.put("dividend", dividend)
        o.put("taxDeductedDividend", taxDeductedDividend)
        o.put("foreignIncome", foreignIncome)
        o.put("agriculturalIncome", agriculturalIncome)
        o.put("otherIncome", otherIncome)
        o.put("zakat", zakat)
        o.put("pensionFundContribution", pensionFundContribution)
        o.put("charitableDonations", charitableDonations)
        o.put("workerWelfareFund", workerWelfareFund)
        o.put("taxPhone", taxPhone); o.put("taxElectricity", taxElectricity)
        o.put("taxVehicle", taxVehicle)
        o.put("taxPropertyPurchase", taxPropertyPurchase)
        o.put("taxPropertySale", taxPropertySale)
        o.put("taxOtherAdjustable", taxOtherAdjustable)
        o.put("openingNetAssets", openingNetAssets)
        o.put("assetProperty", assetProperty); o.put("assetVehicles", assetVehicles)
        o.put("assetBankBalances", assetBankBalances); o.put("assetCash", assetCash)
        o.put("assetInvestments", assetInvestments); o.put("assetBusiness", assetBusiness)
        o.put("assetOther", assetOther); o.put("liabilities", liabilities)
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
                    hasSalary = o.optBoolean("hasSalary"),
                    hasBusiness = o.optBoolean("hasBusiness"),
                    hasProperty = o.optBoolean("hasProperty"),
                    hasCapitalGains = o.optBoolean("hasCapitalGains"),
                    hasOtherSources = o.optBoolean("hasOtherSources"),
                    name = o.optString("name"),
                    cnic = o.optString("cnic"),
                    ntn = o.optString("ntn"),
                    isTeacherOrResearcher = o.optBoolean("isTeacherOrResearcher"),
                    isNonResident = o.optBoolean("isNonResident"),
                    salarySimpleMode = o.optBoolean("salarySimpleMode", true),
                    grossSalary = o.optLong("grossSalary"),
                    taxDeductedSalary = o.optLong("taxDeductedSalary"),
                    basicSalary = o.optLong("basicSalary"),
                    houseRentAllowance = o.optLong("houseRentAllowance"),
                    medicalAllowance = o.optLong("medicalAllowance"),
                    conveyanceAllowance = o.optLong("conveyanceAllowance"),
                    otherAllowances = o.optLong("otherAllowances"),
                    bonusPerformancePay = o.optLong("bonusPerformancePay"),
                    perquisitesAndBenefits = o.optLong("perquisitesAndBenefits"),
                    exemptMedicalAllowance = o.optLong("exemptMedicalAllowance"),
                    exemptHouseRentAllowance = o.optLong("exempHHouseRentAllowance"),
                    otherExemptSalary = o.optLong("otherExemptSalary"),
                    businessType = o.optString("businessType", "Sole Proprietor"),
                    businessName = o.optString("businessName"),
                    businessNature = o.optString("businessNature", "Trading"),
                    businessRevenue = o.optLong("businessRevenue"),
                    businessCostOfSales = o.optLong("businessCostOfSales"),
                    businessAdminExpenses = o.optLong("businessAdminExpenses"),
                    businessFinanceCharges = o.optLong("businessFinanceCharges"),
                    businessOtherIndirectExp = o.optLong("businessOtherIndirectExp"),
                    businessDepreciation = o.optLong("businessDepreciation"),
                    businessInitialAllowance = o.optLong("businessInitialAllowance"),
                    businessPrecommencement = o.optLong("businessPrecommencement"),
                    businessOtherDeductions = o.optLong("businessOtherDeductions"),
                    taxDeductedBusiness = o.optLong("taxDeductedBusiness"),
                    rentReceived = o.optLong("rentReceived"),
                    propertyTax = o.optLong("propertyTax"),
                    propertyInsurance = o.optLong("propertyInsurance"),
                    propertyMaintenance = o.optLong("propertyMaintenance"),
                    propertyInterestOnLoan = o.optLong("propertyInterestOnLoan"),
                    propertyOtherDeductions = o.optLong("propertyOtherDeductions"),
                    taxDeductedRent = o.optLong("taxDeductedRent"),
                    propertyGainAmount = o.optLong("propertyGainAmount"),
                    propertyHoldingYears = o.optInt("propertyHoldingYears"),
                    propertyType = o.optString("propertyType", "Open Plot"),
                    securitiesGainLessThan1Yr = o.optLong("securitiesGainLessThan1Yr"),
                    securitiesGain1To2Yr = o.optLong("securitiesGain1To2Yr"),
                    securitiesGainAbove2Yr = o.optLong("securitiesGainAbove2Yr"),
                    taxDeductedCapitalGains = o.optLong("taxDeductedCapitalGains"),
                    profitOnDebtBank = o.optLong("profitOnDebtBank"),
                    profitOnDebtNss = o.optLong("profitOnDebtNss"),
                    taxDeductedProfit = o.optLong("taxDeductedProfit"),
                    dividend = o.optLong("dividend"),
                    taxDeductedDividend = o.optLong("taxDeductedDividend"),
                    foreignIncome = o.optLong("foreignIncome"),
                    agriculturalIncome = o.optLong("agriculturalIncome"),
                    otherIncome = o.optLong("otherIncome"),
                    zakat = o.optLong("zakat"),
                    pensionFundContribution = o.optLong("pensionFundContribution"),
                    charitableDonations = o.optLong("charitableDonations"),
                    workerWelfareFund = o.optLong("workerWelfareFund"),
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
                    assetBusiness = o.optLong("assetBusiness"),
                    assetOther = o.optLong("assetOther"),
                    liabilities = o.optLong("liabilities"),
                    personalExpenses = o.optLong("personalExpenses"),
                    foreignRemittance = o.optLong("foreignRemittance"),
                    giftsInheritanceOther = o.optLong("giftsInheritanceOther")
                )
            } catch (e: Exception) { ReturnData() }
        }
    }
}
