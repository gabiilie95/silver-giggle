package com.ilieinc.dontsleep

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ilieinc.dontsleep.ui.main.MainFragment
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        setSupportActionBar(action_bar)
        DeviceAdminHelper.init(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.apply {
            menuInflater.inflate(R.menu.main_activity_menu, this)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_rate -> {
            //TODO("Not Implemented")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
