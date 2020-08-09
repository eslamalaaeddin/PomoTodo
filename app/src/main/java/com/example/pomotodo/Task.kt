package com.example.pomotodo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity

data class Task (val title: String,
                 val description:String,
                 val isFinished:Int = 0 ,
                 val pomodoros:Int =0,
                 @PrimaryKey(autoGenerate = true) val id:Int = 0 )