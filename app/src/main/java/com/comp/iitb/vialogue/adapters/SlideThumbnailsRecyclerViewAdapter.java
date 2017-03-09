package com.comp.iitb.vialogue.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.comp.iitb.vialogue.R;
import com.comp.iitb.vialogue.activity.AudioRecordActivity;
import com.comp.iitb.vialogue.coordinators.SharedRuntimeContent;
import com.comp.iitb.vialogue.models.ParseObjects.models.Slide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by ironstein on 09/03/17.
 */

public class SlideThumbnailsRecyclerViewAdapter extends RecyclerView.Adapter<SlideThumbnailsRecyclerViewAdapter.SlideViewHolder> {

    private Activity mActivity;
    private Context mContext = null;
    private int mCurrentSlidePosition;
    private ArrayList<byte []> mByteArrayList;
    private byte[] mDefaultImageByteArray;

    private boolean LOAD_SLIDES = false;

    public SlideThumbnailsRecyclerViewAdapter(Activity activity, Context context, int currentSlidePosition) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        mActivity = activity;
        mContext = context;
        mCurrentSlidePosition = currentSlidePosition;

        mByteArrayList = new ArrayList<byte[]>();
        for(Slide slide : SharedRuntimeContent.getAllSlides()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            slide.getThumbnail().compress(Bitmap.CompressFormat.PNG, 100, stream);
            mByteArrayList.add(stream.toByteArray());
        }
        LOAD_SLIDES = true;
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

    }

    public SlideThumbnailsRecyclerViewAdapter(Context context) {
        System.out.println("ccccccccccccccccccccccccccccccccccc");
        mContext = context;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo).compress(Bitmap.CompressFormat.PNG, 100, stream);
        mDefaultImageByteArray = stream.toByteArray();
        System.out.println("ddddddddddddddddddddddddddddddddddddd");
    }

    public class SlideViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public SlideViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View slideView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_thumbnail, parent, false);
        return new SlideViewHolder(slideView);
    }

    @Override
    public void onBindViewHolder(SlideViewHolder slideViewHolder, int position) {
        System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        Slide slide = SharedRuntimeContent.getSlideAt(position);

        if(!LOAD_SLIDES) {
            // use only dummy images everywhere
            Glide.with(mContext)
                    .fromBytes()
                    .load(mDefaultImageByteArray)
                    .placeholder(R.drawable.app_logo)
                    .into(slideViewHolder.thumbnail);
//            slideViewHolder.thumbnail.setImageResource(R.drawable.app_logo);
        } else {
            // use actual thumbnails from the slides
            Glide.with(mContext)
                    .fromBytes()
                    .load(mByteArrayList.get(position))
                    .placeholder(R.drawable.app_logo)
                    .into(slideViewHolder.thumbnail);

            if(mCurrentSlidePosition == position) {
                slideViewHolder.thumbnail.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.padding_slide_thumbnails);
                slideViewHolder.thumbnail.setPadding(padding, padding, padding, padding);
            }

            final int pos = position;
            if(slide.getSlideType() == Slide.SlideType.IMAGE) {
                if(position != mCurrentSlidePosition) {
                    // don't add onclickListener if the slide is the current slide itself
                    // (does not make sense loading this activity again for the same slide)
                    slideViewHolder.thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, AudioRecordActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(AudioRecordActivity.SLIDE_NO, pos);
                            intent.putExtras(bundle);
                            mActivity.finish();
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else {
                slideViewHolder.thumbnail.setColorFilter(Color.argb(200, 0, 0, 0)); // White Tint
            }
        }
        System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
    }

    @Override
    public int getItemCount() {
        return SharedRuntimeContent.getNumberOfSlides();
    }

}