/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.lightingstar.appmonitor.fragment.task;

import android.widget.AbsListView;

import com.lightingstar.appmonitor.MyApp;
import com.lightingstar.appmonitor.R;
import com.lightingstar.appmonitor.adapter.TaskListViewListAdapter;
import com.lightingstar.appmonitor.core.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;

/**
 * 首页任务
 *
 * @author lightingstar
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none)
public class TaskFragment extends BaseFragment {

    @BindView(R.id.listView)
    AbsListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private TaskListViewListAdapter mAdapter;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        mAdapter = new TaskListViewListAdapter();
        //WidgetUtils.initRecyclerView(recyclerView, 0);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void initListeners() {
//下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.refresh(MyApp.queryAppInfoTask.getAppInfos());
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mAdapter.loadMore(MyApp.queryAppInfoTask.getAppInfos());
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }
}
