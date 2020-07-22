package com.omarproject1.shashah.intro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.omarproject1.shashah.R;
import com.omarproject1.shashah.intro.IntroActivity;
import com.omarproject1.shashah.model.IntroScreenItem;

import java.util.List;

public class IntroAdapter extends PagerAdapter {

    List<IntroScreenItem> introScreenItemList;
    Context context;

    public IntroAdapter() {
    }

    public IntroAdapter(Context context, List<IntroScreenItem> introScreenItemList) {
        this.context = context;
        this.introScreenItemList = introScreenItemList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen=inflater.inflate(R.layout.intro_screen_pager,null);

        ImageView viewPagerImage=layoutScreen.findViewById(R.id.intro_image);
        TextView viewPagerTitle=layoutScreen.findViewById(R.id.intro_title);
        TextView viewPagerDecrip=layoutScreen.findViewById(R.id.intro_description);

        viewPagerImage.setImageResource(introScreenItemList.get(position).getIntroImage());
        viewPagerTitle.setText(introScreenItemList.get(position).getIntroTitle());
        viewPagerDecrip.setText(introScreenItemList.get(position).getIntroDescription());
        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return introScreenItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}
