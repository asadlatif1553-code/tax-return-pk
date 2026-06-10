package pk.taxreturn.app.model

import android.content.Context
import org.json.JSONObject

data class UserData(
    val cnic: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val passwordHash: String = "",
    val registeredAt: String = ""
) {
    fun toJson(): String = JSONObject().apply {
        put("cnic", cnic)
        put("fullName", fullName)
        put("email", email)
        put("phone", phone)
        put("passwordHash", passwordHash)
        put("registeredAt", registeredAt)
    }.toString()

    companion object {
        fun fromJson(s: String?): UserData? {
            if (s.isNullOrBlank()) return null
            return try {
                val o = JSONObject(s)
                UserData(
                    cnic = o.optString("cnic"),
                    fullName = o.optString("fullName"),
                    email = o.optString("email"),
                    phone = o.optString("phone"),
                    passwordHash = o.optString("passwordHash"),
                    registeredAt = o.optString("registeredAt")
                )
            } catch (e: Exception) { null }
        }

        fun load(ctx: Context): UserData? {
            val prefs = ctx.getSharedPreferences("tax_user", Context.MODE_PRIVATE)
            return fromJson(prefs.getString("user", null))
        }

        fun save(ctx: Context, user: UserData) {
            ctx.getSharedPreferences("tax_user", Context.MODE_PRIVATE)
                .edit().putString("user", user.toJson()).apply()
        }

        fun clear(ctx: Context) {
            ctx.getSharedPreferences("tax_user", Context.MODE_PRIVATE)
                .edit().remove("user").apply()
        }

        /** Simple deterministic hash â NOT cryptographically secure, for local storage only */
        fun hashPassword(raw: String): String {
            var h = 5381L
            for (c in raw) h = (h shl 5) + h + c.code
            return h.toString(16)
        }
    }
}
