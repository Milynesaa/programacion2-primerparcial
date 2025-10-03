package com.CursosOnline.learnhub.dao;

import androidx.room.*;
import com.CursosOnline.learnhub.entity.Usuario;
import java.util.List;

    @Dao
    public interface UsuarioDao {
        @Insert
        long insert(Usuario usuario);

        @Update
        void update(Usuario usuario);

        @Delete
        void delete(Usuario usuario);

        @Query("SELECT * FROM usuarios")
        List<Usuario> getAll();
    }



