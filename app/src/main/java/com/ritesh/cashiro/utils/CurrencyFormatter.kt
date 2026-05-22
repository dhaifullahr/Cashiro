package com.ritesh.cashiro.utils

import com.ritesh.cashiro.data.currency.model.CurrencySymbols
import com.ritesh.parser.core.bank.BankParserFactory
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Utility class for formatting currency values
 */
object CurrencyFormatter {

    private val INDIAN_LOCALE = Locale("en", "IN")

    /**
     * Locale mapping for different currencies
     */
    private val CURRENCY_LOCALES = mapOf(
        "INR" to INDIAN_LOCALE,
        "USD" to Locale.US,
        "EUR" to Locale.GERMANY,
        "GBP" to Locale.UK,
        "AED" to Locale.Builder().setLanguage("en").setRegion("AE").build(),
        "SGD" to Locale.Builder().setLanguage("en").setRegion("SG").build(),
        "CAD" to Locale.CANADA,
        "AUD" to Locale.Builder().setLanguage("en").setRegion("AU").build(),
        "JPY" to Locale.JAPAN,
        "CNY" to Locale.CHINA,
        "NPR" to Locale.Builder().setLanguage("ne").setRegion("NP").build(),
        "ETB" to Locale.Builder().setLanguage("am").setRegion("ET").build(),
        "THB" to Locale.Builder().setLanguage("th").setRegion("TH").build(),
        "MYR" to Locale.Builder().setLanguage("ms").setRegion("MY").build(),
        "KWD" to Locale.Builder().setLanguage("en").setRegion("KW").build(),
        "KRW" to Locale.KOREA,
        "SEK" to Locale.Builder().setLanguage("sv").setRegion("SE").build(),
        "CHF" to Locale.Builder().setLanguage("de").setRegion("CH").build(),
        "NZD" to Locale.Builder().setLanguage("en").setRegion("NZ").build(),
        "MXN" to Locale.Builder().setLanguage("es").setRegion("MX").build(),
        "TRY" to Locale("tr", "TR"),
        "RUB" to Locale("ru", "RU"),
        "ZAR" to Locale("en", "ZA"),
        "BRL" to Locale("pt", "BR"),
        "PLN" to Locale("pl", "PL"),
        "NOK" to Locale("nb", "NO"),
        "DKK" to Locale("da", "DK"),
        "CZK" to Locale("cs", "CZ"),
        "HUF" to Locale("hu", "HU"),
        "ILS" to Locale("he", "IL"),
        "PHP" to Locale("en", "PH"),

        // FIXED
        "IDR" to Locale("id", "ID"),

        "SAR" to Locale("ar", "SA"),
        "COP" to Locale("es", "CO"),
        "KES" to Locale("sw", "KE")
    )

    private val DEFAULT_LOCALE = Locale.US

    /**
     * Formats a BigDecimal amount as currency with the specified currency code
     */
    fun formatCurrency(amount: BigDecimal, currencyCode: String = "INR"): String {
        return try {
            val locale = CURRENCY_LOCALES[currencyCode] ?: DEFAULT_LOCALE
            val formatter = NumberFormat.getCurrencyInstance(locale)

            // Get custom symbol
            val customSymbol = CurrencySymbols.getSymbol(currencyCode)

            formatter.minimumFractionDigits = 0
            formatter.maximumFractionDigits = 2

            try {
                formatter.currency = Currency.getInstance(currencyCode)
            } catch (e: Exception) {
                return "$customSymbol${formatAmount(amount, currencyCode)}"
            }

            val formatted = formatter.format(amount)

            if (formatted.contains(currencyCode) || !formatted.contains(customSymbol)) {
                val cleanAmount = formatAmount(amount, currencyCode)

                return if (
                    locale == Locale.US ||
                    locale == Locale.UK ||
                    locale == INDIAN_LOCALE
                ) {
                    "$customSymbol$cleanAmount"
                } else {
                    "$cleanAmount $customSymbol"
                }
            }

            formatted
        } catch (e: Exception) {
            val symbol = CurrencySymbols.getSymbol(currencyCode)
            "$symbol${formatAmount(amount, currencyCode)}"
        }
    }

    /**
     * Formats a Double amount as currency
     */
    fun formatCurrency(amount: Double, currencyCode: String = "INR"): String {
        return formatCurrency(amount.toBigDecimal(), currencyCode)
    }

    /**
     * Formats amount with standard international grouping
     * Example:
     * 278000 -> 278,000.00
     */
    fun formatAmount(amount: BigDecimal, currencyCode: String = "INR"): String {

        val locale = CURRENCY_LOCALES[currencyCode] ?: DEFAULT_LOCALE

        // FIXED FORMAT
        val pattern = "#,###.00"

        val symbols = DecimalFormatSymbols(locale)
        val formatter = DecimalFormat(pattern, symbols)

        return formatter.format(amount)
    }

    /**
     * Formats double amount
     */
    fun formatAmount(amount: Double, currencyCode: String = "INR"): String {
        return formatAmount(amount.toBigDecimal(), currencyCode)
    }

    /**
     * Get currency symbol
     */
    fun getCurrencySymbol(currencyCode: String): String {
        return CurrencySymbols.getSymbol(currencyCode)
    }

    /**
     * Gets bank base currency
     */
    fun getBankBaseCurrency(bankName: String?): String {
        if (bankName == null) return "INR"

        return try {
            val parser = BankParserFactory.getParser(bankName)
            parser?.getCurrency() ?: "INR"
        } catch (e: Exception) {
            "INR"
        }
    }
}