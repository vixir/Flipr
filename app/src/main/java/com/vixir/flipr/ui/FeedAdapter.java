package com.vixir.flipr.ui;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
    private final LayoutInflater layoutInflater;
    private final ColorDrawable[] shotLoadingPlaceholders;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = -1;
    private boolean showLoadingMore = false;

    public FeedAdapter(Activity hostActivity, DataLoadingSubject dataLoading) {
        this.host = hostActivity;
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
        if (viewType == VIEW_TYPE_ITEM) {
            return createFliprShotViewHolder(parent);
        } else if (viewType == VIEW_TYPE_LOADING) {
            Log.e("FeedAdapter", "VIEW_TYPE_LOADING");
            View view = LayoutInflater.from(host).inflate(R.layout.infinite_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDataItemCount() && getDataItemCount() > 0) {
            return VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_LOADING;
    }

    private FliprShotViewHolder createFliprShotViewHolder(ViewGroup parent) {
        final FliprShotViewHolder holder = new FliprShotViewHolder(layoutInflater.inflate(R.layout.main_item, parent, false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(final View v) {
                                                   //TODO change background based on image color palette
                                                   if (host instanceof HomeActivity) {
                                                       int position = ((HomeActivity) host).grid.getChildAdapterPosition(v);
                                                       if (position !=  RecyclerView.NO_POSITION) {
                                                           notifyItemChanged(position);
                                                       }
                                                   }
                                               }
                                           }
        );
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FliprShotViewHolder) {
            bindFliprShotHolder((PhotoShot) getItem(position), (FliprShotViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void bindFliprShotHolder(final PhotoShot photoShot, final FliprShotViewHolder holder, int position) {
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
        return getDataItemCount() + (showLoadingMore ? 1 : 0);
    }

    @Override
    public void dataStartedLoading() {
        if (showLoadingMore) return;
        showLoadingMore = true;
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
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            return -1L;
        }
        return getItem(position).id;
    }


    static class FliprShotViewHolder extends RecyclerView.ViewHolder {

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
        return items.size() + (showLoadingMore ? 0 : 1);
    }

    private int getLoadingMoreItemPosition() {
        return showLoadingMore ? getItemCount() - 1 : RecyclerView.NO_POSITION;
    }

    private void add(List<? extends PhotoShot> newItems) {
        for (PhotoShot newItem : newItems) {
            items.add(newItem);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}
