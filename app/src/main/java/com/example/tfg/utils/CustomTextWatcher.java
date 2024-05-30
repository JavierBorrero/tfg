package com.example.tfg.utils;

import android.text.Editable;
import android.text.TextWatcher;

public class CustomTextWatcher implements TextWatcher {
    
    private final Runnable callback;
    
    public CustomTextWatcher(Runnable callback){
        this.callback = callback;
    }
    
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        callback.run();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
