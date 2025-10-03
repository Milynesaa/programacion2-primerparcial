package com.CursosOnline.learnhub.ui.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CursosOnline.learnhub.entity.Calificacion;
import com.example.CursosOnline.databinding.ItemCalificacionBinding;
import java.util.ArrayList;
import java.util.List;

public class CalificacionAdapter extends RecyclerView.Adapter<CalificacionAdapter.ViewHolder> {

    private List<CalificacionConDetalles> calificaciones = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Calificacion calificacion);
        void onDelete(Calificacion calificacion);
    }

    public static class CalificacionConDetalles {
        public Calificacion calificacion;
        public String nombreUsuario;
        public String tituloModulo;

        public CalificacionConDetalles(Calificacion calificacion, String nombreUsuario, String tituloModulo) {
            this.calificacion = calificacion;
            this.nombreUsuario = nombreUsuario;
            this.tituloModulo = tituloModulo;
        }
    }

    public CalificacionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalificacionBinding binding = ItemCalificacionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(calificaciones.get(position));
    }

    @Override
    public int getItemCount() {
        return calificaciones.size();
    }

    public void setCalificaciones(List<CalificacionConDetalles> calificaciones) {
        this.calificaciones = calificaciones;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCalificacionBinding binding;

        ViewHolder(ItemCalificacionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CalificacionConDetalles item) {
            binding.tvUsuario.setText(item.nombreUsuario);
            binding.tvModulo.setText("Módulo: " + item.tituloModulo);
            binding.tvNota.setText("Nota: " + String.format("%.2f", item.calificacion.getNota()));

            binding.btnEditar.setOnClickListener(v -> listener.onEdit(item.calificacion));
            binding.btnEliminar.setOnClickListener(v -> listener.onDelete(item.calificacion));
        }
    }
}