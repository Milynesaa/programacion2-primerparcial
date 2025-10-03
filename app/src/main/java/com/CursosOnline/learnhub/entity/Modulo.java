package com.CursosOnline.learnhub.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "modulos",
        foreignKeys = @ForeignKey(entity = Curso.class, parentColumns = "id", childColumns = "cursoId"),
        indices = {@Index("cursoId")})
public class Modulo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int cursoId;
    private String titulo;
    private int orden;

    public Modulo(int cursoId, String titulo, int orden) {
        this.cursoId = cursoId;
        this.titulo = titulo;
        this.orden = orden;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCursoId() { return cursoId; }
    public void setCursoId(int cursoId) { this.cursoId = cursoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}

