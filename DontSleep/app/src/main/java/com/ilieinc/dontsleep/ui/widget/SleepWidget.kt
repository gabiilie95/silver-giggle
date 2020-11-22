package com.ilieinc.dontsleep.ui.widget

import android.appwidget.AppWidgetProvider
import android.content.Context

class SleepWidget : AppWidgetProvider() {
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

//    override fun onUpdate(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetIds: IntArray
//    ) {
//        // Perform this loop procedure for each App Widget that belongs to this provider
//        appWidgetIds.forEach { appWidgetId ->
//            // Create an Intent to launch ExampleActivity
//            val pendingIntent: PendingIntent = Intent(context, ExampleActivity::class.java)
//                .let { intent ->
//                    PendingIntent.getActivity(context, 0, intent, 0)
//                }
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            val views: RemoteViews = RemoteViews(
//                context.packageName,
//                R.layout.appwidget_provider_layout
//            ).apply {
//                setOnClickPendingIntent(R.id.button, pendingIntent)
//            }
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
//    }

}