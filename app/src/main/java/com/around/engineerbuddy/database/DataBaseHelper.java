package com.around.engineerbuddy.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {EOEngineerBookingInfo.class}, version = 1, exportSchema = false)
public abstract class DataBaseHelper extends RoomDatabase {
    public abstract EngineerBookingDao engineerBookingInfoDao();

}
