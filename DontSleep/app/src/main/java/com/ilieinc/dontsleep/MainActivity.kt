package com.ilieinc.dontsleep

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ilieinc.dontsleep.ui.main.MainFragment
import com.ilieinc.dontsleep.util.DeviceAdminHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        DeviceAdminHelper.init(applicationContext)
    }
}
