package com.example.tfg.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tfg.R;
import com.example.tfg.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private List<Usuario> userList;
    private String postUserId;
    private OnUserLongClickListener onUserLongClickListener;
    private OnItemClickListener onItemClickListener;
    
    public interface OnUserLongClickListener {
        void onUserLongClick(Usuario usuario, int position);
    }
    
    public interface OnItemClickListener {
        void onItemClick(Usuario usuario);
    }

    public UserAdapter(List<Usuario> userList, String postUserId, @Nullable OnUserLongClickListener onUserLongClickListener, OnItemClickListener onItemClickListener) {
        this.userList = userList;
        this.postUserId = postUserId;
        this.onUserLongClickListener = onUserLongClickListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Usuario usuario = userList.get(position);
        holder.bind(usuario, onUserLongClickListener,onItemClickListener);

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
