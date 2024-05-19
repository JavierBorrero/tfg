package com.example.tfg;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.tfg.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    
    private final List<Integer> fragmentsToHideBottomNavView = Arrays.asList(
            R.id.signinfragment,
            R.id.signupfragment,
            R.id.editprofilefragment
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
                } else if (itemId == R.id.accountfragment) {
                    goToFragment(new AccountFragment(), R.id.accountfragment);
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
        
        /*binding.botNavView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.postsfragment){
                loadFragment(new PostsFragment());
            }
        });*/
        
        
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.botNavView, navController);

        // Array de fragemnts para no mostrar el botNavView
        List<Integer> hideBotNavViewList = Arrays.asList(
                R.id.signinfragment,
                R.id.signupfragment,
                R.id.editprofilefragment
        );
        
        getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if(event == Lifecycle.Event.ON_RESUME){
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if(hideBotNavViewList.contains(destination.getId())){
                        binding.botNavView.setVisibility(View.GONE);
                    }else{
                        binding.botNavView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });*/
    }

    public void goToFragment(Fragment fragment, int fragmentId){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
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