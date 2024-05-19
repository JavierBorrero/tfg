package com.example.tfg.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.MyPostsFragment;
import com.example.tfg.PostsFragment;

public class ViewAdapter extends FragmentStateAdapter {
    
    public ViewAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){
        if(position == 0){
            return new PostsFragment();
        }else{
            return new MyPostsFragment();
        }
    }
    
    @Override
    public int getItemCount(){
        return 2;
    }
}
