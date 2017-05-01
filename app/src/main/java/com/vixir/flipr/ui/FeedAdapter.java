package com.vixir.flipr.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vixir.flipr.R;
import com.vixir.flipr.data.DataLoadingSubject;
import com.vixir.flipr.data.PhotoShot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DataLoadingSubject.DataLoadingCallbacks {

    private List<PhotoShot> items;
    private final Activity host;
    @Nullable
    private final DataLoadingSubject dataLoading;
    private final LayoutInflater layoutInflater;
    private final ColorDrawable[] shotLoadingPlaceholders;
    private boolean showLoadingMore = false;


    public FeedAdapter(Activity hostActivity, DataLoadingSubject dataLoading) {
        this.host = hostActivity;
        this.dataLoading = dataLoading;
        dataLoading.registerCallback(this);
        layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();

        final TypedArray a = host.obtainStyledAttributes(R.styleable.FliprFeed);
        final int loadingColorArrayId = a.getResourceId(R.styleable.FliprFeed_shotLoadingPlaceholderColors, 0);
        if (loadingColorArrayId != 0) {
            int[] placeholderColors = host.getResources().getIntArray(loadingColorArrayId);
            shotLoadingPlaceholders = new ColorDrawable[placeholderColors.length];
            for (int i = 0; i < placeholderColors.length; i++) {
                shotLoadingPlaceholders[i] = new ColorDrawable(placeholderColors[i]);
            }
        } else {
            shotLoadingPlaceholders = new ColorDrawable[]{new ColorDrawable(Color.DKGRAY)};
        }
        a.recycle();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createFliprShotViewHolder(parent);
    }

    private FliprShotViewHolder createFliprShotViewHolder(ViewGroup parent) {
        final FliprShotViewHolder holder = new FliprShotViewHolder(layoutInflater.inflate(R.layout.main_item, parent, false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(final View v) {
                                                   //TODO change background based on image color palette
                                                   int position = ((HomeActivity) host).grid.getChildAdapterPosition(v);
                                                   if (position != RecyclerView.NO_POSITION) {
                                                       notifyItemChanged(position);
                                                   }
                                               }
                                           }
        );
        //TODO Custom ImageView with proper image ratio and badge color
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindFliprShotHolder((PhotoShot) getItem(position), (FliprShotViewHolder) holder, position);
    }

    private void bindFliprShotHolder(final PhotoShot photoShot, final FliprShotViewHolder holder, int position) {

        // Description api : ??
        // holder.title.setText(photoShot.title);
        holder.title.setText("");
        holder.description.setText(photoShot.description);
        holder.dimen.setText(photoShot.size);
        Glide.with(host)
                .load(photoShot.image)
                .placeholder(shotLoadingPlaceholders[position % shotLoadingPlaceholders.length])
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .into(holder.image);
        holder.image.setBackground(shotLoadingPlaceholders[position % shotLoadingPlaceholders.length]);
    }

    private PhotoShot getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void dataStartedLoading() {
        notifyItemInserted(getLoadingMoreItemPosition());
    }

    @Override
    public void dataFinishedLoading() {
        if (!showLoadingMore) return;
        final int loadingPos = getLoadingMoreItemPosition();
        showLoadingMore = false;
        notifyItemRemoved(loadingPos);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    static class FliprShotViewHolder extends RecyclerView.ViewHolder {

        //TODO convert it to a particular ratio image view? 600x600 or 4:3
        @BindView(R.id.shot)
        ThreeFourImageView image;
        @BindView(R.id.title_metadata)
        TextView title;
        @BindView(R.id.description_metadata)
        TextView description;
        @BindView(R.id.dimen_metadata)
        TextView dimen;
        View mCardFrontLayout;
        View mCardBackLayout;
        boolean isBackVisible = false;

        public FliprShotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCardFrontLayout = itemView.findViewById(R.id.card_front);
            mCardBackLayout = itemView.findViewById(R.id.card_back);
            mCardBackLayout.setVisibility(View.INVISIBLE);
        }
    }

    //TODO sorting logic.

    public void addAndResort(List<? extends PhotoShot> newItems) {
        add(newItems);
        notifyDataSetChanged();
    }


    public int getDataItemCount() {
        return items.size();
    }

    private void add(List<? extends PhotoShot> newItems) {
        for (PhotoShot newItem : newItems) {
            items.add(newItem);
        }
    }
}
