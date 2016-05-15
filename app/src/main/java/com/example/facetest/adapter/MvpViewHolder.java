package com.example.facetest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.facetest.presenter.BasePresenter;

/**
 * Created by Benjamin on 15/05/16.
 */
public abstract class MvpViewHolder<P extends BasePresenter> extends RecyclerView.ViewHolder {
    protected P presenter;

    public MvpViewHolder(View itemView) {
        super(itemView);
    }

    public void bindPresenter(P presenter) {
        this.presenter = presenter;
        presenter.bindView(this);
    }

    public void unbindPresenter() {
        presenter = null;
    }
}
