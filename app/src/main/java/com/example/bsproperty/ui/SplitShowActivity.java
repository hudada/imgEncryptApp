package com.example.bsproperty.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bsproperty.R;
import com.example.bsproperty.adapter.BaseAdapter;
import com.example.bsproperty.bean.ImagePiece;
import com.example.bsproperty.utils.ImageSplitter;
import com.example.bsproperty.utils.Logistic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplitShowActivity extends BaseActivity {

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @Override
    protected void initView(Bundle savedInstanceState) {
        rvList.setLayoutManager(new GridLayoutManager(this, 32));
        rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_split_show;
    }

    @Override
    protected void loadData() {


        Bitmap bitmap;
        TypedValue value = new TypedValue();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opts.inTargetDensity = value.density;
        opts.inScaled = false;
//        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test, opts);
//        bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("path"), opts);
        bitmap = getIntent().getParcelableExtra("path");

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


        int[] doubles = Logistic.getKey(9999998);
        List<ImagePiece> mItemBitmaps = ImageSplitter.split(bitmap, 8);
        int[] pixels8 = new int[mItemBitmaps.get(0).bitmap.getWidth() * mItemBitmaps.get(0).bitmap.getHeight()];
        int[] pixels8c = new int[pixels8.length];
        for (int i = 0; i < mItemBitmaps.size(); i++) {
            Bitmap bitmap8 = mItemBitmaps.get(i).bitmap;
//            if (i == 0) {
                bitmap8.getPixels(pixels8, 0,
                        bitmap8.getWidth(), 0, 0,
                        bitmap8.getWidth(), bitmap8.getHeight());
                for (int i1 = 0; i1 < pixels8.length; i1++) {
                    int xx = doubles[i1] ^ pixels8[i1];
                    pixels8c[i1] = xx;
                }

                bitmap8.setPixels(pixels8c, 0, bitmap8.getWidth(), 0, 0, bitmap8.getWidth(), bitmap8.getHeight());

//            }
        }

        MyAdapter adapter = new MyAdapter(this, R.layout.item_show_split, (ArrayList<ImagePiece>) mItemBitmaps);
        rvList.setAdapter(adapter);

    }

    private class MyAdapter extends BaseAdapter<ImagePiece> {

        public MyAdapter(Context context, int layoutId, ArrayList<ImagePiece> data) {
            super(context, layoutId, data);
        }

        @Override
        public void initItemView(BaseViewHolder holder, ImagePiece imagePiece, int position) {
            ImageView imageView = (ImageView) holder.getView(R.id.iv_show);
            imageView.setImageBitmap(imagePiece.bitmap);

            View ll_show = holder.getView(R.id.ll_show);
//            ll_show.setBackgroundColor(Color.RED);

//            holder.setText(R.id.tv_log, imagePiece.log + "");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
