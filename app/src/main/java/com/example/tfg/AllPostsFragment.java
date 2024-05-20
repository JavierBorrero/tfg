package com.example.tfg;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tfg.databinding.FragmentAllPostsBinding;
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

public class AllPostsFragment extends Fragment implements View.OnClickListener {
    
    FragmentAllPostsBinding binding;
    private FirebaseFirestore db;
    private List<Post> postList;
    private PostAdapter adapter;
    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();

        activity = (MainActivity) getActivity();

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
            activity.goToFragment(new NewPostFragment(), R.id.newpostfragment);
        }
    }
}