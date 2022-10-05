package uz.exemple.intentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "Int"
    private lateinit var mInfoTextView: TextView
    private lateinit var mProgressBar: ProgressBar

    private lateinit var mMyBroadcastReceiver: MyBroadcastReceiver
    private lateinit var mUpdateBroadcastReceiver: UpdateBroadcastReceiver

    private var mMyServiceIntent: Intent? = null
    private var mNumberOfIntentService = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    fun initViews() {
        mInfoTextView = findViewById<TextView>(R.id.textView)
        mProgressBar = findViewById<View>(R.id.progressbar) as ProgressBar
        val startButton = findViewById<View>(R.id.buttonStart)
        val stopButton = findViewById<View>(R.id.buttonStop)
        startButton.setOnClickListener {
            mNumberOfIntentService++

            // Запускаем свой IntentService
            mMyServiceIntent = Intent(this@MainActivity, MyIntentService::class.java)
            startService(mMyServiceIntent!!.putExtra("time", 5).putExtra("task","Moshinani zavad bo'lyapti"))
            startService(mMyServiceIntent!!.putExtra("time", 10).putExtra("task","Moshinani qizdirilyapti"))
        }
        stopButton.setOnClickListener {
            if (mMyServiceIntent != null) {
                stopService(mMyServiceIntent)
                mMyServiceIntent = null
            }
        }
        mNumberOfIntentService = 0
        mMyBroadcastReceiver = MyBroadcastReceiver(mInfoTextView)
        mUpdateBroadcastReceiver = UpdateBroadcastReceiver(mProgressBar)

        // регистрируем BroadcastReceiver
        val intentFilter = IntentFilter(
            MyIntentService.ACTION_MYINTENTSERVICE
        )
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(mMyBroadcastReceiver, intentFilter)

        // Регистрируем второй приёмник
        val updateIntentFilter = IntentFilter(
            MyIntentService.ACTION_UPDATE
        )
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(mUpdateBroadcastReceiver, updateIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMyBroadcastReceiver)
        unregisterReceiver(mUpdateBroadcastReceiver)
    }

    class MyBroadcastReceiver(var mInfoTextView:TextView) : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
            val result = intent.getStringExtra(MyIntentService.EXTRA_KEY_OUT)

            mInfoTextView.text = result
        }

    }

    class UpdateBroadcastReceiver(var mProgressBar:ProgressBar) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val update = intent
                .getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0)
            mProgressBar.setProgress(update)
        }
    }
}