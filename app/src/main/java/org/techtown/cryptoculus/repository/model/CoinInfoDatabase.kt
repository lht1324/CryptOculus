package org.techtown.cryptoculus.repository.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CoinInfo::class],
    version = 1
)
abstract class CoinInfoDatabase : RoomDatabase() {
    abstract fun coinInfoDao(): CoinInfoDao

    companion object {
        @Volatile
        private var INSTANCE: CoinInfoDatabase? = null

        fun getInstance(context: Context) : CoinInfoDatabase? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CoinInfoDatabase::class.java,
                        "coininfo_database")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}