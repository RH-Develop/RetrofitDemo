package com.example.retrofit.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.example.retrofit.R;

/**
 * @author RH
 * @date 2018/3/5
 */
public abstract class BaseListFragment<T extends BasePresenter> extends BaseFragment<T> implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "BaseListFragment";
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;

    @Override
    protected int attachLayoutId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
    }




}
