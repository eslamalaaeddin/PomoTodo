package com.example.pomotodo

import java.util.*

object Utils {
    const val DELAY = 1000L
    var NUMBER = 0
    var STATE = false
    var PRESS_STATE = false
    var minutes = 0
    var seconds = 0
    var formattedNumber = ""
    var serviceEnd = false
    var alerted = false
    var stop = true
    var shortBreakEnd = false
    var longBreakEnd = false

    var shortBreakNotification = false
    var longBreakNotification = false

    var currentPmodoro = ""
    var currentId = -1
    var currentDescription = ""
    var currentPomodoros = -1

    var finalNumber = -1
    var sbFinalNumber = -1
    var lbFinalNumber = -1
    fun format(number:Int?) : String{
      number?.let {
          minutes = (number%3600)/60
          seconds = number%60

      }
        formattedNumber = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )

        return formattedNumber
    }

    fun getTime() : Int{
        return NUMBER
    }


}