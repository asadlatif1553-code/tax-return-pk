# Tax Return PK — Android App (Tax Year 2026)

An Android app for **salaried individuals in Pakistan** to prepare their income tax return and wealth statement for **Tax Year 2026** (1 July 2025 – 30 June 2026), based on the Income Tax Ordinance 2001 as amended by the **Finance Act 2025**.

The app computes your full tax position and produces an **IRIS-ready summary** — you then enter the figures on [iris.fbr.gov.pk](https://iris.fbr.gov.pk) to file (FBR provides no public filing API; apps like Befiler also file on IRIS on your behalf rather than through an API).

## Features

- **Profile** — name, CNIC, teacher/researcher status (25% tax reduction)
- **Income** — salary u/s 12 (with exempt medical allowance), property u/s 15/15A, profit on debt u/s 7B (bank 20% / NSS 15%), dividend u/s 5 (15%), other sources
- **Deductions & credits** — Zakat u/s 60, pension fund (VPS) credit u/s 63 (20% cap), donations u/s 61 (30% cap), all common adjustable withholding taxes (236, 235, 234/231B, 236K, 236C)
- **Wealth statement u/s 116** — assets, liabilities, expenses, foreign remittance, with automatic reconciliation and out-of-balance warning
- **Summary** — full tax computation (TY2026 salaried slabs, 9% surcharge u/s 4AB above Rs 10m), payable/refundable, share as text, one-tap "Open IRIS"
- Data saved locally on the phone only (SharedPreferences); nothing is transmitted anywhere

## TY2026 salaried slabs implemented

| Taxable income (Rs) | Tax |
|---|---|
| up to 600,000 | 0 |
| 600,001 – 1,200,000 | 1% of excess over 600,000 |
| 1,200,001 – 2,200,000 | 6,000 + 11% over 1,200,000 |
| 2,200,001 – 3,200,000 | 116,000 + 23% over 2,200,000 |
| 3,200,001 – 4,100,000 | 346,000 + 30% over 3,200,000 |
| above 4,100,000 | 616,000 + 35% over 4,100,000 |

Surcharge u/s 4AB: 9% where taxable income exceeds Rs 10,000,000.

## How to build the APK

1. Install **Android Studio** (Ladybug or newer) — https://developer.android.com/studio
2. Open Android Studio → **Open** → select the `TaxReturnApp` folder
3. Let Gradle sync finish (first sync downloads dependencies; if prompted about a missing Gradle wrapper, accept Studio's offer to create/use one — Gradle 8.9+)
4. **Build → Build App Bundle(s)/APK(s) → Build APK(s)**
5. APK is at `app/build/outputs/apk/debug/app-debug.apk` — copy to your phone and install (allow "install unknown apps")

To run directly on your phone: enable USB debugging, connect, press **Run ▶**.

## Disclaimer

This app is a preparation/computation aid, not a filing service and not tax advice. Verify all figures in IRIS before submission. Edge cases (profit on debt above Rs 5m, salary below 75% of taxable income, foreign income, capital gains) are flagged with warnings and may need adviser input.
