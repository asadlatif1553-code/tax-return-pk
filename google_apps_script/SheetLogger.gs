/**
 * Tax Return PK â Admin Google Sheet Logger
 * Deploy this as a Google Apps Script web app to receive user events from the Android app.
 *
 * SETUP INSTRUCTIONS:
 * 1. Open https://script.google.com â New Project
 * 2. Paste this entire file into the editor
 * 3. Edit SHEET_ID below: open your Google Sheet, copy the ID from the URL
 *    (the long string between /d/ and /edit)
 * 4. Click Deploy â New deployment â Web app
 *    â¢ Execute as: Me
 *    â¢ Who has access: Anyone
 * 5. Authorize the script when prompted
 * 6. Copy the deployment URL and paste it into SheetsService.kt in the Android project
 */

const SHEET_ID = "REPLACE_WITH_YOUR_GOOGLE_SHEET_ID";

function doPost(e) {
  try {
    const data = JSON.parse(e.postData.contents);
    const ss = SpreadsheetApp.openById(SHEET_ID);
    const now = new Date().toISOString();

    if (data.action === "register") {
      let sheet = ss.getSheetByName("Registrations");
      if (!sheet) {
        sheet = ss.insertSheet("Registrations");
        sheet.appendRow(["Timestamp", "CNIC", "Full Name", "Email", "Phone", "Registered At"]);
        sheet.getRange(1, 1, 1, 6).setFontWeight("bold").setBackground("#1B5E20").setFontColor("#FFFFFF");
      }
      sheet.appendRow([now, data.cnic, data.name, data.email, data.phone, data.registeredAt]);
    }

    if (data.action === "login") {
      let sheet = ss.getSheetByName("Logins");
      if (!sheet) {
        sheet = ss.insertSheet("Logins");
        sheet.appendRow(["Timestamp", "CNIC", "Name"]);
        sheet.getRange(1, 1, 1, 3).setFontWeight("bold").setBackground("#1B5E20").setFontColor("#FFFFFF");
      }
      sheet.appendRow([now, data.cnic, data.name]);
    }

    return ContentService
      .createTextOutput(JSON.stringify({ status: "ok" }))
      .setMimeType(ContentService.MimeType.JSON);

  } catch (err) {
    return ContentService
      .createTextOutput(JSON.stringify({ status: "error", message: err.toString() }))
      .setMimeType(ContentService.MimeType.JSON);
  }
}

// Test from the Apps Script editor: run testPost() to verify sheet access
function testPost() {
  const fakeEvent = { postData: { contents: JSON.stringify({
    action: "register",
    cnic: "1234567890123",
    name: "Test User",
    email: "test@example.com",
    phone: "03001234567",
    registeredAt: new Date().toISOString()
  }) } };
  doPost(fakeEvent);
  Logger.log("Done â check your Google Sheet for a new row in Registrations");
}
