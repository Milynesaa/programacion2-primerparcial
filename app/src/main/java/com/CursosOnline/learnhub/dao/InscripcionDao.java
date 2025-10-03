package com.CursosOnline.learnhub.dao;

import androidx.room.*;
import com.CursosOnline.learnhub.entity.Inscripcion;
import java.util.List;

@Dao
public interface InscripcionDao {
    @Insert
    void insert(Inscripcion inscripcion);

    @Delete
    void delete(Inscripcion inscripcion);

    @Query("SELECT * FROM inscripciones")
    List<Inscripcion> getAll();
}