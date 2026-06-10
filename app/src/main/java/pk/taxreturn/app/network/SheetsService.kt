package pk.taxreturn.app.network

import pk.taxreturn.app.model.UserData
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Sends user registration / login events to a Google Apps Script web app,
 * which appends rows to an admin Google Sheet.
 *
 * âââ HOW TO SET UP ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
 * 1. Open https://script.google.com â New Project
 * 2. Paste the contents of google_apps_script/SheetLogger.gs
 * 3. Click Deploy â New deployment â Web app
 *    â¢ Execute as: Me
 *    â¢ Who has access: Anyone
 * 4. Copy the deployment URL and paste it below as SCRIPT_URL.
 * ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
 */
object SheetsService {

    // Replace with your Google Apps Script deployment URL after setup
    private const val SCRIPT_URL =
        "https://script.google.com/macros/s/REPLACE_WITH_YOUR_DEPLOYMENT_ID/exec"

    /**
     * Posts a new user registration row to the admin sheet.
     * Runs on a background thread â safe to call from any context.
     */
    fun registerUser(user: UserData, onDone: (success: Boolean) -> Unit) {
        Thread {
            val ok = postJson(
                """{"action":"register","cnic":"${user.cnic}","name":"${user.fullName}",""" +
                """"email":"${user.email}","phone":"${user.phone}","registeredAt":"${user.registeredAt}"}"""
            )
            onDone(ok)
        }.start()
    }

    /**
     * Logs a login event to the admin sheet.
     */
    fun logLogin(cnic: String, name: String, onDone: (success: Boolean) -> Unit = {}) {
        Thread {
            val ok = postJson("""{"action":"login","cnic":"$cnic","name":"$name"}""")
            onDone(ok)
        }.start()
    }

    private fun postJson(body: String): Boolean {
        return try {
            val url = URL(SCRIPT_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            OutputStreamWriter(conn.outputStream).use { it.write(body) }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        } catch (e: Exception) {
            false // Network errors silently ignored â app works offline
        }
    }
}
