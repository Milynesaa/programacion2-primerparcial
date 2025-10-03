package com.CursosOnline.learnhub.ui.adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.CursosOnline.learnhub.entity.Curso;
import com.example.CursosOnline.databinding.ItemCursoBinding;
import java.util.ArrayList;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.ViewHolder> {

    private List<Curso> cursos = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(Curso curso);
        void onDelete(Curso curso);
    }

    public CursoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCursoBinding binding = ItemCursoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(cursos.get(position));
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemCursoBinding binding;

        ViewHolder(ItemCursoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Curso curso) {
            binding.tvTitulo.setText(curso.getTitulo());
            binding.tvInstructor.setText("Instructor: " + curso.getInstructor());

            binding.btnEditar.setOnClickListener(v -> listener.onEdit(curso));
            binding.btnEliminar.setOnClickListener(v -> listener.onDelete(curso));
        }
    }
}