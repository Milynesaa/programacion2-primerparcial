package com.CursosOnline.learnhub.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "calificaciones",
        foreignKeys = {
                @ForeignKey(entity = Usuario.class, parentColumns = "id", childColumns = "usuarioId"),
                @ForeignKey(entity = Modulo.class, parentColumns = "id", childColumns = "moduloId")
        },
        indices = {@Index("usuarioId"), @Index("moduloId")})
public class Calificacion {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int usuarioId;
    private int moduloId;
    private float nota;

    public Calificacion(int usuarioId, int moduloId, float nota) {
        this.usuarioId = usuarioId;
        this.moduloId = moduloId;
        this.nota = nota;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getModuloId() { return moduloId; }
    public void setModuloId(int moduloId) { this.moduloId = moduloId; }
    public float getNota() { return nota; }
    public void setNota(float nota) { this.nota = nota; }
}