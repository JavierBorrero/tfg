package com.example.tfg.utils.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tfg.AllAnunciosFragment;
import com.example.tfg.MisAnunciosFragment;

public class AnunciosViewPagerAdapter extends FragmentStateAdapter {
    
    public AnunciosViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0){
            return new AllAnunciosFragment();
        }else{
            return new MisAnunciosFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
