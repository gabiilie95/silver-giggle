package com.ilieinc.dontsleep

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ilieinc.dontsleep.ui.compose.MainScreen
import com.ilieinc.dontsleep.ui.theme.AppTheme
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.StateHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen()
            }
        }
        DeviceAdminHelper.init(applicationContext)
        StateHelper.requestRatingIfNeeded(this)
    }

}
