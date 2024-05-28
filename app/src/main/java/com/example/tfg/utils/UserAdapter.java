package com.example.tfg.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private List<Usuario> userList;
    private String postUserId;
    private OnUserLongClickListener onUserLongClickListener;

    public interface OnUserLongClickListener {
        void onUserLongClick(Usuario usuario, int position);
    }

    public UserAdapter(List<Usuario> userList, String postUserId, @Nullable OnUserLongClickListener onUserLongClickListener) {
        this.userList = userList;
        this.postUserId = postUserId;
        this.onUserLongClickListener = onUserLongClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view, onUserLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario usuario = userList.get(position);
        holder.bind(usuario);

        if (onUserLongClickListener != null && postUserId != null && postUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.itemView.setTag(usuario); // Asociar el usuario al itemView
            holder.itemView.setOnLongClickListener(v -> {
                onUserLongClickListener.onUserLongClick(usuario, position);
                return true;
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
