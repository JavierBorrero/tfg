package com.example.tfg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tfg.databinding.FragmentPostsBinding;
import com.example.tfg.utils.adapters.PostsViewPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class PostsFragment extends Fragment {

    /*
        === POSTS FRAGMENT ===
        Esta clase se encarga de contener el TabLayout y el ViewPager para la muestra
        de los otros fragmentos
     */
    
    FragmentPostsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Objeto del adapter para el PostViewPager y se pone el adapter
        PostsViewPagerAdapter adapter = new PostsViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Se crea el TabLayoutMediator para separar en Todos los Posts y Mis Posts
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Todos los Posts");
                    break;
                case 1:
                    tab.setText("Mis Posts");
                    break;
            }
        }).attach();
    }
}