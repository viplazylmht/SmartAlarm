package com.viplazy.smartalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var listAlarm = mutableListOf<Work>()
    lateinit var adapter : ListAdapter
    private var mBackPressed: Long = 0

    companion object {
        const val CYCLE_TIME = 5400 // 90 min
        const val ALARM_MESSAGE = "Set by SmartAlarm"
        const val REQUEST_SET_ALARM = 100

        const val TIME_INTERVAL = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ListAdapter(baseContext, listAlarm)
        list_view.adapter = adapter

        list_view.setOnItemClickListener { _, _, position, _ ->
            setAlarm(adapter.getItem(position))
        }

        fab.setOnClickListener { view ->
            calcTime(Calendar.getInstance().time)

            showSnackBar("We have calculated time for you!", parent = container)
        }

        fab_dismiss.setOnClickListener { view ->

            showAlarm()
        }
    }

    private fun calcTime(date: Date) {
        val timestamp = date.time / 1000

        listAlarm.clear()

        for (i in 3..6) {
            val next = timestamp + i * CYCLE_TIME
            // random asleep (between 15 and 20 min)
            val random = Random(Calendar.getInstance().timeInMillis)

            listAlarm.add(Work(i, next + random.nextInt(15, 20) * 60))
            Thread.sleep(20)
        }

        adapter.notifyDataSetChanged()
    }

    private fun setAlarm(work: Work) {

        val calendar = Calendar.getInstance()
        calendar.time = Date(work.timestamp * 1000)

        val (hour, min) = listOf(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        val intent = Intent(AlarmClock.ACTION_SET_ALARM)
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
        intent.putExtra(AlarmClock.EXTRA_MINUTES, min)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, ALARM_MESSAGE)
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)

        startActivityForResult(intent, REQUEST_SET_ALARM)
    }

    private fun showAlarm() {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, ALARM_MESSAGE)

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SET_ALARM) {
            if (resultCode == Activity.RESULT_OK) {
                showSnackBar("Alarm is set!", parent = container)
            }
        }
    }

    fun showSnackBar(
        message: String = "Hello Kotlin!",
        duration: Int = Snackbar.LENGTH_LONG,
        parent: View?
    ) {

        if (parent != null) {
            Snackbar.make(parent, message, duration).show()
        }
    }

    override fun onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()

            finish()
            return
        } else {
            Toast.makeText(baseContext, "Tap back button in order to exit", Toast.LENGTH_SHORT)
                .show()
        }
        mBackPressed = System.currentTimeMillis()
    }
}
