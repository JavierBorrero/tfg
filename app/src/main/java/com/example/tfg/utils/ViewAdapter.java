package com.example.tfg.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.MyPostsFragment;
import com.example.tfg.PostsFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewAdapter extends FragmentStateAdapter {
    
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    
    public ViewAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position){
        return fragmentList.get(position);
    }
    
    @Override
    public int getItemCount(){
        return fragmentList.size();
    }
    
    public CharSequence getPageTitle(int position){
        return fragmentTitleList.get(position);
    }
}
