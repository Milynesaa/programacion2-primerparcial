package com.CursosOnline.learnhub.dao;

import androidx.room.*;
import com.CursosOnline.learnhub.entity.Curso;
import java.util.List;

@Dao
public interface CursoDao {
    @Insert
    long insert(Curso curso);

    @Update
    void update(Curso curso);

    @Delete
    void delete(Curso curso);

    @Query("SELECT * FROM cursos")
    List<Curso> getAll();
}
