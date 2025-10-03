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
import com.CursosOnline.learnhub.entity.Inscripcion;
import com.CursosOnline.learnhub.entity.Usuario;
import com.CursosOnline.learnhub.ui.adapter.InscripcionAdapter;
import com.example.CursosOnline.databinding.DialogInscripcionBinding;
import com.example.CursosOnline.databinding.FragmentInscripcionesBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InscripcionesFragment extends Fragment implements InscripcionAdapter.OnItemClickListener {

    private FragmentInscripcionesBinding binding;
    private AppDatabase db;
    private InscripcionAdapter adapter;
    private List<Usuario> listaUsuarios;
    private List<Curso> listaCursos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInscripcionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new InscripcionAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        cargarInscripciones();

        binding.fabAdd.setOnClickListener(v -> mostrarDialog());
    }

    private void cargarInscripciones() {
        new Thread(() -> {
            List<Inscripcion> inscripciones = db.inscripcionDao().getAll();
            List<InscripcionAdapter.InscripcionConDetalles> detalles = new ArrayList<>();

            for (Inscripcion insc : inscripciones) {
                Usuario usuario = db.usuarioDao().getAll().stream()
                        .filter(u -> u.getId() == insc.getUsuarioId())
                        .findFirst()
                        .orElse(null);

                Curso curso = db.cursoDao().getAll().stream()
                        .filter(c -> c.getId() == insc.getCursoId())
                        .findFirst()
                        .orElse(null);

                String nombreUsuario = usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "Desconocido";
                String tituloCurso = curso != null ? curso.getTitulo() : "Desconocido";

                detalles.add(new InscripcionAdapter.InscripcionConDetalles(insc, nombreUsuario, tituloCurso));
            }

            requireActivity().runOnUiThread(() -> adapter.setInscripciones(detalles));
        }).start();
    }

    private void mostrarDialog() {
        new Thread(() -> {
            listaUsuarios = db.usuarioDao().getAll();
            listaCursos = db.cursoDao().getAll();

            requireActivity().runOnUiThread(() -> {
                if (listaUsuarios.isEmpty() || listaCursos.isEmpty()) {
                    Toast.makeText(requireContext(), "Debe haber usuarios y cursos registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                DialogInscripcionBinding dialogBinding = DialogInscripcionBinding.inflate(getLayoutInflater());

                // Configurar spinner de usuarios
                List<String> nombresUsuarios = new ArrayList<>();
                for (Usuario u : listaUsuarios) {
                    nombresUsuarios.add(u.getNombre() + " " + u.getApellido());
                }
                ArrayAdapter<String> usuarioAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresUsuarios);
                usuarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerUsuario.setAdapter(usuarioAdapter);

                // Configurar spinner de cursos
                List<String> titulosCursos = new ArrayList<>();
                for (Curso c : listaCursos) {
                    titulosCursos.add(c.getTitulo());
                }
                ArrayAdapter<String> cursoAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, titulosCursos);
                cursoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerCurso.setAdapter(cursoAdapter);

                builder.setView(dialogBinding.getRoot());
                AlertDialog dialog = builder.create();

                dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

                dialogBinding.btnGuardar.setOnClickListener(v -> {
                    int posUsuario = dialogBinding.spinnerUsuario.getSelectedItemPosition();
                    int posCurso = dialogBinding.spinnerCurso.getSelectedItemPosition();

                    int usuarioId = listaUsuarios.get(posUsuario).getId();
                    int cursoId = listaCursos.get(posCurso).getId();

                    String fechaActual = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                    new Thread(() -> {
                        Inscripcion nueva = new Inscripcion(usuarioId, cursoId, fechaActual);
                        db.inscripcionDao().insert(nueva);

                        requireActivity().runOnUiThread(() -> {
                            cargarInscripciones();
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Inscripción guardada", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                });

                dialog.show();
            });
        }).start();
    }

    @Override
    public void onClick(Inscripcion inscripcion) {
        // Opcional: Mostrar detalles
    }

    @Override
    public void onDelete(Inscripcion inscripcion) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar esta inscripción?")
                .setPositiveButton("Sí", (d, w) -> {
                    new Thread(() -> {
                        db.inscripcionDao().delete(inscripcion);
                        requireActivity().runOnUiThread(() -> {
                            cargarInscripciones();
                            Toast.makeText(requireContext(), "Inscripción eliminada", Toast.LENGTH_SHORT).show();
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