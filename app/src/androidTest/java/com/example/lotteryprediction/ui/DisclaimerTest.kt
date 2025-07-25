package com.example.lotteryprediction.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lotteryprediction.R
import com.example.lotteryprediction.ui.main.MainActivity
import com.example.lotteryprediction.ui.result.PredictionResultActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisclaimerTest {

    @Test
    fun testFirstLaunchShowsDisclaimer() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withText(R.string.disclaimer_title))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testResultPageShowsDisclaimer() {
        ActivityScenario.launch(PredictionResultActivity::class.java).use {
            onView(withText(R.string.disclaimer))
                .check(matches(isDisplayed()))
        }
    }
}
