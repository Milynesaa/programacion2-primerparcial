package com.CursosOnline.learnhub.dao;

import androidx.room.*;
import com.CursosOnline.learnhub.entity.Modulo;
import java.util.List;

@Dao
public interface ModuloDao {
    @Insert
    void insert(Modulo modulo);

    @Update
    void update(Modulo modulo);

    @Delete
    void delete(Modulo modulo);

    @Query("SELECT * FROM modulos WHERE cursoId = :cursoId")
    List<Modulo> getByCurso(int cursoId);
}