package com.ilieinc.dontsleep.ui.widget

import android.app.Activity
import android.os.Bundle
import com.ilieinc.dontsleep.R


class SleepWidgetActivity : Activity() {
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED)
        // Set the view layout resource to use.
        setContentView(R.layout.sleep_appwidget)
        // Find the EditText
//        sleep_button.setOnClickListener {
//            Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show()
//        }
    }
}