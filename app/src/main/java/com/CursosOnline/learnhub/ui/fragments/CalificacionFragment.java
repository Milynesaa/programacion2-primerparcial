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
import com.CursosOnline.learnhub.entity.Calificacion;
import com.CursosOnline.learnhub.entity.Modulo;
import com.CursosOnline.learnhub.entity.Usuario;
import com.CursosOnline.learnhub.ui.adapter.CalificacionAdapter;
import com.example.CursosOnline.databinding.DialogCalificacionBinding;
import com.example.CursosOnline.databinding.FragmentCalificacionesBinding;
import java.util.ArrayList;
import java.util.List;

public class CalificacionFragment extends Fragment implements CalificacionAdapter.OnItemClickListener {

    private FragmentCalificacionesBinding binding;
    private AppDatabase db;
    private CalificacionAdapter adapter;
    private List<Usuario> listaUsuarios;
    private List<Modulo> listaModulos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCalificacionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new CalificacionAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        cargarCalificaciones();

        binding.fabAdd.setOnClickListener(v -> mostrarDialog(null));
    }

    private void cargarCalificaciones() {
        new Thread(() -> {
            List<Usuario> usuarios = db.usuarioDao().getAll();
            List<CalificacionAdapter.CalificacionConDetalles> detalles = new ArrayList<>();

            for (Usuario usuario : usuarios) {
                List<Calificacion> calificaciones = db.calificacionDao().getByUsuario(usuario.getId());

                for (Calificacion calif : calificaciones) {
                    Modulo modulo = null;
                    List<Modulo> todosModulos = obtenerTodosModulos();

                    for (Modulo m : todosModulos) {
                        if (m.getId() == calif.getModuloId()) {
                            modulo = m;
                            break;
                        }
                    }

                    String nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
                    String tituloModulo = modulo != null ? modulo.getTitulo() : "Desconocido";

                    detalles.add(new CalificacionAdapter.CalificacionConDetalles(calif, nombreUsuario, tituloModulo));
                }
            }

            requireActivity().runOnUiThread(() -> adapter.setCalificaciones(detalles));
        }).start();
    }

    private List<Modulo> obtenerTodosModulos() {
        List<Modulo> todosModulos = new ArrayList<>();
        List<com.CursosOnline.learnhub.entity.Curso> cursos = db.cursoDao().getAll();

        for (com.CursosOnline.learnhub.entity.Curso curso : cursos) {
            todosModulos.addAll(db.moduloDao().getByCurso(curso.getId()));
        }

        return todosModulos;
    }

    private void mostrarDialog(Calificacion calificacion) {
        new Thread(() -> {
            listaUsuarios = db.usuarioDao().getAll();
            listaModulos = obtenerTodosModulos();

            requireActivity().runOnUiThread(() -> {
                if (listaUsuarios.isEmpty() || listaModulos.isEmpty()) {
                    Toast.makeText(requireContext(), "Debe haber usuarios y módulos registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                DialogCalificacionBinding dialogBinding = DialogCalificacionBinding.inflate(getLayoutInflater());

                // Configurar spinner de usuarios
                List<String> nombresUsuarios = new ArrayList<>();
                for (Usuario u : listaUsuarios) {
                    nombresUsuarios.add(u.getNombre() + " " + u.getApellido());
                }
                ArrayAdapter<String> usuarioAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, nombresUsuarios);
                usuarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerUsuario.setAdapter(usuarioAdapter);

                // Configurar spinner de módulos
                List<String> titulosModulos = new ArrayList<>();
                for (Modulo m : listaModulos) {
                    titulosModulos.add(m.getTitulo());
                }
                ArrayAdapter<String> moduloAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, titulosModulos);
                moduloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerModulo.setAdapter(moduloAdapter);

                if (calificacion != null) {
                    dialogBinding.etNota.setText(String.valueOf(calificacion.getNota()));

                    // Seleccionar usuario y módulo correctos
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (listaUsuarios.get(i).getId() == calificacion.getUsuarioId()) {
                            dialogBinding.spinnerUsuario.setSelection(i);
                            break;
                        }
                    }

                    for (int i = 0; i < listaModulos.size(); i++) {
                        if (listaModulos.get(i).getId() == calificacion.getModuloId()) {
                            dialogBinding.spinnerModulo.setSelection(i);
                            break;
                        }
                    }
                }

                builder.setView(dialogBinding.getRoot());
                AlertDialog dialog = builder.create();

                dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

                dialogBinding.btnGuardar.setOnClickListener(v -> {
                    String notaStr = dialogBinding.etNota.getText().toString().trim();

                    if (notaStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float nota = Float.parseFloat(notaStr);

                    if (nota < 0 || nota > 10) {
                        Toast.makeText(requireContext(), "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int posUsuario = dialogBinding.spinnerUsuario.getSelectedItemPosition();
                    int posModulo = dialogBinding.spinnerModulo.getSelectedItemPosition();

                    int usuarioId = listaUsuarios.get(posUsuario).getId();
                    int moduloId = listaModulos.get(posModulo).getId();

                    new Thread(() -> {
                        if (calificacion == null) {
                            Calificacion nueva = new Calificacion(usuarioId, moduloId, nota);
                            db.calificacionDao().insert(nueva);
                        } else {
                            calificacion.setNota(nota);
                            calificacion.setUsuarioId(usuarioId);
                            calificacion.setModuloId(moduloId);
                            db.calificacionDao().update(calificacion);
                        }

                        requireActivity().runOnUiThread(() -> {
                            cargarCalificaciones();
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Calificación guardada", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                });

                dialog.show();
            });
        }).start();
    }

    @Override
    public void onEdit(Calificacion calificacion) {
        mostrarDialog(calificacion);
    }

    @Override
    public void onDelete(Calificacion calificacion) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar esta calificación?")
                .setPositiveButton("Sí", (d, w) -> {
                    new Thread(() -> {
                        db.calificacionDao().delete(calificacion);
                        requireActivity().runOnUiThread(() -> {
                            cargarCalificaciones();
                            Toast.makeText(requireContext(), "Calificación eliminada", Toast.LENGTH_SHORT).show();
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