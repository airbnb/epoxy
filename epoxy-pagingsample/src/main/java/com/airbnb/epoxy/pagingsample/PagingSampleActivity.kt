package com.airbnb.epoxy.pagingsample

import android.arch.paging.PagedList
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.paging.PagingEpoxyController
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.lang.RuntimeException
import java.util.concurrent.Executor

class PagingSampleActivity : AppCompatActivity() {

    lateinit var db: PagingDatabase

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Room.databaseBuilder(applicationContext,
                                  PagingDatabase::class.java,
                                  "database-name").build()

        val pagingController = TestController()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pagingController.adapter

        async(UI) {
            val pagedList = bg {
                db.userDao().delete(db.userDao().all)
                (1..3000)
                        .map { User(it) }
                        .let { db.userDao().insertAll(*it.toTypedArray()) }

                PagedList.Builder<Int, User>(
                        db.userDao().dataSource.create(),
                        PagedList.Config.Builder().run {
                            setEnablePlaceholders(false)
                            setPageSize(150)
                            setPrefetchDistance(30)
                            build()
                        }).run {
                    setNotifyExecutor(UiThreadExecutor)
                    setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    build()
                }
            }

            pagingController.setList(pagedList.await())
        }

        pagingController.setList(emptyList())
    }
}

class TestController : PagingEpoxyController<User>() {
    init {
        isDebugLoggingEnabled = true
    }

    override fun buildModels(users: List<User>) {
        pagingView {
            id("header")
            name("Header")
        }

        users.forEach {
            pagingView {
                id(it.uid)
                name("Id: ${it.uid}")
            }
        }
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        throw exception
    }

}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class PagingView(context: Context) : AppCompatTextView(context) {

    @TextProp
    fun name(name: CharSequence) {
        text = name
    }

}

object UiThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        handler.post(command)
    }
}
