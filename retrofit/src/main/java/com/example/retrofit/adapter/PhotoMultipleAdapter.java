package com.example.retrofit.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.retrofit.R;
import com.example.retrofit.activity.ShowImageActivity;
import com.example.retrofit.photo.IPhotoArticle;
import com.example.retrofit.utils.NetWorkUtil;
import com.example.retrofit.utils.Utils;
import com.example.retrofit.model.PhotoArticleBean;
import java.util.List;

/**
 * @author RH
 * @date 2018/3/12
 */
public class PhotoMultipleAdapter extends RecyclerView.Adapter<PhotoMultipleAdapter.PictureViewHolder> {
    private static final String TAG = "PhotoMultipleAdapter";
    private Context context;
    private IPhotoArticle.View view;
    private List<PhotoArticleBean.ThumbImage> thumbImageList;


    public PhotoMultipleAdapter(List<PhotoArticleBean.ThumbImage> thumbImageList, IPhotoArticle.View view) {
        this.thumbImageList = thumbImageList;
        this.view = view;
    }

    class PictureViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        private PictureViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_item_multiple_recycle_view_item_image);
            //setImageViewSize(imageView);
        }
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_multiple_adapter_item, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder,  int position) {
        final String imgUrl = thumbImageList.get(position).getUrl();

        Glide.with(context).load(imgUrl).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("imageUrl", imgUrl);
                    context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return thumbImageList.size();
    }

    private void setImageViewSize(ImageView imageView) {
        ViewGroup.LayoutParams lp= imageView.getLayoutParams();
       //将宽度设置为屏幕的1/3
        lp.width= Utils.getScreenWidth()/3-24;
        //Log.e(TAG, "setImageViewSize: "+lp.width);
        imageView.setLayoutParams(lp);
    }

}
