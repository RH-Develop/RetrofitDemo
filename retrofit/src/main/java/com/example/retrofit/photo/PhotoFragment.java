package com.example.retrofit.photo;

import android.os.IInterface;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.Toast;
import com.example.retrofit.adapter.PhotoAdapter;
import com.example.retrofit.model.PhotoArticleBean;
import com.example.retrofit.base.BaseListFragment;
import java.util.ArrayList;
import java.util.List;


/**
 * @author RH
 * @date 2018/3/5
 */
public class PhotoFragment extends BaseListFragment<IPhotoArticle.Presenter> implements IPhotoArticle.View {
    private static final String TAG = "PhotoFragment";
    public static PhotoFragment photoFragment;

    public static PhotoFragment getInstance() {
        if (photoFragment == null) {
            photoFragment = new PhotoFragment();
        }
        return photoFragment;
    }

    private PhotoAdapter photoAdapter;
    private List<PhotoArticleBean.Data> dataList = new ArrayList<>();

    @Override
    public void setPresenter(IPhotoArticle.Presenter presenter) {
        //将IPhotoArticle.View 传给对应的Presenter
        if (presenter == null) {
            this.presenter = new PhotoPresenter(this);
        }
    }


    @Override
    protected void initView(View view) {
        super.initView(view);
        photoAdapter = new PhotoAdapter(dataList , this);
        recyclerView.setAdapter(photoAdapter);
        //添加Android自带的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        presenter.loadData();
    }

    @Override
    public void onUpdateUI(List<PhotoArticleBean.Data> list) {
        //dataList.clear();
        //dataList.addAll(list);
        dataList.addAll(0,list);
        photoAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadingFinish() {
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void responsInfo(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        loadingFinish();
    }


    @Override
    public void onRefresh() {
        presenter.loadData();
    }


}
