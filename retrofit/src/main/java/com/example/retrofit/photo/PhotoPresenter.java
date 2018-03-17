package com.example.retrofit.photo;

import android.support.annotation.NonNull;
import com.example.retrofit.MyApplication;
import com.example.retrofit.network.RetrofitFactory;
import com.example.retrofit.network.api.RetrofitService;
import com.example.retrofit.model.PhotoArticleBean;
import com.example.retrofit.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author RH
 * @date 2018/3/5
 */
public class PhotoPresenter implements IPhotoArticle.Presenter {
    private static final String TAG = "PhotoPresenter";
    private final String RETRY = "retry";
    private IPhotoArticle.View view;

    public PhotoPresenter(IPhotoArticle.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        getPhotoFromNeiHan();
    }


    private void getPhotoFromNeiHan() {
        Call<PhotoArticleBean> call = RetrofitFactory.getRetrofit().create(RetrofitService.class).getPhoto();
        call.enqueue(new Callback<PhotoArticleBean>() {
            @Override
            public void onResponse(@NonNull Call<PhotoArticleBean> call, @NonNull Response<PhotoArticleBean> response) {
                PhotoArticleBean body = response.body();
                if (body != null && !RETRY.equals(body.getMessage()) && body.getData().isHas_more()) {
                    view.onUpdateUI(body.getData().getDataList());
                }
                if (!NetWorkUtil.isNetworkConnected(MyApplication.getContext())) {
                    view.responsInfo("请检查当前网路！");
                } else {
                    if (body != null && !RETRY.equals(body.getMessage()) && body.getData().isHas_more()) {
                        view.responsInfo(body.getData().getTip());
                    } else {
                        view.responsInfo("暂无最新数据");
                    }
                }

            }

            @Override
            public void onFailure(Call<PhotoArticleBean> call, Throwable t) {
                view.responsInfo("获取数据失败");
            }
        });
    }

}
