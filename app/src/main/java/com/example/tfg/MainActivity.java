package com.example.tfg;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tfg.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    
    private final List<Integer> fragmentsToHideBottomNavView = Arrays.asList(
            R.id.signinfragment,
            R.id.signupfragment,
            R.id.editprofilefragment,
            R.id.forgotpasswordfragment
    );
    
    private boolean isNavigationEnabled = true;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseApp.initializeApp(MainActivity.this);
        
        binding.botNavView.setOnItemSelectedListener(item -> {
            if(isNavigationEnabled){
                isNavigationEnabled = false;
                
                int itemId = item.getItemId();
                
                if(itemId == R.id.postsfragment){
                    goToFragment(new PostsFragment(), R.id.postsfragment);
                }else if (itemId == R.id.searchfragment) {
                    goToFragment(new SearchFragment(), R.id.searchfragment);
                } else if (itemId == R.id.accountfragment) {
                    goToFragment(new AccountFragment(), R.id.accountfragment);
                } else if (itemId == R.id.anunciosfragment) {
                    goToFragment(new AnunciosFragment(), R.id.anunciosfragment);
                }

                disableNavigationTemporarily();
                return true;
                
            }else{
                return false;
            }
        });
        
        goToFragment(new SignInFragment(), R.id.signinfragment);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressedHandler();
            }
        };
        
        getOnBackPressedDispatcher().addCallback(this, callback);
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goToFragment(Fragment fragment, int fragmentId){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
        );
        
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        
        updateBottomNavigationVisibility(fragmentId);
    }
    
    public void updateBottomNavigationVisibility(int fragmentId){
        if(fragmentsToHideBottomNavView.contains(fragmentId)){
            binding.botNavView.setVisibility(View.GONE);
        }else{
            binding.botNavView.setVisibility(View.VISIBLE);
        }
    }
    
    public void disableNavigationTemporarily(){
        binding.botNavView.postDelayed(() -> isNavigationEnabled = true, 500);
    }
    
    public void onBackPressedHandler(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() > 1){
            fragmentManager.popBackStack();
            
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
            if(currentFragment != null){
                updateBottomNavigationVisibility(currentFragment.getId());
            }
            
        }else{
            finish();
        }
    }
}