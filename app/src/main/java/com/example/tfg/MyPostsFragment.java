package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentMyPostsBinding;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment {

    FragmentMyPostsBinding binding;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}