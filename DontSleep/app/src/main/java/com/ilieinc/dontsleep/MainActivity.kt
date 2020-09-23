package com.ilieinc.dontsleep

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.ilieinc.dontsleep.ui.main.MainFragment
import com.ilieinc.dontsleep.util.DeviceAdminHelper
import com.ilieinc.dontsleep.util.Logger
import com.ilieinc.dontsleep.util.StateHelper
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
            StateHelper.createDialog(
                this,
                "Rate this app",
                "If you enjoy this app please feel free to rate it on the Google Play " +
                        "Store, it would help me out a lot :)\n" +
                        "For any feedback or suggestions either leave a review, " +
                        "or contact me directly at gabiilie95@gmail.com",
                "Rate App",
                "Dismiss"
            ) {
                val manager = ReviewManagerFactory.create(this)
                manager.requestReviewFlow().apply {
                    addOnCompleteListener { request ->
                        if (request.isSuccessful) {
                            val reviewInfo = request.result
                            val flow = manager.launchReviewFlow(this@MainActivity, reviewInfo)
                            flow.addOnCompleteListener { _ ->
                                Logger.info("Review finished")
                            }
                        } else {
                            Logger.info("Unable to show review")
                        }
                    }
                }
            }.show()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
