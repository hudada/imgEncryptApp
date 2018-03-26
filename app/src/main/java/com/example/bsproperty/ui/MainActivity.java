package com.example.bsproperty.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.bsproperty.R;
import com.example.bsproperty.bean.ImagePiece;
import com.example.bsproperty.utils.ImageSplitter;
import com.example.bsproperty.utils.LQRPhotoSelectUtils;
import com.example.bsproperty.utils.Logistic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends BaseActivity {


    @BindView(R.id.btn_select)
    Button btnSelect;
    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.btn_do)
    Button btnDo;
    @BindView(R.id.btn_return)
    Button btnReturn;
    @BindView(R.id.ll_show)
    LinearLayout llShow;
    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;
    private Bitmap selectBitmap;
    private Bitmap bitmapNext;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                Bitmap bitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
                try {
                    selectBitmap = Bitmap.createBitmap(bitmap, 0, 0, 256, 256);
                    ivShow.setImageBitmap(selectBitmap);
                } catch (Exception e) {
                    showToast(MainActivity.this, "图片需大于等于256*256");
                }

            }
        }, false);
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void loadData() {

    }


    @OnClick({R.id.btn_select, R.id.btn_do, R.id.btn_return})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(new String[]{
                        "拍照选择", "本地相册选择", "取消"
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                PermissionGen.with(MainActivity.this)
                                        .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA
                                        ).request();
                                break;
                            case 1:
                                PermissionGen.needPermission(MainActivity.this,
                                        LQRPhotoSelectUtils.REQ_SELECT_PHOTO,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                );
                                break;
                            case 2:
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.btn_do:
                if (selectBitmap != null) {
//                    Intent intent = new Intent(MainActivity.this, SplitShowActivity.class);
//                    intent.putExtra("path", selectBitmap);
//                    startActivity(intent);

                    final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final long t1 = System.currentTimeMillis();

                            Bitmap bitmap = selectBitmap;
                            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

                            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


                            int[] doubles = Logistic.getKey(9999998);
                            List<ImagePiece> mItemBitmaps = ImageSplitter.split(bitmap, 8);
                            int[] pixels8 = new int[mItemBitmaps.get(0).bitmap.getWidth() * mItemBitmaps.get(0).bitmap.getHeight()];
                            int[] pixels8c = new int[pixels8.length];
                            for (int i = 0; i < mItemBitmaps.size(); i++) {
                                Bitmap bitmap8 = mItemBitmaps.get(i).bitmap;

                                bitmap8.getPixels(pixels8, 0,
                                        bitmap8.getWidth(), 0, 0,
                                        bitmap8.getWidth(), bitmap8.getHeight());
                                for (int i1 = 0; i1 < pixels8.length; i1++) {
                                    int xx = doubles[i1] ^ pixels8[i1];
                                    pixels8c[i1] = xx;
                                }

                                bitmap8.setPixels(pixels8c, 0, bitmap8.getWidth(), 0, 0, bitmap8.getWidth(), bitmap8.getHeight());

                            }
                            Bitmap newmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                            Canvas canvas = new Canvas(newmap);

                            int x = 0;
                            int y = 0;
                            for (int i = 0; i < mItemBitmaps.size(); i++) {
                                Bitmap bitmap1 = mItemBitmaps.get(i).bitmap;
                                if (i % 32 == 0 && i > 0) {
                                    x = 0;
                                    y++;
                                }
                                canvas.drawBitmap(bitmap1,
                                        bitmap1.getWidth() * x,
                                        bitmap1.getHeight() * y, null);
                                x++;
                            }

                            canvas.save(Canvas.ALL_SAVE_FLAG);
                            canvas.restore();

                            bitmapNext = newmap;

                            double xx = 0;
                            ArrayList<Point> points = new ArrayList<>();


                            int loop = bitmapNext.getWidth();
                            for (int i = 0; i < loop; i++) {
                                for (int i1 = 0; i1 < loop; i1++) {
                                    if (i == 0 && i1 == 0) {
                                        xx = 9999998 / Math.pow(10, 8);
                                    } else {
                                        xx = 4 * xx * (1 - xx);
                                    }
                                    int M = (int) Math.floor(xx * bitmapNext.getWidth() * bitmapNext.getWidth());
                                    int p = M / loop;
                                    int q = M % loop;
                                    Point point = new Point(p, q);
                                    Point point1 = new Point(i,i1);
                                    if (!points.contains(point) && !points.contains(point1)) {
                                        points.add(point);
                                        points.add(point1);
                                        int p1 = bitmapNext.getPixel(i,i1);
                                        int p2 = bitmapNext.getPixel(p,q);


                                        bitmapNext.setPixel(p, q, p1);
                                        bitmapNext.setPixel(i, i1, p2);
                                    }
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    ivShow.setImageBitmap(bitmapNext);

                                    long t2 = System.currentTimeMillis();

                                    long t3 = t2 - t1;

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                    builder1.setMessage(t3/1000+"").show();
                                }
                            });



                        }
                    }).start();


                } else {
                    showToast(MainActivity.this, "请选择图片");
                }
                break;
            case R.id.btn_return:

                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final long t1 = System.currentTimeMillis();
                        double xx = 0;
                        ArrayList<Point> points = new ArrayList<>();


                        int loop = bitmapNext.getWidth();
                        for (int i = 0; i < loop; i++) {
                            for (int i1 = 0; i1 < loop; i1++) {
                                if (i == 0 && i1 == 0) {
                                    xx = 9999998 / Math.pow(10, 8);
                                } else {
                                    xx = 4 * xx * (1 - xx);
                                }
                                int M = (int) Math.floor(xx * bitmapNext.getWidth() * bitmapNext.getWidth());
                                int p = M / loop;
                                int q = M % loop;
                                Point point = new Point(p, q);
                                Point point1 = new Point(i,i1);
                                if (!points.contains(point) && !points.contains(point1)) {
                                    points.add(point);
                                    points.add(point1);
                                    int p1 = bitmapNext.getPixel(i,i1);
                                    int p2 = bitmapNext.getPixel(p,q);


                                    bitmapNext.setPixel(p, q, p1);
                                    bitmapNext.setPixel(i, i1, p2);
                                }
                            }
                        }


                        Bitmap bitmap = bitmapNext;

                        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

                        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


                        int[] doubles = Logistic.getKey(9999998);
                        List<ImagePiece> mItemBitmaps = ImageSplitter.split(bitmap, 8);
                        int[] pixels8 = new int[mItemBitmaps.get(0).bitmap.getWidth() * mItemBitmaps.get(0).bitmap.getHeight()];
                        int[] pixels8c = new int[pixels8.length];
                        for (int i = 0; i < mItemBitmaps.size(); i++) {
                            Bitmap bitmap8 = mItemBitmaps.get(i).bitmap;

                            bitmap8.getPixels(pixels8, 0,
                                    bitmap8.getWidth(), 0, 0,
                                    bitmap8.getWidth(), bitmap8.getHeight());
                            for (int i1 = 0; i1 < pixels8.length; i1++) {
                                int xx1 = doubles[i1] ^ pixels8[i1];
                                pixels8c[i1] = xx1;
                            }

                            bitmap8.setPixels(pixels8c, 0, bitmap8.getWidth(), 0, 0, bitmap8.getWidth(), bitmap8.getHeight());

                        }
                        final Bitmap newmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                        Canvas canvas = new Canvas(newmap);

                        int x = 0;
                        int y = 0;
                        for (int i = 0; i < mItemBitmaps.size(); i++) {
                            Bitmap bitmap1 = mItemBitmaps.get(i).bitmap;
                            if (i % 32 == 0 && i > 0) {
                                x = 0;
                                y++;
                            }
                            canvas.drawBitmap(bitmap1,
                                    bitmap1.getWidth() * x,
                                    bitmap1.getHeight() * y, null);
                            x++;
                        }

                        canvas.save(Canvas.ALL_SAVE_FLAG);
                        canvas.restore();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                ivShow.setImageBitmap(newmap);
                                long t2 = System.currentTimeMillis();

                                long t3 = t2 - t1;

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                                builder1.setMessage(t3/1000+"").show();
                            }
                        });

                    }
                }).start();


                break;
        }
    }


    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void takePhoto() {
        mLqrPhotoSelectUtils.takePhoto();
    }

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void selectPhoto() {
        mLqrPhotoSelectUtils.selectPhoto();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void showTip1() {
        showDialog();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void showTip2() {
        showDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("权限申请");
        builder.setMessage("在设置-应用-权限 中开启相机、存储权限，才能正常使用拍照或图片选择功能");

        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {//点击完确定后，触发这个事件

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
    }
}
