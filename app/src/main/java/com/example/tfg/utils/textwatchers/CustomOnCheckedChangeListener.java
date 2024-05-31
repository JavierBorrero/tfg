package com.example.tfg.utils.textwatchers;

import android.widget.CompoundButton;

public class CustomOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener{
    
    private final Runnable callback;
    
    public CustomOnCheckedChangeListener (Runnable callback){
        this.callback = callback;
    }
    
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        callback.run();
    }
}
