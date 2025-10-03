package com.CursosOnline.learnhub.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CursosOnline.learnhub.entity.Usuario;
import com.example.CursosOnline.databinding.ItemUsuarioBinding;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private List<Usuario> usuarios = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Usuario usuario);
        void onDelete(Usuario usuario);
    }

    public UsuarioAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUsuarioBinding binding = ItemUsuarioBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(usuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemUsuarioBinding binding;

        ViewHolder(ItemUsuarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Usuario usuario) {
            binding.tvNombre.setText(usuario.getNombre());
            binding.tvEmail.setText(usuario.getEmail());
            binding.btnEditar.setOnClickListener(v -> listener.onEdit(usuario));
            binding.btnEliminar.setOnClickListener(v -> listener.onDelete(usuario));
        }
    }
}