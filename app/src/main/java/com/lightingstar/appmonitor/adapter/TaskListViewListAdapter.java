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

package com.lightingstar.appmonitor.adapter;

import androidx.annotation.NonNull;

import com.lightingstar.appmonitor.R;
import com.lightingstar.appmonitor.model.TaskInfo;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.recyclerview.XRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页动态新闻【只是用于演示效果】
 *
 * @author XUE
 * @since 2019/5/9 10:41
 */
public class TaskListViewListAdapter extends BaseRecyclerAdapter<TaskInfo> {
    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    protected int getItemLayoutId(int viewType) {

        return R.layout.adapter_item_simple_list_2;
    }

    @Override
    public int getItemViewType(int position) {
      return 1;
    }

    public XRecyclerAdapter refresh(List<TaskInfo> data) {
        List<TaskInfo> list = new ArrayList<>(data);
        //用于占位
        list.add(0, new TaskInfo());
        return super.refresh(list);
    }

    @Override
    public void bindData(@NonNull RecyclerViewHolder holder, int position, TaskInfo model) {
        if (model == null) {
            return;
        }

        holder.text(R.id.tv_title, model.getTaskName());
        holder.text(R.id.tv_sub_title, model.getScore());
    }

}
