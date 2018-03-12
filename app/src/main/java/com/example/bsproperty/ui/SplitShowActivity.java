package com.example.bsproperty.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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
        rvList.setLayoutManager(new GridLayoutManager(this, 8));
        rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 10;
                outRect.bottom = 10;
            }
        });
    }

    @Override
    protected int getRootViewId() {
        return R.layout.activity_split_show;
    }

    @Override
    protected void loadData() {
//        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("path"));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.test);

        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            int clr = pixels[i];
            int red = (clr & 0x00ff0000) >> 16;  //取高两位
            int green = (clr & 0x0000ff00) >> 8; //取中两位
            int blue = clr & 0x000000ff; //取低两位
//            System.out.println("index="+i+"r=" + red + ",g=" + green + ",b=" + blue);

        }


        Double[] doubles = Logistic.getKey();
        List<ImagePiece> mItemBitmaps = ImageSplitter.split(bitmap, 8);

        for (int i = 0; i < mItemBitmaps.size(); i++) {
            mItemBitmaps.get(i).log = doubles[i];
        }

        MyAdapter adapter = new MyAdapter(this, R.layout.item_show_split, (ArrayList<ImagePiece>) mItemBitmaps);
        rvList.setAdapter(adapter);
//                    Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
//                        @Override
//                        public int compare(ImagePiece lhs, ImagePiece rhs) {
//                            return Math.random() > 0.5 ? 1 : -1;
//                        }
//                    });
    }

    private class MyAdapter extends BaseAdapter<ImagePiece> {

        public MyAdapter(Context context, int layoutId, ArrayList<ImagePiece> data) {
            super(context, layoutId, data);
        }

        @Override
        public void initItemView(BaseViewHolder holder, ImagePiece imagePiece, int position) {
            ImageView imageView = (ImageView) holder.getView(R.id.iv_show);
            imageView.setImageBitmap(imagePiece.bitmap);
            holder.setText(R.id.tv_log, imagePiece.log + "");
        }
    }
}
