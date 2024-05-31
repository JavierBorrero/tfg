package com.example.tfg.utils.adapters;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tfg.R;
import com.example.tfg.models.Usuario;

public class UserViewHolder extends RecyclerView.ViewHolder {
    
    ImageView usuarioPfp;
    TextView usuarioNombre;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        usuarioPfp = itemView.findViewById(R.id.userPhoto);
        usuarioNombre = itemView.findViewById(R.id.userName);
    }

    public void bind(Usuario usuario, UserAdapter.OnUserLongClickListener listener, UserAdapter.OnItemClickListener onItemClickListener) {
        usuarioNombre.setText(usuario.getNombre());
        String imagePfpUrl = usuario.getImagePfpUrl();

        if (imagePfpUrl != null && !imagePfpUrl.isEmpty()) {
            Uri uri = Uri.parse(imagePfpUrl);
            Glide.with(usuarioPfp.getContext()).load(uri).into(usuarioPfp);
        } else {
            usuarioPfp.setImageResource(R.drawable.icon_account);
        }

        itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onUserLongClick((Usuario) v.getTag(), position); // Utilizar la etiqueta asociada
                    return true;
                }
            }
            return false;
        });
        
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(usuario);
            }
        });
    }
}
