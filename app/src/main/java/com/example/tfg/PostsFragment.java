package com.example.tfg;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tfg.databinding.FragmentPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.PostAdapter;
import com.example.tfg.utils.ViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {
    
    FragmentPostsBinding binding;
    ViewAdapter viewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        
        setupViewPager();
        
        return binding.getRoot();
    }
    
    private void setupViewPager(){
        viewAdapter = new ViewAdapter(this);
        viewAdapter.addFragment(new AllPostsFragment(), "Todos los Posts");
        viewAdapter.addFragment(new MyPostsFragment(), "Mis Posts");
        
        binding.viewPager.setAdapter(viewAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(viewAdapter.getPageTitle(position))).attach();
    }
}