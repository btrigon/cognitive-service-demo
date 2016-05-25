package com.example.facetest.ui.categoryselect;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.facetest.FaceTestApplication;
import com.example.facetest.R;
import com.example.facetest.adapter.MvpRecyclerListAdapter;
import com.example.facetest.di2.DaggerNetInjectorComponent;
import com.example.facetest.models.CategoryItem;
import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.rx.RxBus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Benjamin on 15/05/16.
 */
public class CategoryAdapter extends MvpRecyclerListAdapter<CategoryItem, CategoryItemPresenter, CategoryViewHolder> {

    private static final int MIN_CLICK_INTERVAL = 1000;  //ms
    private List<CategoryItem> mCategoryItems;
    private long mLastClickTime;

    @Inject
    RxBus rxBus;

    public CategoryAdapter(List<CategoryItem> mCategoryItems) {
        this.mCategoryItems = mCategoryItems;
        mLastClickTime = 0;
        DaggerNetInjectorComponent.builder()
            .netComponent(FaceTestApplication.getNetComponent())
            .build()
            .inject(this);
    }

    @NonNull
    @Override
    protected CategoryItemPresenter createPresenter(@NonNull CategoryItem model) {
        CategoryItemPresenter presenter = new CategoryItemPresenter();
        presenter.setModel(model);
        return presenter;
    }

    @NonNull
    @Override
    protected Object getModelId(@NonNull CategoryItem model) {
        return model.getCategoryId();
    }


    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mCategoryItems == null ? 0 : mCategoryItems.size();
    }

    /**
     * Called when RecyclerView needs a new {@link CategoryViewHolder} of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(CategoryViewHolder, int)
     */
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.category_item_layout, parent, false);
//
//
//        return new CategoryViewHolder(v);


        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CategoryViewHolder(
                MaterialRippleLayout.on(inflater.inflate(R.layout.category_item_layout, parent, false))
                        .rippleOverlay(true)
                        .rippleAlpha(0.6f)
                        .rippleDelayClick(true)
                        .rippleColor(0x4DFFFFFF)
                        .rippleHover(false)
                        .create()
        );
    }

    /**.
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link CategoryViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        holder.getTitle().setText(mCategoryItems.get(position).getCategoryName());

        int color;
        if(position % 4 == 0){
            color = holder.itemView.getContext().getResources().getColor(R.color.light_green);
        }else if(position % 4 == 1) {
            color = holder.itemView.getContext().getResources().getColor(R.color.light_pink);
        }else if(position % 4 == 2) {
            color = holder.itemView.getContext().getResources().getColor(R.color.light_red);
        }else{
            color = holder.itemView.getContext().getResources().getColor(R.color.light_blue);
        }
        holder.getCategoryAr().setBackgroundColor(color);

        holder.getImage().setImageResource(mCategoryItems.get(position).getResourceId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - mLastClickTime < MIN_CLICK_INTERVAL){
                    //clicked too fast
                    //TODO: try RxBinding throttleFirst
                    return;
                }
                mLastClickTime = System.currentTimeMillis();
                Log.d("CategoryAdapter", "click" );
                rxBus.send(new CategorySelectEvent(mCategoryItems.get(position)));
            }
        });
    }

    public static class CategorySelectEvent{
        private CategoryItem categoryItem;
        public CategorySelectEvent(CategoryItem categoryItem) {
            this.categoryItem = categoryItem;
        }
        public CategoryItem getCategoryItem() {
            return categoryItem;
        }
    }

}
