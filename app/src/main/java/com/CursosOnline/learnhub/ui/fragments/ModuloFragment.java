package com.CursosOnline.learnhub.ui.fragments;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.CursosOnline.learnhub.data.database.AppDatabase;
import com.CursosOnline.learnhub.entity.Curso;
import com.CursosOnline.learnhub.entity.Modulo;
import com.CursosOnline.learnhub.ui.adapter.ModuloAdapter;
import com.example.CursosOnline.databinding.DialogModuloBinding;
import com.example.CursosOnline.databinding.FragmentModulosBinding;
import java.util.ArrayList;
import java.util.List;

public class ModuloFragment extends Fragment implements ModuloAdapter.OnItemClickListener {

    private FragmentModulosBinding binding;
    private AppDatabase db;
    private ModuloAdapter adapter;
    private List<Curso> listaCursos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentModulosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new ModuloAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        cargarModulos();

        binding.fabAdd.setOnClickListener(v -> mostrarDialog(null));
    }

    private void cargarModulos() {
        new Thread(() -> {
            List<Curso> cursos = db.cursoDao().getAll();
            List<ModuloAdapter.ModuloConDetalles> detalles = new ArrayList<>();

            for (Curso curso : cursos) {
                List<Modulo> modulos = db.moduloDao().getByCurso(curso.getId());
                for (Modulo modulo : modulos) {
                    detalles.add(new ModuloAdapter.ModuloConDetalles(modulo, curso.getTitulo()));
                }
            }

            requireActivity().runOnUiThread(() -> adapter.setModulos(detalles));
        }).start();
    }

    private void mostrarDialog(Modulo modulo) {
        new Thread(() -> {
            listaCursos = db.cursoDao().getAll();

            requireActivity().runOnUiThread(() -> {
                if (listaCursos.isEmpty()) {
                    Toast.makeText(requireContext(), "Debe haber cursos registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                DialogModuloBinding dialogBinding = DialogModuloBinding.inflate(getLayoutInflater());

                // Configurar spinner de cursos
                List<String> titulosCursos = new ArrayList<>();
                for (Curso c : listaCursos) {
                    titulosCursos.add(c.getTitulo());
                }
                ArrayAdapter<String> cursoAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, titulosCursos);
                cursoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerCurso.setAdapter(cursoAdapter);

                if (modulo != null) {
                    dialogBinding.etTitulo.setText(modulo.getTitulo());
                    dialogBinding.etOrden.setText(String.valueOf(modulo.getOrden()));

                    // Seleccionar el curso correcto en el spinner
                    for (int i = 0; i < listaCursos.size(); i++) {
                        if (listaCursos.get(i).getId() == modulo.getCursoId()) {
                            dialogBinding.spinnerCurso.setSelection(i);
                            break;
                        }
                    }
                }

                builder.setView(dialogBinding.getRoot());
                AlertDialog dialog = builder.create();

                dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

                dialogBinding.btnGuardar.setOnClickListener(v -> {
                    String titulo = dialogBinding.etTitulo.getText().toString().trim();
                    String ordenStr = dialogBinding.etOrden.getText().toString().trim();

                    if (titulo.isEmpty() || ordenStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int orden = Integer.parseInt(ordenStr);
                    int posCurso = dialogBinding.spinnerCurso.getSelectedItemPosition();
                    int cursoId = listaCursos.get(posCurso).getId();

                    new Thread(() -> {
                        if (modulo == null) {
                            Modulo nuevo = new Modulo(cursoId, titulo, orden);
                            db.moduloDao().insert(nuevo);
                        } else {
                            modulo.setTitulo(titulo);
                            modulo.setOrden(orden);
                            modulo.setCursoId(cursoId);
                            db.moduloDao().update(modulo);
                        }

                        requireActivity().runOnUiThread(() -> {
                            cargarModulos();
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Módulo guardado", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                });

                dialog.show();
            });
        }).start();
    }

    @Override
    public void onEdit(Modulo modulo) {
        mostrarDialog(modulo);
    }

    @Override
    public void onDelete(Modulo modulo) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar este módulo?")
                .setPositiveButton("Sí", (d, w) -> {
                    new Thread(() -> {
                        db.moduloDao().delete(modulo);
                        requireActivity().runOnUiThread(() -> {
                            cargarModulos();
                            Toast.makeText(requireContext(), "Módulo eliminado", Toast.LENGTH_SHORT).show();
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