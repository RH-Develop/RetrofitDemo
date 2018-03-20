package com.example.retrofit.photo;


import com.example.retrofit.model.PhotoArticleBean;
import com.example.retrofit.base.IBaseView;

import java.util.List;

/**
 * @author RH
 */
public interface IPhotoArticle {

    interface View {

        void onUpdateUI(List<PhotoArticleBean.Data> list);

        void loadingFinish();

        void responsInfo(String s);

    }

    interface Presenter {

        void loadData();

    }
}
