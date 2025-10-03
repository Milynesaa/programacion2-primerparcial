package com.CursosOnline.learnhub.dao;

import androidx.room.*;
import com.CursosOnline.learnhub.entity.Calificacion;
import java.util.List;

@Dao
public interface CalificacionDao {
    @Insert
    void insert(Calificacion calificacion);

    @Update
    void update(Calificacion calificacion);

    @Delete
    void delete(Calificacion calificacion);

    @Query("SELECT * FROM calificaciones WHERE usuarioId = :usuarioId")
    List<Calificacion> getByUsuario(int usuarioId);
}