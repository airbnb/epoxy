package com.airbnb.epoxy;

import com.airbnb.epoxy.integrationtest.BuildConfig;
import com.airbnb.epoxy.integrationtest.Model_;
import com.airbnb.epoxy.paging.PagingEpoxyController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import androidx.paging.PagedList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class PagingIntegrationTest_PagedList {
  private Controller controller;

  private static class Controller extends PagingEpoxyController<Integer> {

    public void setPagedListWithSize(int size) {
      PagedList pagedList = mock(PagedList.class);
      when(pagedList.size()).thenReturn(size);

      ArrayList<Integer> list = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        list.add(i + 1);
      }
      when(pagedList.snapshot()).thenReturn(list);

      setList(pagedList);
    }

    @Override
    protected void buildModels(List<Integer> list) {
      for (Integer position : list) {
        add(new Model_().id(position));
      }
    }
  }

  @Before
  public void setup() {
    controller = new Controller();
  }

  @Test
  public void initialPageBind() {
    controller.setConfig(
        new PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(100)
            .setInitialLoadSizeHint(100)
            .build()
    );
    controller.setPagedListWithSize(500);

    List<EpoxyModel<?>> models = controller.getAdapter().getCopyOfModels();
    assertEquals(100, models.size());

    assertEquals(1, models.get(0).id());
  }
}
