package com.example.facetest.ui.groupphoto;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facetest.FaceTestApplication;
import com.example.facetest.R;
import com.example.facetest.di2.DaggerNetInjectorComponent;
import com.example.facetest.models.CategoryItem;
import com.example.facetest.network.apimodels.FaceEmotion;
import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.presenter.PresenterManager;
import com.example.facetest.utils.ImageSelectorUtil;
import com.example.facetest.utils.ImageUtils;
import com.liulishuo.magicprogresswidget.MagicProgressBar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by Benjamin on 15/05/16.
 */
@RuntimePermissions
public class GroupPhotoFragment extends Fragment implements GroupPhotoView {
    private static final String TAG = "GroupPhotoFragment";
    private static final String API_KEY = "YOUR_API_KEY";

    @Inject
    @Named("uncachedEmotionApi")
    EmotionApiInterface mEmotionApi;

    private Bitmap mCurrentCameraBitmap;
    private GroupPhotoPresenter mPresenter;

    @Bind(R.id.send_button) Button mSendButton;
    @Bind(R.id.error_results_text) TextView mResultsText;
    @Bind(R.id.progress_layout) LinearLayout mLoadingProgress;
    @Bind(R.id.toptencontainer) LinearLayout mTopTenContainer;
    @Bind(R.id.fragment_main_image) ImageView mImage;
    @Bind(R.id.results_top10title) ImageView mTopTenIcon;
    @Bind(R.id.results_layout) View mResultsLayoutContainer;
    @Bind(R.id.top10_layout) View mTopTenLayout;
    @Bind(R.id.results_title) TextView mTopTenTitle;
    @Bind(R.id.result_winner) View mResultsWinnerLayout;
    @Bind(R.id.result_loser) View mResultsLoserLayout;

    //TODO: move to ViewHolder
    private View mWinnerInner, mLoserInner ;
    private CircleImageView mWinnerImage, mLoserImage;
    private TextView mResultsWinnerText, mResultsLoserText;
    private MagicProgressBar mWinnerProgress, mLoserProgress;

