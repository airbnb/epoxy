package com.airbnb.epoxy.pagingsample

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.arch.persistence.room.Room
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.paging.PagedListEpoxyController
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.coroutines.experimental.bg
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class PagingSampleActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pagingController = TestController()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pagingController.adapter

        val viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        viewModel.pagedList.observe(this, Observer {
          pagingController.submitList(it)
        })
    }
}

class TestController : PagedListEpoxyController<User>(
    modelBuildingHandler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {
    override fun buildItemModel(currentPosition: Int, item: User?): EpoxyModel<*> {
        return if (item == null) {
            PagingViewModel_()
                .id(-currentPosition)
                .name("loading ${currentPosition}")
        } else {
            PagingViewModel_()
                .id(item.uid)
                .name("${item.uid}: ${item.firstName} / ${item.lastName}")
        }
    }

    override fun addModels(models: List<EpoxyModel<*>>) {
        pagingView {
            id("header")
            name("showing ${models.size} items")
        }
        super.addModels(models)
    }

    init {
        isDebugLoggingEnabled = true
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

class ActivityViewModel(app : Application) : AndroidViewModel(app) {
    val db by lazy {
        Room.inMemoryDatabaseBuilder(app, PagingDatabase::class.java).build()
    }
    val pagedList : LiveData<PagedList<User>> by lazy {
        LivePagedListBuilder<Int, User>(
            db.userDao().dataSource, 100
        ).build()
    }
    init {
        bg {
            (1..3000).map {
                User(it)
            }.let {
                it.groupBy {
                    it.uid % 20
                }.forEach { group ->
                    launch(CommonPool) {
                        delay(group.key.toLong(), TimeUnit.SECONDS)
                        db.userDao().insertAll(group.value)
                    }
                }
            }
        }
    }
}
