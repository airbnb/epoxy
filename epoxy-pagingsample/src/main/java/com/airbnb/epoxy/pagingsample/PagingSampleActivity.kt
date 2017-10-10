package com.airbnb.epoxy.pagingsample

import android.arch.paging.*
import android.content.*
import android.os.*
import android.support.v7.app.*
import android.support.v7.widget.*
import android.widget.*
import com.airbnb.epoxy.*
import com.airbnb.epoxy.paging.*
import android.arch.persistence.room.*;
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.*
import org.jetbrains.anko.coroutines.experimental.*
import java.util.concurrent.*

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

                return@bg PagedList.Builder<Int, User>().run {
                    setDataSource(db.userDao().dataSource)
                    setMainThreadExecutor(UiThreadExecutor)
                    setBackgroundThreadExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    setConfig(PagedList.Config.Builder().run {
                        setEnablePlaceholders(false)
                        setPageSize(40)
                        setInitialLoadSizeHint(80)
                        setPrefetchDistance(50)
                        build()
                    })
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
        println("build ${users.size}")

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
