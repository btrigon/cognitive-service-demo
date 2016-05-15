package com.example.facetest.ui.groupphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.facetest.R;
import com.example.facetest.models.CategoryItem;
import com.example.facetest.ui.BaseActivity;
import com.example.facetest.utils.ImageSelectorUtil;
import com.example.facetest.utils.ImageUtils;

import java.io.File;

/**
 * Created by Benjamin on 15/05/16.
 */
public class GroupPhotoActivity extends BaseActivity {
    public static final String CATEGORY_INTENT = "category_intent";
    private CategoryItem mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupphoto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.send_photo_title));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mCategory = getIntent().getParcelableExtra(CATEGORY_INTENT);

        if(mCategory==null){
            Log.e("GroupPhotoActivity", "category was null");
        }else{
            GroupPhotoFragment mainFragment = (GroupPhotoFragment)getFragmentManager().findFragmentById(R.id.fragment_main);
            if(mainFragment!=null) {
                mainFragment.initCategory(mCategory);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1  && resultCode == RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator +
                    ImageSelectorUtil.BASE_DIR + File.separator + "test1234" + ".jpg");
            ImageUtils.resizeStoredCameraImage(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;  // mutable for drawing
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            GroupPhotoFragment mainFragment = (GroupPhotoFragment)getFragmentManager().findFragmentById(R.id.fragment_main);
            if(mainFragment!=null && bitmap!=null){
                mainFragment.sendBitmapFromActivity(bitmap);
            }

        }
    }
}
