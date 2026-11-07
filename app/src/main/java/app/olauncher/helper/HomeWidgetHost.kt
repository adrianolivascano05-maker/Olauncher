package app.olauncher.helper

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

/**
 * The one AppWidgetHost behind the home screen widget (for example Google Calendar's day view).
 * It lives as long as the process does: MainActivity starts and stops listening, the
 * HomeFragment builds the view, SettingsFragment picks and removes the widget.
 */
object HomeWidgetHost {

    const val HOST_ID = 1024
    const val NO_WIDGET = -1

    @Volatile
    private var host: AppWidgetHost? = null

    fun get(context: Context): AppWidgetHost =
        host ?: synchronized(this) {
            host ?: AppWidgetHost(context.applicationContext, HOST_ID).also { host = it }
        }

    fun providerInfo(context: Context, appWidgetId: Int): AppWidgetProviderInfo? {
        if (appWidgetId == NO_WIDGET) return null
        return try {
            AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId)
        } catch (e: Exception) {
            null
        }
    }

    /** Null when the widget's app is gone or the id was never bound; the caller then forgets the id. */
    fun createView(context: Context, appWidgetId: Int): AppWidgetHostView? {
        val info = providerInfo(context, appWidgetId) ?: return null
        return try {
            get(context).createView(context, appWidgetId, info)
        } catch (e: Exception) {
            null
        }
    }

    fun delete(context: Context, appWidgetId: Int) {
        if (appWidgetId == NO_WIDGET) return
        try {
            get(context).deleteAppWidgetId(appWidgetId)
        } catch (_: Exception) {
        }
    }
}
