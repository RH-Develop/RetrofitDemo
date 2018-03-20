package com.example.retrofit.base;

import com.trello.rxlifecycle2.LifecycleTransformer;

/**
 * @author RH
 * @date 2018/3/5
 */
public interface IBaseView<T> {

    //void setPresenter(T presenter);

    /**
     * 绑定生命周期
     */
    <T> LifecycleTransformer<T> bindToLife();

}
