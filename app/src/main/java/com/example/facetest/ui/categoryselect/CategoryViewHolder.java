package com.example.facetest.ui.categoryselect;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.facetest.R;
import com.example.facetest.adapter.MvpViewHolder;

/**
 * Created by Benjamin on 15/05/16.
 */
public class CategoryViewHolder extends MvpViewHolder<CategoryItemPresenter> implements CategoryItemView {

    View categoryAr;
    TextView title;
    ImageView image;

    public CategoryViewHolder(View itemView) {
        super(itemView);
        categoryAr = itemView.findViewById(R.id.category_arview);
        title = (TextView)itemView.findViewById(R.id.category_title);
        image = (ImageView)itemView.findViewById(R.id.image);
    }

    public View getCategoryAr() {
        return categoryAr;
    }

    public ImageView getImage() {
        return image;
    }

    public TextView getTitle() {
        return title;
    }

    @Override
    public void setImage(Bitmap image) {

    }

    @Override
    public void setTitle(String title) {

    }
}
