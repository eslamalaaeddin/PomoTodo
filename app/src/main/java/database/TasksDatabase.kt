package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pomotodo.Task

@Database(entities = [ Task::class ], version=3)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao
}

val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE task ADD COLUMN pomodoros INTEGER NOT NULL DEFAULT 0"
        )
    }
}