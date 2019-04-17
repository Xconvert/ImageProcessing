package com.iconvert.imageprocessing;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wang.avi.AVLoadingIndicatorView;

import static com.iconvert.imageprocessing.ImageProvider.FILE_PATH;
import static com.iconvert.imageprocessing.ImageProvider.sTempImgName;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Main2Activity";
    private ImageView mImageView;
    private ImageManager mImageManager;
    private AVLoadingIndicatorView mLoadingView;
    private final int MSG_UPDATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mImageManager = new ImageManager(this);
        hideSystemUI();
        mLoadingView = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView_BallClipRotate);
        dismissLoadingView();

        mImageView = (ImageView) findViewById(R.id.image);
        Intent intent = getIntent();

        Uri imageUri = intent.getData();
        if (imageUri != null) {
            mImageView.setImageURI(imageUri);
            mImageManager.setImage(imageUri);
        }

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageManager.saveImage();
                //更新相册
                MediaScannerConnection.scanFile(Main2Activity.this, new String[]{FILE_PATH + "/" + sTempImgName}, null, null);

            }
        });

    }

    //全屏沉浸模式
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    public void onClick(View v) {
        showLoadingView();
        final int id = v.getId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (id) {
                    case R.id.src:
                        mImageManager.setSrcPic();
                    case R.id.avrFiltering:
                        mImageManager.avrFiltering();
                        break;
                    case R.id.medianFiltering:
                        mImageManager.medianFiltering();
                        break;
                    case R.id.histogramFiltering:
                        mImageManager.histogramFiltering();
                        break;
                    case R.id.negativeFilm:
                        mImageManager.negativeFilm();
                        break;
                    case R.id.grayScale:
                        mImageManager.grayScale();
                        break;
                    case R.id.grayScaleInversion:
                        mImageManager.grayScaleInversion();
                        break;
                    case R.id.gamma:
                        mImageManager.gamma();
                        break;
                    case R.id.laplaceProcess:
                        mImageManager.laplaceProcess();
                        break;
                    case R.id.fft:
                        mImageManager.fft();
                        break;
                    case R.id.lowPassFiltering:
                        mImageManager.lowPassFiltering();
                        break;
                    case R.id.highPassFiltering:
                        mImageManager.highPassFiltering();
                        break;
                    case R.id.inverseFiltering:
                        mImageManager.inverseFiltering();
                        break;
                    case R.id.wienerFiltering:
                        mImageManager.wienerFiltering();
                        break;

                    default:
                }
                handler.sendEmptyMessage(MSG_UPDATE);
            }
        }).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg1) {
            switch (msg1.what) {
                case MSG_UPDATE:
                    mImageView.setImageBitmap(mImageManager.getOutputImg());
                    dismissLoadingView();
                    break;
                default:
                    break;
            }
        }
    };

    //加载动画
    private void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void dismissLoadingView() {
        mLoadingView.setVisibility(View.GONE);
    }

}
