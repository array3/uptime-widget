package com.example.uptimewidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import java.util.concurrent.TimeUnit

class UptimeWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            // SystemClock.elapsedRealtime() mierzy czas od uruchomienia (w tym w stanie uśpienia)
            val uptimeMs = SystemClock.elapsedRealtime()
            val days = TimeUnit.MILLISECONDS.toDays(uptimeMs)
            val hours = TimeUnit.MILLISECONDS.toHours(uptimeMs) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(uptimeMs) % 60

            val uptimeText = "Uptime: ${days}d ${hours}h ${minutes}m"

            val views = RemoteViews(context.packageName, R.layout.widget_uptime).apply {
                setTextViewText(R.id.tv_uptime, uptimeText)

                // Ręczne odświeżenie widgetu po dotknięciu
                val intent = Intent(context, UptimeWidget::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}