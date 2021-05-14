package com.airbnb.epoxy.pagingsample

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.airbnb.epoxy.paging.PagedListEpoxyController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PagingSampleActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pagingController = TestController()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = pagingController.adapter

        val viewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)
        viewModel.pagedList.observe(
            this,
            Observer {
                pagingController.submitList(it)
            }
        )
    }
}

class TestController : PagedListEpoxyController<User>() {
    override fun buildItemModel(currentPosition: Int, item: User?): EpoxyModel<*> {
        return if (item == null) {
            PagingViewModel_()
                .id(-currentPosition)
                .name("loading $currentPosition")
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

class ActivityViewModel(app: Application) : AndroidViewModel(app) {
    val db by lazy {
        Room.inMemoryDatabaseBuilder(app, PagingDatabase::class.java).build()
    }
    val pagedList: LiveData<PagedList<User>> by lazy {
        LivePagedListBuilder<Int, User>(
            db.userDao().dataSource, 100
        ).build()
    }

    init {
        GlobalScope.launch {
            (1..3000).map {
                User(it)
            }.let {
                it.groupBy {
                    it.uid / 200
                }.forEach { group ->
                    withContext(Dispatchers.Default) {
                        delay(group.key.toLong())
                        db.userDao().insertAll(group.value)
                    }
                }
            }
        }
    }
}
