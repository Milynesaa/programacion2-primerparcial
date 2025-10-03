package com.CursosOnline.learnhub.ui.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CursosOnline.learnhub.entity.Modulo;
import com.example.CursosOnline.databinding.ItemModuloBinding;
import java.util.ArrayList;
import java.util.List;

public class ModuloAdapter extends RecyclerView.Adapter<ModuloAdapter.ViewHolder> {

    private List<ModuloConDetalles> modulos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Modulo modulo);
        void onDelete(Modulo modulo);
    }

    public static class ModuloConDetalles {
        public Modulo modulo;
        public String tituloCurso;

        public ModuloConDetalles(Modulo modulo, String tituloCurso) {
            this.modulo = modulo;
            this.tituloCurso = tituloCurso;
        }
    }

    public ModuloAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemModuloBinding binding = ItemModuloBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(modulos.get(position));
    }

    @Override
    public int getItemCount() {
        return modulos.size();
    }

    public void setModulos(List<ModuloConDetalles> modulos) {
        this.modulos = modulos;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemModuloBinding binding;

        ViewHolder(ItemModuloBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ModuloConDetalles item) {
            binding.tvTitulo.setText(item.modulo.getTitulo());
            binding.tvCurso.setText("Curso: " + item.tituloCurso);
            binding.tvOrden.setText("Orden: " + item.modulo.getOrden());

            binding.btnEditar.setOnClickListener(v -> listener.onEdit(item.modulo));
            binding.btnEliminar.setOnClickListener(v -> listener.onDelete(item.modulo));
        }
    }
}