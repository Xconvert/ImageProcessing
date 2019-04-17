package com.iconvert.imageprocessing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import static com.iconvert.imageprocessing.ImageProvider.ALBUM_REQUEST_CODE;
import static com.iconvert.imageprocessing.ImageProvider.CAMERA_REQUEST_CODE;
import static com.iconvert.imageprocessing.ImageProvider.FILE_PATH;
import static com.iconvert.imageprocessing.ImageProvider.sTempImgName;
import static com.iconvert.imageprocessing.ImageProvider.sTempImgUri;

public class MainActivity extends AppCompatActivity {

    private ImageProvider mImageProvider;
    private final String TAG = "MainActivity";

    private Button mTakePhoto;
    private Button mOpenAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageProvider = new ImageProvider(this);
        mTakePhoto = (Button) findViewById(R.id.button_camera);
        mOpenAlbum = (Button) findViewById(R.id.button_album);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get pictures from camera
                mImageProvider.takePhoto();
            }
        });

        mOpenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open album to get image
                mImageProvider.getPicFromAlbm();
            }
        });

        hideSystemUI();
        checkPermission();
        mTakePhoto.setAnimation(AnimationUtils.makeInAnimation(this, true));
        mOpenAlbum.setAnimation(AnimationUtils.makeInAnimation(this, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            // 调用相机后返回
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: CAMERA REQUEST");

                    Intent intent2 = new Intent(this, Main2Activity.class);
                    intent2.setData(sTempImgUri);
                    startActivity(intent2);
                    //更新相册
                    MediaScannerConnection.scanFile(this, new String[]{FILE_PATH + "/" + sTempImgName}, null, null);

                }
                break;
            //调用相册后返回
            case ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (intent == null) return;
                    Uri uri = intent.getData();
                    Log.i(TAG, "onActivityResult: ALBUM REQUEST uri is " + uri);
                    //mImageProvider.cropPhoto(uri);//裁剪图片
                    Intent intent2 = new Intent(this, Main2Activity.class);
                    intent2.setData(uri);
                    startActivity(intent2);
                }
                break;
            default:
        }
    }

    private void checkPermission() {
        final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                //Toast.makeText(this, "请打开储存权限！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                    REQUEST_WRITE_EXTERNAL_STORAGE);

        }

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

}
