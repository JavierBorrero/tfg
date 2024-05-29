package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentAnunciosBinding;
import com.example.tfg.utils.AnunciosViewPagerAdapter;
import com.example.tfg.utils.PostsViewPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class AnunciosFragment extends Fragment {
    
    FragmentAnunciosBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnunciosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AnunciosViewPagerAdapter adapter = new AnunciosViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Todos los anuncios");
                    break;
                    
                case 1:
                    tab.setText("Mis anuncios");
                    break;
            }
        }).attach();
    }
}