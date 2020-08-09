package com.example.pomotodo

import android.content.Context
import android.preference.PreferenceManager

private const val POMODORO_DURATION = "POMODORO_DURATION"
private const val POMODORO_DURATION_POSITION = "POMODORO_DURATION_POSITION"
private const val SHORT_BREAK_DURATION = "SHORT_BREAK_DURATION"
private const val SHORT_BREAK_DURATION_POSITION = "SHORT_BREAK_DURATION_POSITION"
private const val LONG_BREAK_DURATION = "LONG_BREAK_DURATION"
private const val LONG_BREAK_DURATION_POSITION = "LONG_BREAK_DURATION_POSITION"
private const val ANIMATION_STATE = "ANIMATION_STATE"

object Preferences {

    //Pomodoror duration

    fun getPomodoroDuration(context: Context?): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(POMODORO_DURATION, 1500)
    }

    fun getPomodoroDurationPosition(context: Context?, duration:Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        when(duration/60){
            15 -> return 0
            20 -> return 1
            25 -> return 2
            30 -> return 3
            35 -> return 4
            40 -> return 5
            45 -> return 6
        }
        return prefs.getInt(POMODORO_DURATION_POSITION, 1500)
    }

    fun setPomodoroDuration(context: Context?, duration: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(POMODORO_DURATION, duration)
            .apply()
    }

    //Short break duration

    fun getShortBreakDuration(context: Context?): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(SHORT_BREAK_DURATION, 300)
    }

    fun getShortBreakDurationPosition(context: Context?, duration:Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        when(duration/60){
            3 -> return 0
            4 -> return 1
            5 -> return 2

        }
        return prefs.getInt(SHORT_BREAK_DURATION_POSITION, 300)
    }

    fun setShortBreakDuration(context: Context?, duration: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(SHORT_BREAK_DURATION_POSITION, duration)
            .apply()
    }

    //Long break duration

    fun getLongBreakDuration(context: Context?): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(LONG_BREAK_DURATION, 600)
    }

    fun getLongBreakDurationPosition(context: Context?, duration:Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        when(duration/60){
            10 -> return 0
            15 -> return 1
            20 -> return 2
            25 -> return 3

        }
        return prefs.getInt(LONG_BREAK_DURATION_POSITION, 600)
    }

    fun setLongBreakDuration(context: Context?, duration: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(LONG_BREAK_DURATION_POSITION, duration)
            .apply()
    }

    //Animation
    fun getAnimationState (context: Context?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean(ANIMATION_STATE,true)
    }

    fun setAnimationState (context: Context?, state: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(ANIMATION_STATE, state)
            .apply()
    }
}