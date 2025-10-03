package com.CursosOnline.learnhub.ui.fragments;



import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.CursosOnline.learnhub.data.database.AppDatabase;
import com.CursosOnline.learnhub.entity.Curso;
import com.CursosOnline.learnhub.ui.adapter.CursoAdapter;
import com.example.CursosOnline.databinding.DialogCursoBinding;
import com.example.CursosOnline.databinding.FragmentCursosBinding;
import java.util.List;

public class CursoFragment extends Fragment implements CursoAdapter.OnItemClickListener {

    private FragmentCursosBinding binding;
    private AppDatabase db;
    private CursoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCursosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new CursoAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        cargarCursos();

        binding.fabAdd.setOnClickListener(v -> mostrarDialog(null));
    }

    private void cargarCursos() {
        new Thread(() -> {
            List<Curso> cursos = db.cursoDao().getAll();
            requireActivity().runOnUiThread(() -> adapter.setCursos(cursos));
        }).start();
    }

    private void mostrarDialog(Curso curso) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogCursoBinding dialogBinding = DialogCursoBinding.inflate(getLayoutInflater());

        if (curso != null) {
            dialogBinding.etTitulo.setText(curso.getTitulo());
            dialogBinding.etInstructor.setText(curso.getInstructor());
            dialogBinding.etDuracion.setText(String.valueOf(curso.getDuracionHoras()));
        }

        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnGuardar.setOnClickListener(v -> {
            String titulo = dialogBinding.etTitulo.getText().toString().trim();
            String instructor = dialogBinding.etInstructor.getText().toString().trim();
            String duracionStr = dialogBinding.etDuracion.getText().toString().trim();

            if (titulo.isEmpty() || instructor.isEmpty() || duracionStr.isEmpty()) {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            int duracion = Integer.parseInt(duracionStr);

            new Thread(() -> {
                if (curso == null) {
                    Curso nuevo = new Curso(titulo, instructor, duracion);
                    db.cursoDao().insert(nuevo);
                } else {
                    curso.setTitulo(titulo);
                    curso.setInstructor(instructor);
                    curso.setDuracionHoras(duracion);
                    db.cursoDao().update(curso);
                }

                requireActivity().runOnUiThread(() -> {
                    cargarCursos();
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Curso guardado", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        dialog.show();
    }

    @Override
    public void onEdit(Curso curso) {
        mostrarDialog(curso);
    }

    @Override
    public void onDelete(Curso curso) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar este curso?")
                .setPositiveButton("Sí", (d, w) -> {
                    new Thread(() -> {
                        db.cursoDao().delete(curso);
                        requireActivity().runOnUiThread(() -> {
                            cargarCursos();
                            Toast.makeText(requireContext(), "Curso eliminado", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}