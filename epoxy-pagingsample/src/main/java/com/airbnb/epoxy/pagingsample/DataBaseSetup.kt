package com.airbnb.epoxy.pagingsample

import android.arch.paging.*
import android.arch.persistence.room.*

@Database(entities = arrayOf(User::class), version = 1)
abstract class PagingDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Entity
data class User(
        @PrimaryKey
        var uid: Int,

        @ColumnInfo(name = "first_name")
        var firstName: String = "first name",

        @ColumnInfo(name = "last_name")
        var lastName: String = "last name"
)

@Dao
interface UserDao {
    @get:Query("SELECT * FROM user")
    val dataSource: DataSource.Factory<Int, User>

    @get:Query("SELECT * FROM user")
    val all: List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: User)

    @Delete
    fun delete(users: List<User>)
}