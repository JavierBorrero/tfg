package com.example.tfg;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentEditAnuncioBinding;


public class EditAnuncioFragment extends Fragment {
    
    FragmentEditAnuncioBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditAnuncioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}