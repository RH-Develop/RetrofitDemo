package com.example.retrofit.photo;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.retrofit.MyApplication;
import com.example.retrofit.network.RetrofitFactory;
import com.example.retrofit.network.api.RetrofitService;
import com.example.retrofit.model.PhotoArticleBean;
import com.example.retrofit.utils.NetWorkUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
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
        //getPhotoFromNeiHan();
        getPhotoByObserve();
    }

    /**
     * 常规Retrofit
     */
    private void getPhotoFromNeiHan() {
        Call<PhotoArticleBean> call = RetrofitFactory.getRetrofit().create(RetrofitService.class).getPhoto1();
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

    /**
     * Retrofit + RxJava（观察者模式）
     */
    private void getPhotoByObserve() {
        Observable<PhotoArticleBean> observable = RetrofitFactory.getRetrofit().create(RetrofitService.class).getPhoto();
        // Schedulers.io()（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。此外还有Schedulers.computation()(计算所使用的 Scheduler)
        observable.subscribeOn(Schedulers.io())
                /*  if判断  */
                //主线程，进行UI操作
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<PhotoArticleBean>() {
                    @Override
                    public boolean test(PhotoArticleBean photoArticleBean) throws Exception {
                        if (photoArticleBean.getData().isHas_more()) {
                            if (NetWorkUtil.isNetworkConnected(MyApplication.getContext())) {
                                view.responsInfo(photoArticleBean.getData().getTip());
                            } else {
                                view.responsInfo("请检查当前网路！");
                            }
                            return true;
                        } else {
                            view.responsInfo("暂无最新数据");
                            return false;
                        }
                    }
                })
                .observeOn(Schedulers.io())
                /*  数据转换 ,可将一个数组转换成多个对象 */
                .flatMap(new Function<PhotoArticleBean, ObservableSource<List<PhotoArticleBean.Data>>>() {
                    @Override
                    public ObservableSource<List<PhotoArticleBean.Data>> apply(PhotoArticleBean photoArticleBean) throws Exception {
                        return Observable.fromArray(photoArticleBean.getData().getDataList());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                /*  观察者方法实现 */
                .subscribe(new Observer<List<PhotoArticleBean.Data>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //判断是否解除绑定
                        Log.e(TAG, "onSubscribe: " + d.isDisposed());
                    }

                    @Override
                    public void onNext(List<PhotoArticleBean.Data> data) {
                        //多个对象时会多次调用
                        view.onUpdateUI(data);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //e.printStackTrace();
                        Log.e(TAG, "onError: " + e.getMessage());
                        view.responsInfo("网络数据获取失败");
                        view.loadingFinish();
                    }

                    @Override
                    public void onComplete() {
                        view.loadingFinish();
                    }
                });
    }

}
