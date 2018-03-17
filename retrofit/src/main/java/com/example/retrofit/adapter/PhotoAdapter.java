package com.example.retrofit.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.retrofit.R;
import com.example.retrofit.activity.ShowImageActivity;
import com.example.retrofit.model.PhotoArticleBean;
import com.example.retrofit.photo.IPhotoArticle;
import com.example.retrofit.utils.NetWorkUtil;
import java.util.ArrayList;
import java.util.List;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @author RH
 * @date 2018/3/5
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {
    private List<PhotoArticleBean.Data> dataList = new ArrayList<>();
    private final int SINGLE_PICTURE = 1;
    private final int MULTIPLE_PICTURE = 2;
    private Context context;
    private IPhotoArticle.View view;

    class MyViewHolder extends RecyclerView.ViewHolder {
        int viewType;
        TextView content;
        ImageView userImage;
        TextView textView;
        TextView diggCount;
        TextView buryCount;
        TextView commentsCount;
        ImageView diggImage;
        ImageView buryImage;

        ImageView imageView;
        JZVideoPlayerStandard jzVideoPlayerStandard;
        ViewStub imageViewViewStub;
        ViewStub videoViewStub;

        RecyclerView recyclerView;

        private MyViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            content = itemView.findViewById(R.id.photo_item_content);
            //category = itemView.findViewById(R.id.photo_item_category_name);
            userImage = itemView.findViewById(R.id.photo_item_user_image);
            textView = itemView.findViewById(R.id.photo_item_user_name);
            diggCount = itemView.findViewById(R.id.photo_item_digg_count);
            buryCount = itemView.findViewById(R.id.photo_item_bury_count);
            commentsCount = itemView.findViewById(R.id.photo_item_comment_count);
            diggImage = itemView.findViewById(R.id.photo_item_digg_image);
            buryImage = itemView.findViewById(R.id.photo_item_bury_image);
            if (viewType == SINGLE_PICTURE) {
                //imageView = itemView.findViewById(R.id.photo_item_image);
                //jzVideoPlayerStandard = itemView.findViewById(R.id.photo_item_video);
                imageViewViewStub = itemView.findViewById(R.id.photo_item_image_view_stub);
                videoViewStub = itemView.findViewById(R.id.photo_item_video_view_stub);
            } else if (viewType == MULTIPLE_PICTURE) {
                recyclerView = itemView.findViewById(R.id.photo_item_multiple_recycle_view);
            }

        }

    }


    public PhotoAdapter(List<PhotoArticleBean.Data> list, IPhotoArticle.View view) {
        dataList = list;
        this.view = view;
    }


    @Override
    public int getItemViewType(int position) {
        PhotoArticleBean.Data data = dataList.get(position);
        if (data.getGroup()!= null&&data.getGroup().getThumb_image_list() != null) {
           // Log.e(TAG, "getItemViewType:数组大小为 " + data.getGroup().getThumb_image_list().size() + "：" + position);
            return MULTIPLE_PICTURE;
        } else {
            return SINGLE_PICTURE;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        if (viewType == SINGLE_PICTURE) {
            View view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view, viewType);
            return viewHolder;
        } else if (viewType == MULTIPLE_PICTURE) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_multiple, parent, false), viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final PhotoArticleBean.Data data = dataList.get(position);
/*公共操作*/
        if (data.getGroup() != null) {
         /*作者信息*/
            Glide.with(context).load(data.getGroup().getUser().getAvatar_url()).into(holder.userImage);
            holder.textView.setText(data.getGroup().getUser().getName());
        /*文本内容*/
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
            int length = -2;
            if (data.getGroup().getCategory_name() != null) {
                stringBuilder.append("#").append(data.getGroup().getCategory_name()).append("#");
                length = data.getGroup().getCategory_name().length();
            }
            if (data.getGroup().getContent() != null) {
                stringBuilder.append(data.getGroup().getContent());
            }
            stringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ea8baf")), 0, length + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.content.setText(stringBuilder);

        /*评论点赞*/
            holder.diggCount.setText(String.valueOf(data.getGroup().getDigg_count()));
            holder.buryCount.setText(String.valueOf(data.getGroup().getBury_count()));
            holder.commentsCount.setText(String.valueOf(data.getGroup().getComment_count()));

            holder.diggImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.diggImage.setImageResource(R.mipmap.digg2);
                    holder.diggCount.setText(String.valueOf(data.getGroup().getDigg_count() + 1));

                }
            });
            holder.buryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.buryImage.setImageResource(R.mipmap.bury2);
                    holder.buryCount.setText(String.valueOf(data.getGroup().getBury_count() + 1) );
                }
            });

/*根据不同的布局进行不同的操作*/
            if (holder.viewType == SINGLE_PICTURE) {
                /*单加载视频or图片*/
                /*视频*/
                if (data.getGroup().getGifvideo() != null && data.getGroup().getGifvideo().getP_video().getUrl_list().get(0).getUrl() != null) {
                    if (holder.jzVideoPlayerStandard == null) {
                        View videoView = holder.videoViewStub.inflate();
                        holder.jzVideoPlayerStandard = videoView.findViewById(R.id.photo_item_video);
                        //Log.e(TAG, "创建视频控件");
                    }
                    holder.jzVideoPlayerStandard.setVisibility(View.VISIBLE);
                    if (holder.imageView != null) {
                        holder.imageView.setVisibility(View.GONE);
                        //Log.e(TAG, "图片不为空 ");
                    }
                    holder.jzVideoPlayerStandard.setUp(data.getGroup().getGifvideo().getP_video().getUrl_list().get(0).getUrl(), JZVideoPlayerStandard.SCREEN_WINDOW_LIST, "");
                    Glide.with(context).load(data.getGroup().getMiddle_image().getUrl_list().get(0).getUrl()).into(holder.jzVideoPlayerStandard.thumbImageView);
                }
                /*单张图片*/
                else {
                    if (holder.imageView == null) {
                        View imageView = holder.imageViewViewStub.inflate();
                        holder.imageView = imageView.findViewById(R.id.photo_item_image);
                        // Log.e(TAG, "创建图片控件");
                    }
                    if (holder.jzVideoPlayerStandard != null) {
                        holder.jzVideoPlayerStandard.setVisibility(View.GONE);
                        //Log.e(TAG, "视频不为空 ");
                    }
                    if (data.getGroup().getMiddle_image() != null && data.getGroup().getMiddle_image().getUrl_list().get(0) != null) {
                        holder.imageView.setVisibility(View.VISIBLE);
                        final String url = data.getGroup().getMiddle_image().getUrl_list().get(0).getUrl();
                        Glide.with(context).load(url).into(holder.imageView);
                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ShowImageActivity.class);
                                intent.putExtra("imageUrl",url);
                                context.startActivity(intent);
                            }
                        });
                    } else {
                        holder.imageView.setVisibility(View.GONE);
                    }
                }

            } else if (holder.viewType == MULTIPLE_PICTURE) {
                /*加载多图片*/
                //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                holder.recyclerView.setLayoutManager(gridLayoutManager);
                holder.recyclerView.setAdapter(new PhotoMultipleAdapter(data.getGroup().getThumb_image_list() , view));
            }

        }
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
