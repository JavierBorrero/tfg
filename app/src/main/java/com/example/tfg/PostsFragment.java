package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentPostsBinding;

public class PostsFragment extends Fragment implements View.OnClickListener {
    
    FragmentPostsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        
        getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if(event == Lifecycle.Event.ON_RESUME){
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if(!(destination.getId() == R.id.postsfragment)){
                        binding.btnNewPost.setVisibility(View.GONE);
                    }else{
                        binding.btnNewPost.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.btnNewPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.btnNewPost){
            NavHostFragment.findNavController(this).navigate(R.id.newpostfragment);
        }
    }
}