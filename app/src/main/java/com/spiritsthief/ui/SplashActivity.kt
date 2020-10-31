package com.spiritsthief.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spiritsthief.R
import com.spiritsthief.common.UserPreferences
import com.spiritsthief.ui.ui.main.MainActivity2
import com.spiritsthief.ui.onboarding.OnBoarding


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (UserPreferences.isFirstTime) {
            startActivity(Intent(this, OnBoarding::class.java))
        } else {
            if (!handleIntent(intent)) {
                startActivity(Intent(this, MainActivity2::class.java))
            }
        }

        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent): Boolean {
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            val bottleId = appLinkData.lastPathSegment.toString()
            MainActivity2.startForBottleId(this, bottleId)
            finish()
            return true
        }

        return false
    }
}
