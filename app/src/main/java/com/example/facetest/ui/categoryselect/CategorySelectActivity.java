package com.example.facetest.ui.categoryselect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.facetest.FaceTestApplication;
import com.example.facetest.R;
import com.example.facetest.di2.DaggerNetInjectorComponent;
import com.example.facetest.rx.RxBus;
import com.example.facetest.ui.BaseActivity;
import com.example.facetest.ui.categoryselect.CategoryAdapter;
import com.example.facetest.models.CategoryItem;
import com.example.facetest.ui.categoryselect.CategorySelectPresenter;
import com.example.facetest.presenter.PresenterManager;
import com.example.facetest.ui.groupphoto.GroupPhotoActivity;
import com.example.facetest.ui.categoryselect.CategorySelectView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Benjamin on 15/05/16.
 */
public class CategorySelectActivity extends BaseActivity implements CategorySelectView {

    private List<CategoryItem> mCategoryList;
    private CategoryAdapter mCategoryAdapter;
    private CategorySelectPresenter mPresenter;

    @Inject
    RxBus rxBus;

    private CompositeSubscription rxSubscriptions;

    @Bind(R.id.category_recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mPresenter = new CategorySelectPresenter();
        } else {
            mPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }

        setContentView(R.layout.activity_category_select);
        ButterKnife.bind(this);

        DaggerNetInjectorComponent.builder()
                .netComponent(FaceTestApplication.getNetComponent())
                .build()
                .inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setTitle(getString(R.string.category_title));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        populateCategoryList();

        mCategoryAdapter = new CategoryAdapter(mCategoryList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 2 columns
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        rxSubscriptions = new CompositeSubscription();
        rxSubscriptions.add(rxBus.toObserverable()//
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object event) {
                                if (event instanceof CategoryAdapter.CategorySelectEvent) {
                                    Log.d("Act", "rec event");
                                    Intent intent = new Intent(CategorySelectActivity.this, GroupPhotoActivity.class);
                                    intent.putExtra(GroupPhotoActivity.CATEGORY_INTENT,
                                            ((CategoryAdapter.CategorySelectEvent) event).getCategoryItem());
                                    startActivity(intent);;
                                }
                            }
                        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        rxSubscriptions.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unbindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(mPresenter, outState);
    }

    //TODO: move out of view layer
    private void populateCategoryList(){
        String[] categories = getResources().getStringArray(R.array.categories);
        mCategoryList = new ArrayList<>(categories.length);
        for(int i = 0; i < categories.length; i++){
            CategoryItem categoryItem = new CategoryItem();
            categoryItem.setCategoryName(categories[i]);
            categoryItem.setCategoryId(i);

            int resid;
            switch (i){
                case 0 : resid = R.drawable.ic_anger;
                    break;
                case 1 : resid = R.drawable.ic_contempt;
                    break;
                case 2 : resid = R.drawable.ic_disgust;
                    break;
                case 3 : resid = R.drawable.ic_fear;
                    break;
                case 4 : resid = R.drawable.ic_happy;
                    break;
                case 5 : resid = R.drawable.ic_neutral;
                    break;
                case 6 : resid = R.drawable.ic_sad;
                    break;
                case 7 : resid = R.drawable.ic_surprise;
                    break;
                default: resid = R.drawable.ic_unknown;
            }

            categoryItem.setResourceId(resid);
            mCategoryList.add(categoryItem);
        }
    }

    @Override
    public void showCategories(List<CategoryItem> categoryItemList) {

    }

    @Override
    public void showLoading() {

    }
}
