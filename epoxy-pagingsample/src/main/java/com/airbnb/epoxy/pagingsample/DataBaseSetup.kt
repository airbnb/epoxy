package com.airbnb.epoxy.pagingsample

import androidx.paging.DataSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = arrayOf(User::class), version = 1)
abstract class PagingDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Entity
data class User(
    @PrimaryKey
    var uid: Int,

    @ColumnInfo(name = "first_name")
    var firstName: String = "first name $uid",

    @ColumnInfo(name = "last_name")
    var lastName: String = "last name $uid"
)

@Dao
interface UserDao {
    @get:Query("SELECT * FROM user")
    val dataSource: DataSource.Factory<Int, User>

    @get:Query("SELECT * FROM user")
    val all: List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)

    @Delete
    fun delete(users: List<User>)
}
