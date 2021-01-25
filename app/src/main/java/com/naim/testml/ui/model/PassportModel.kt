package com.naim.testml

import com.naim.testml.PassportView
import com.naim.testml.ui.Constant
import java.util.regex.Matcher
import java.util.regex.Pattern

class PassportModel(private val mainPresenterView: PassportView.PresenterView) :
    PassportView.ModelView {
    override fun retrievePassportInfo(passportData: String) {
        try {
            var regexNID = "(^|\\s)([0-9]{17}|[0-9]{13}|[0-9]{10})($|\\s)"
            var regexPassport = "(^|\\s)[A-Z]{1,3}[0-9]{6,8}($|\\s)"
            var regexExpiry = "(expiry)(\\s{0,100})[0-9]{2}\\s[A-Z]{3}\\s[0-9]{4}"
            var regexBirth = "(birth)(\\s{0,100})[0-9]{2}\\s[A-Z]{3}\\s[0-9]{4}"
            var regexIssue = "(issue)(\\s{0,100})[0-9]{2}\\s[A-Z]{3}\\s[0-9]{4}"
            var regexGender = "(^|\\s)(M|F)(\\s|$)"
            var regexDIP = "(^|\\s)DIP(|\\s).(|\\s)\\w+(\\s|\$)"
            var data = ""
            var dataType = ""
            var hasValue = false

            val pPassport: Pattern = Pattern.compile(regexPassport)
            val mPassport: Matcher = pPassport.matcher(passportData)
            if (mPassport.find()) {
                data = mPassport.group(0)
                dataType = Constant.PASSPORT_TAG
                hasValue = true
            }

            val pNID: Pattern = Pattern.compile(regexNID)
            val mNID: Matcher = pNID.matcher(passportData)
            if (mNID.find()) {
                data = mNID.group(0)
                dataType = Constant.NID_TAG
                hasValue = true
            }

            val p: Pattern = Pattern.compile(regexBirth)
            val m: Matcher = p.matcher(passportData)
            if (m.find()) {
                data = mNID.group(0).toLowerCase().replace("birth", "").trim()
                dataType = Constant.BIRTH_DATE
                hasValue = true
            }

            val pExpiry: Pattern = Pattern.compile(regexExpiry)
            val mExpiry: Matcher = pExpiry.matcher(passportData)
            if (mExpiry.find()) {
                data = mExpiry.group(0).toLowerCase().replace("expiry", "").trim()
                dataType = Constant.EXPIRY_DATE
                hasValue = true
            }

            val pIssue: Pattern = Pattern.compile(regexIssue)
            val mIssue: Matcher = pIssue.matcher(passportData)
            if (mIssue.find()) {
                data = mIssue.group(0).toLowerCase().replace("issue", "").trim()
                dataType = Constant.ISSUE_DATE
                hasValue = true
            }

            val pGender: Pattern = Pattern.compile(regexGender)
            val mGender: Matcher = pGender.matcher(passportData)
            if (mGender.find()) {
                data = mGender.group(0).trim()
                dataType = Constant.GENDER
                hasValue = true
            }

            val pDIP: Pattern = Pattern.compile(regexDIP)
            val mDIP: Matcher = pDIP.matcher(passportData)
            if (mDIP.find()) {
                data = mDIP.group(0).trim()
                dataType = Constant.DIP
                hasValue = true
            }
            if (hasValue) {
                mainPresenterView.getEachPassportData(data, dataType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    //Work on your model here
}