package com.CursosOnline.learnhub.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CursosOnline.learnhub.entity.Inscripcion;
import com.example.CursosOnline.databinding.ItemInscripcionBinding;
import java.util.ArrayList;
import java.util.List;

public class InscripcionAdapter extends RecyclerView.Adapter<InscripcionAdapter.ViewHolder> {

    private List<InscripcionConDetalles> inscripciones = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDelete(Inscripcion inscripcion);
        void onClick(Inscripcion inscripcion);
    }

    public static class InscripcionConDetalles {
        public Inscripcion inscripcion;
        public String nombreUsuario;
        public String tituloCurso;

        public InscripcionConDetalles(Inscripcion inscripcion, String nombreUsuario, String tituloCurso) {
            this.inscripcion = inscripcion;
            this.nombreUsuario = nombreUsuario;
            this.tituloCurso = tituloCurso;
        }
    }

    public InscripcionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInscripcionBinding binding = ItemInscripcionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(inscripciones.get(position));
    }

    @Override
    public int getItemCount() {
        return inscripciones.size();
    }

    public void setInscripciones(List<InscripcionConDetalles> inscripciones) {
        this.inscripciones = inscripciones;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemInscripcionBinding binding;

        ViewHolder(ItemInscripcionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InscripcionConDetalles item) {
            binding.tvUsuario.setText(item.nombreUsuario);
            binding.tvCurso.setText(item.tituloCurso);
            binding.tvFecha.setText("Fecha: " + item.inscripcion.getFecha());

            binding.btnEliminar.setOnClickListener(v -> listener.onDelete(item.inscripcion));
            binding.getRoot().setOnClickListener(v -> listener.onClick(item.inscripcion));
        }
    }
}
