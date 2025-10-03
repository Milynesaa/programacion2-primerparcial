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
import com.CursosOnline.learnhub.ui.adapter.UsuarioAdapter;
import com.CursosOnline.learnhub.entity.Usuario;
import com.example.CursosOnline.databinding.DialogUsuarioBinding;
import com.example.CursosOnline.databinding.FragmentUsuariosBinding;
import java.util.List;

public class UsuariosFragment extends Fragment implements UsuarioAdapter.OnItemClickListener {

    private FragmentUsuariosBinding binding;
    private AppDatabase db;
    private UsuarioAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new UsuarioAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        cargarUsuarios();

        binding.fabAdd.setOnClickListener(v -> mostrarDialog(null));
    }

    private void cargarUsuarios() {
        new Thread(() -> {
            List<Usuario> usuarios = db.usuarioDao().getAll();
            requireActivity().runOnUiThread(() -> adapter.setUsuarios(usuarios));
        }).start();
    }

    private void mostrarDialog(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogUsuarioBinding dialogBinding = DialogUsuarioBinding.inflate(getLayoutInflater());

        if (usuario != null) {
            dialogBinding.etNombre.setText(usuario.getNombre());
            dialogBinding.etEmail.setText(usuario.getEmail());
            dialogBinding.etTelefono.setText(usuario.getTelefono());
        }

        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        dialogBinding.btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnGuardar.setOnClickListener(v -> {
            String nombre = dialogBinding.etNombre.getText().toString().trim();
            String email = dialogBinding.etEmail.getText().toString().trim();
            String telefono = dialogBinding.etTelefono.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                if (usuario == null) {
                    String fechaActual = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
                    Usuario nuevo = new Usuario(nombre, "", email, telefono, fechaActual);
                    db.usuarioDao().insert(nuevo);
                } else {
                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    usuario.setTelefono(telefono);
                    db.usuarioDao().update(usuario);
                }

                requireActivity().runOnUiThread(() -> {
                    cargarUsuarios();
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Usuario guardado", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        dialog.show();
    }

    @Override
    public void onEdit(Usuario usuario) {
        mostrarDialog(usuario);
    }

    @Override
    public void onDelete(Usuario usuario) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar")
                .setMessage("¿Eliminar este usuario?")
                .setPositiveButton("Sí", (d, w) -> {
                    new Thread(() -> {
                        db.usuarioDao().delete(usuario);
                        requireActivity().runOnUiThread(() -> {
                            cargarUsuarios();
                            Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
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
