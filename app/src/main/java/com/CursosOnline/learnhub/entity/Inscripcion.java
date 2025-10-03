package com.CursosOnline.learnhub.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.CursosOnline.learnhub.entity.Curso;
import com.CursosOnline.learnhub.entity.Usuario;

@Entity(tableName = "inscripciones",
        foreignKeys = {
                @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "usuarioId"),
                @ForeignKey(entity = Curso.class, parentColumns = "id", childColumns = "cursoId")
        },
        indices = {@Index("usuarioId"), @Index("cursoId")})
public class Inscripcion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int usuarioId;
    private int cursoId;
    private String fecha;

    public Inscripcion(int usuarioId, int cursoId, String fecha) {
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.fecha = fecha;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}