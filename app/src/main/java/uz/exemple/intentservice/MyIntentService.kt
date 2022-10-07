package uz.exemple.intentservice

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast

class MyIntentService :IntentService("my_intentService") {
    private val TAG = "IntentServiceLogs"
    companion object{
        val ACTION_MYINTENTSERVICE = "RESPONSE"
        val EXTRA_KEY_OUT = "EXTRA_OUT"
        var extraOut = "Moshina zavad bo'ldi, qizdirildi haydashga tayyor!"

        val ACTION_UPDATE = "UPDATE"
        val EXTRA_KEY_UPDATE = "EXTRA_UPDATE"
        private val NOTIFICATION_ID = 1
    }
    private var mNotificationManager: NotificationManager? = null

    private var mIsSuccess = false
    private var mIsStopped = false


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        val notice: String
        mIsStopped = true
        notice = if (mIsSuccess) {
            "onDestroy with success"
        } else {
            "onDestroy WITHOUT success!"
        }
        Toast.makeText(applicationContext, notice, Toast.LENGTH_LONG)
            .show()
        val responseIntent = Intent()
        responseIntent.action = MyIntentService.ACTION_MYINTENTSERVICE
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        responseIntent.putExtra(MyIntentService.EXTRA_KEY_OUT, extraOut)
        sendBroadcast(responseIntent)
        super.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {

        val tm = intent!!.getIntExtra("time", 0)
        val label = intent.getStringExtra("task")

        Toast.makeText(applicationContext, label,Toast.LENGTH_LONG).show()
        Log.d(TAG, "onHandleIntent start: $label")
        // sending result
        val responseIntent = Intent()
        responseIntent.action = MyIntentService.ACTION_MYINTENTSERVICE
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        responseIntent.putExtra(MyIntentService.EXTRA_KEY_OUT, label)
        sendBroadcast(responseIntent)
        for (i in 0..10) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (mIsStopped) {
                break
            }
            // sending update
            val updateIntent = Intent()
            updateIntent.action = MyIntentService.ACTION_UPDATE
            updateIntent.addCategory(Intent.CATEGORY_DEFAULT)
            updateIntent.putExtra(MyIntentService.EXTRA_KEY_UPDATE, i)
            sendBroadcast(updateIntent)
            mIsSuccess = true
            // notification
            val notificationText = (100 * i / 10).toString() + " %"
            val notification = Notification.Builder(
                applicationContext
            )
                .setContentTitle("Progress")
                .setContentText(notificationText)
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                .build()
            mNotificationManager!!.notify(MyIntentService.NOTIFICATION_ID, notification)
        }
        Log.d(TAG, "onHandleIntent end: $label")
    }




}