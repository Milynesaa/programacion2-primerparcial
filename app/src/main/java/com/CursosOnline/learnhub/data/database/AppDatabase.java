package com.CursosOnline.learnhub.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.CursosOnline.learnhub.dao.CalificacionDao;
import com.CursosOnline.learnhub.dao.CursoDao;
import com.CursosOnline.learnhub.dao.InscripcionDao;
import com.CursosOnline.learnhub.dao.ModuloDao;
import com.CursosOnline.learnhub.dao.UsuarioDao;
import com.CursosOnline.learnhub.entity.Calificacion;
import com.CursosOnline.learnhub.entity.Curso;
import com.CursosOnline.learnhub.entity.Inscripcion;
import com.CursosOnline.learnhub.entity.Modulo;
import com.CursosOnline.learnhub.entity.Usuario;

@Database(entities = {Usuario.class, Curso.class, Inscripcion.class, Modulo.class, Calificacion.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UsuarioDao usuarioDao();
    public abstract CursoDao cursoDao();
    public abstract InscripcionDao inscripcionDao();
    public abstract ModuloDao moduloDao();
    public abstract CalificacionDao calificacionDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "learnhub_db")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}