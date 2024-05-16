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
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tfg.databinding.FragmentPostsBinding;
import com.example.tfg.models.Post;
import com.example.tfg.utils.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment implements View.OnClickListener {
    
    FragmentPostsBinding binding;
    private FirebaseFirestore db;
    private List<Post> postList;
    private PostAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        
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
        
        postList = new ArrayList<>();
        adapter = new PostAdapter(postList);
        
        binding.postsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.postsList.setAdapter(adapter);
        
        postsFromFirebase();
    }
    
    private void postsFromFirebase(){
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<Post> tempPostList = new ArrayList<>();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Post post = document.toObject(Post.class);
                                tempPostList.add(post);
                            }
                            fetchAuthorNames(tempPostList);
                        }else{
                            Log.w("PostsFragment","Error getting documents ", task.getException());
                        }
                    }
                });
    }

    private void fetchAuthorNames(List<Post> tempPostList) {
        for (Post post : tempPostList) {
            db.collection("usuarios").document(post.getUserId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot userDoc = task.getResult();
                                String authorName = userDoc.getString("nombre");
                                Log.d("PostsFragment", "Author name for userId " + post.getUserId() + ": " + authorName);
                                post.setNombreAutor(authorName); // Establecer el nombre del autor en el objeto Post
                            } else {
                                Log.w("PostsFragment", "Failed to get author name for userId " + post.getUserId());
                            }
                            postList.add(post);
                            if (postList.size() == tempPostList.size()) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        
        if(i == R.id.btnNewPost){
            NavHostFragment.findNavController(this).navigate(R.id.newpostfragment);
        }
    }
}