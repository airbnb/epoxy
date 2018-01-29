package com.airbnb.epoxy.pagingsample

import android.arch.paging.PagedList
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.paging.PagingEpoxyController
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
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
                for (i in 1..3000) {
                    db.userDao().insertAll(User(i))
                }

                return@bg PagedList.Builder<Int, User>(
                        db.userDao().dataSource.create(),
                        PagedList.Config.Builder().run {
                            setEnablePlaceholders(false)
                            setPageSize(40)
                            setInitialLoadSizeHint(80)
                            setPrefetchDistance(50)
                            build()
                        }).run {
                    setMainThreadExecutor(UiThreadExecutor)
                    setBackgroundThreadExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    build()
                }
            }

            pagingController.setList(pagedList.await())
        }

    }
}

class TestController : PagingEpoxyController<User>() {
    init {
        setDebugLoggingEnabled(true)
    }

    override fun buildModels(users: List<User>) {
        users.forEach {
            pagingView {
                id(it.uid)
                name("Id: ${it.uid}")
            }
        }
    }

}

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class PagingView(context: Context) : TextView(context) {

    @TextProp
    fun name(name: CharSequence) {
        text = name
    }

}

object UiThreadExecutor : Executor {
    private val mHandler = Handler(Looper.getMainLooper())

    override fun execute(command: Runnable) {
        mHandler.post(command)
    }
}
