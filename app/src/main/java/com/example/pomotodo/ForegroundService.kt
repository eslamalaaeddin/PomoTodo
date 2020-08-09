package com.example.pomotodo

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ui.MainActivity

private const val CHANNEL_ID = "5"
private const val Foreground_CHANNEL_ID = "14"
private  var NOTIFICATION_TITLE = "Focus!"
private  var FINISH_NOTIFICATION_TITLE = "Great!"
private  var CONTENT_TEXT = "You have finished a new Pomodoro!"
private const val TAG_STARTED = "ForegroundService"
private const val RUNNING_ID = "runningId"
private const val NUMBER_ID = "numberId"
var currentPomodoros = 0
private lateinit var wakeLock: PowerManager.WakeLock
class ForegroundService : Service() {

    private lateinit var builder: NotificationCompat.Builder
    val repo = TasksRepository.get()
    var number = 1
    var running = false
    val handler = Handler()
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createNotificationChannelForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        NotificationManagerCompat.from(this).cancel(159)
        //Her is the problem source
        number = intent?.getIntExtra(NUMBER_ID, 1500)!!
        ///tempNumber = intent.getIntExtra(NUMBER_ID,-1)!!
        running = intent.getBooleanExtra(RUNNING_ID, false)
        if (!running || number == 1) {
            stopForeground(true)
        } else if (running) {
            notifyMe()
            runTimer()

                     wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , "MyApp::MyWakelockTag").apply {
                    acquire()
                }
            }

        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    //Foreground Service
    private val runnable = object : Runnable {
        override fun run() {

            if (running) {
                number--
                Utils.finalNumber = number
                Utils.sbFinalNumber = number
                Utils.lbFinalNumber = number
                builder.setContentText(Utils.format(number))
                startForeground(1, builder.build())


            } else if (!running) {
                handler.removeCallbacks(this)
                stopForeground(true)
                stopSelf(1)
            }
            handler.postDelayed(this, Utils.DELAY)
            if (number == 0) {
                number = Utils.NUMBER
                handler.removeCallbacks(this)
                stopForeground(true)
                stopSelf(1)
                Utils.serviceEnd = true
                currentPomodoros =Utils.currentPomodoros+1
                createNotification()
                val task = Task(Utils.currentPmodoro, Utils.currentDescription ,1 ,
                    currentPomodoros,Utils.currentId)
                repo.updateTask(task)
                wakeLock.release()
            }
        }
    }

    private fun runTimer() {
        handler.post(runnable)
    }


    private fun notifyMe() {

        if (Utils.shortBreakNotification) {
            NOTIFICATION_TITLE = "Short break is running...Relax"
        }
        else if (Utils.longBreakNotification) {
            NOTIFICATION_TITLE = "Long break in running...Relax"
        }
        else{
            if (Utils.currentPmodoro.isEmpty()){
                NOTIFICATION_TITLE = "Pomodoro is running...Focus."
            }
            else{
                NOTIFICATION_TITLE = "${Utils.currentPmodoro} is running...Focus."

            }

        }

        builder = NotificationCompat.Builder(this, "14")
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(Utils.format(number))
            .setPriority(NotificationCompat.PRIORITY_LOW)

            .setAutoCancel(true)
        //3 Create the action
        val actionIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 5, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        startForeground(1, builder.build())
    }

    private fun createNotification() {

        if (Utils.shortBreakNotification) {
            FINISH_NOTIFICATION_TITLE = "Time's Up!"
            CONTENT_TEXT = "Get back to work."
        }
        else if (Utils.longBreakNotification) {
            FINISH_NOTIFICATION_TITLE = "Time's Up!"
            CONTENT_TEXT = "Get back to work."
        }
        else{
            FINISH_NOTIFICATION_TITLE = "Great!"

            if (Utils.currentPmodoro.isEmpty()){
                CONTENT_TEXT = "A new Pomodoro is completed... time to take a break."
            }
            else{
                CONTENT_TEXT = "${Utils.currentPmodoro} is finished...time to take a break."
            }

        }

        //2 Create the builder
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_success)
            .setContentTitle(FINISH_NOTIFICATION_TITLE)
            .setContentText(CONTENT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVibrate(longArrayOf(0, 1000))
            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +this.packageName +"/"+R.raw.sound7))
            .setAutoCancel(true)
        //3 Create the action
        val actionIntent = Intent(this, MainActivity::class.java)
        actionIntent.putExtra("NotificationMessage", 1)
        val pendingIntent =
            PendingIntent.getActivity(this, 3, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(159, builder.build())


        }
    }

    private fun createNotificationChannelForeground() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.foreground_channel_name)
            val descriptionText = getString(R.string.foreground_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(Foreground_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.after_finish_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




}
