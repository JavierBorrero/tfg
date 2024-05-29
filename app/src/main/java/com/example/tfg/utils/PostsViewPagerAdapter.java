package com.example.tfg.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.AllPostsFragment;
import com.example.tfg.MyPostsFragment;

public class PostsViewPagerAdapter extends FragmentStateAdapter {
    
    public PostsViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new AllPostsFragment();
        }else{
            return new MyPostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