    public GroupPhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_groupphoto, container, false);
        ButterKnife.bind(this, rootView); // bind Views

        mResultsLayoutContainer.setVisibility(View.INVISIBLE);
        mTopTenLayout.setVisibility(View.INVISIBLE);

        mWinnerInner = ButterKnife.findById(mResultsWinnerLayout, R.id.result_section_inner);
        mWinnerInner.setBackgroundColor(getResources().getColor(R.color.light_green));

        mLoserInner = ButterKnife.findById(mResultsLoserLayout, R.id.result_section_inner);
        mLoserInner.setBackgroundColor(getResources().getColor(R.color.light_red));

        mWinnerImage = ButterKnife.findById(mResultsWinnerLayout, R.id.result_image_thumb);
        mLoserImage = ButterKnife.findById(mResultsLoserLayout, R.id.result_image_thumb);

        mResultsWinnerText = ButterKnife.findById(mResultsWinnerLayout, R.id.result_image_text);
        mResultsWinnerText.setText(getResources().getString(R.string.winner));
        mResultsWinnerText.setBackgroundColor(getResources().getColor(R.color.light_green_80));

        mResultsLoserText = ButterKnife.findById(mResultsLoserLayout, R.id.result_image_text);
        mResultsLoserText.setText(getResources().getString(R.string.loser));
        mResultsLoserText.setBackgroundColor(getResources().getColor(R.color.light_red_80));

        mWinnerProgress = ButterKnife.findById(mResultsWinnerLayout, R.id.result_progress);
        mLoserProgress = ButterKnife.findById(mResultsLoserLayout, R.id.result_progress);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupPhotoFragmentPermissionsDispatcher.retrievePhotoWithCheck(GroupPhotoFragment.this);
            }
        });

        return rootView;
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    protected void retrievePhoto(){
        // external camera, use default
        ImageSelectorUtil.openDefaultCamera(getActivity(), "test1234");
    }

    /**
     * circle faces in photo
     */
    private void highlightFaces(List<FaceEmotion> facesList){
        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setStyle(Paint.Style.STROKE);
        int strokeWidth=4;
        myPaint.setStrokeWidth(strokeWidth);
        myPaint.setColor(0xffff0000);   //color.RED
        Canvas canvas = new Canvas(mCurrentCameraBitmap);
        for(FaceEmotion face : facesList){
            highlightOneFace(face, canvas, myPaint);
        }
        mImage.setImageBitmap(mCurrentCameraBitmap); // with faces highlighted
    }

    /**
     * circle single face in photo
     * @param faceEmotion result from api
     * @param canvas to draw on
     * @param paint settings for drawing
     */
    private void highlightOneFace(FaceEmotion faceEmotion, Canvas canvas, Paint paint){
        int width = faceEmotion.getFaceRectangle().getWidth();
        int height = faceEmotion.getFaceRectangle().getHeight();
        float radius = width > height ? width : height;
        int xCenter = faceEmotion.getFaceRectangle().getLeft() + (int)(radius/2);
        int yCenter = faceEmotion.getFaceRectangle().getTop() + (int)(radius/2);
        canvas.drawCircle(xCenter, yCenter, radius, paint);
    }

    /**
     * handle response codes from api
     * @param code
     * @param message
     */
    private void handleOtherResponseCode(int code, String message){
      //  mSendButton.setVisibility(View.VISIBLE);
        if(code == 400){
            String messageText =  "JSON parsing error, faceRectangles cannot be parsed correctly, or count exceeds 64, or content-type is not recognized.";
            mResultsText.setText("Error: " + messageText);
        }else{
            mResultsText.setText("Error: " + message);
        }
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * { #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        DaggerNetInjectorComponent.builder()
                .netComponent(FaceTestApplication.getNetComponent())
                .build()
                .inject(this);

        if (savedInstanceState == null) {
            mPresenter = new GroupPhotoPresenter();
        } else {
            mPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mPresenter.bindView(this);
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to {Activity#onPause() Activity.onPause} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unbindView();
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to {@link #onCreate(Bundle)},
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, and
     * {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>This corresponds to {Activity#onSaveInstanceState(Bundle)
     * Activity.onSaveInstanceState(Bundle)} and most of the discussion there
     * applies here as well.  Note however: <em>this method may be called
     * at any time before {@link #onDestroy()}</em>.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(mPresenter, outState);
    }

    /**
     * Receive Bitmap from Activity
     * @param bitmap
     */
    public void sendBitmapFromActivity(Bitmap bitmap){
        if (bitmap != null) {
            mCurrentCameraBitmap = bitmap;
            mImage.setImageBitmap(mCurrentCameraBitmap);   //set camera image
            mImage.setVisibility(View.VISIBLE);
            updateUiOnSending();
            mPresenter.sendBitmap(bitmap, API_KEY);
        }
    }

    @Override
    public void updateUiOnSending(){
        mImage.setVisibility(View.VISIBLE);
        mSendButton.setVisibility(View.GONE);
        mResultsText.setVisibility(View.INVISIBLE);
        mLoadingProgress.setVisibility(View.VISIBLE);
        mResultsLayoutContainer.setVisibility(View.INVISIBLE);
        mTopTenLayout.setVisibility((View.INVISIBLE));
    }

    @Override
    public void updateUiOnSentSuccess(){
        mImage.setVisibility(View.VISIBLE);
        mSendButton.setVisibility(View.GONE);
        mResultsText.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mResultsLayoutContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateUiOnSentFailed(String errorMessage){
        mImage.setVisibility(View.INVISIBLE);
        mSendButton.setVisibility(View.VISIBLE);
        mResultsText.setText(errorMessage);
        mResultsText.setVisibility(View.VISIBLE);
        mLoadingProgress.setVisibility(View.GONE);
        mResultsLayoutContainer.setVisibility(View.INVISIBLE);
        mTopTenLayout.setVisibility(View.INVISIBLE);
    }

    public void initCategory(CategoryItem categoryItem){
        mPresenter.setCategory(categoryItem);
    }

    @Override
    public void setTopTenIcon(int resourceId) {
        if(mTopTenIcon!=null){
            mTopTenIcon.setImageResource(resourceId);
        }
    }

    private void showTopTen(List<FaceEmotion> facesList, int categoryId){

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        final int MAX_FACES = 20;

        for(int i = 0; i < facesList.size() && i < MAX_FACES; i++){
            View rowView = layoutInflater.inflate(R.layout.result_section_rowitem, mTopTenContainer, false);
            TextView rowText = (TextView)rowView.findViewById(R.id.result_rowitem_text);
            rowText.setText("" + (i + 1));
            CircleImageView image = (CircleImageView)rowView.findViewById(R.id.result_rowitem_image);
            FaceEmotion face = facesList.get(i);
            final Bitmap thumb = ImageUtils.getCroppedBitmap(mCurrentCameraBitmap,
                    face.getFaceRectangle().getLeft(),
                    face.getFaceRectangle().getTop(),
                    face.getFaceRectangle().getWidth(),
                    face.getFaceRectangle().getHeight(),
                    75);
            image.setImageBitmap(thumb);
            Double progress = face.getScores().getScoreForCategoryId(categoryId);
            // set min and max range for result
            double rowMin = 0.1 - (i * 0.01);
            double rowMax = 1.0 - (i * 0.04);
            if(progress < rowMin){
                progress = rowMin;
            }else if(progress > rowMax){
                progress=rowMax;
            }

            Log.d(TAG, (i + 1) + " progress " + progress);
            MagicProgressBar progressBar = (MagicProgressBar)rowView.findViewById(R.id.result_rowitem_progress);
            progressBar.setPercent(progress.floatValue());

            if(i==MAX_FACES-1 || i==facesList.size()-1){
                // last item, remove bottom divider
                rowView.findViewById(R.id.result_rowitem_divider).setVisibility(View.GONE);
            }

            mTopTenContainer.addView(rowView);
        }

    }

    @Override
    public void setSendButtonText(String text) {
        if(mSendButton!=null) {
            mSendButton.setText(
                    String.format(getResources().getString(R.string.take_photo), text));
        }
    }

    @Override
    public void setTopTenTitle(int count, String categoryName) {
        mTopTenTitle.setText(String.format(getResources().getString(R.string.result_title),
                count, categoryName));
    }

    @Override
    public void showError() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void setResultsText(String resultsText) {

    }

    @Override
    public void animateResults(List<FaceEmotion> facesList, int categoryId) {
        FaceEmotion topFaceEmotion = facesList.get(0);
        int padding = 75; // 75%
        final Bitmap thumb = ImageUtils.getCroppedBitmap(mCurrentCameraBitmap,
                topFaceEmotion.getFaceRectangle().getLeft(),
                topFaceEmotion.getFaceRectangle().getTop(),
                topFaceEmotion.getFaceRectangle().getWidth(),
                topFaceEmotion.getFaceRectangle().getHeight(),
                padding);
        Double winnerValue = topFaceEmotion.getScores().getScoreForCategoryId(categoryId);
        if(winnerValue < 0.1) winnerValue = 0.1;

        FaceEmotion loserFaceEmotion = facesList.get(facesList.size() - 1);
        final Bitmap loserThumb = ImageUtils.getCroppedBitmap(mCurrentCameraBitmap,
                loserFaceEmotion.getFaceRectangle().getLeft(),
                loserFaceEmotion.getFaceRectangle().getTop(),
                loserFaceEmotion.getFaceRectangle().getWidth(),
                loserFaceEmotion.getFaceRectangle().getHeight(),
                padding);
        Double loserValue = loserFaceEmotion.getScores().getScoreForCategoryId(categoryId);

        showTopTen(facesList, categoryId); // before drawing on bitmap

        highlightFaces(facesList); // add highlight to original image after getting thumbnails
        int loserAnimationTime = (int)(3000 * loserValue);
        if(loserAnimationTime< 1000) loserAnimationTime = 1000;

        ObjectAnimator loserAnimator = ObjectAnimator.ofFloat(mLoserProgress, "percent", 0, loserValue.floatValue());
        loserAnimator.setDuration(loserAnimationTime);
        loserAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLoserImage.setImageBitmap(loserThumb);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        ObjectAnimator winnerAnimator =  ObjectAnimator.ofFloat(mWinnerProgress, "percent", 0, winnerValue.floatValue());
        winnerAnimator.setDuration(3000);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                loserAnimator,
                winnerAnimator
        );
        set.setInterpolator(new AccelerateInterpolator());
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mWinnerImage.setImageBitmap(thumb);
                mTopTenLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showRationaleForCamera(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.permission_camera_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        Toast.makeText(getActivity(), R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void onCameraNeverAskAgain() {
        Toast.makeText(getActivity(), R.string.permission_camera_never_askagain, Toast.LENGTH_SHORT).show();
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        GroupPhotoFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


}
