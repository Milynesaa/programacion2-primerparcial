package com.CursosOnline.learnhub.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

    @Entity(tableName = "cursos")
    public class Curso {
        @PrimaryKey(autoGenerate = true)
        private int id;
        private String titulo;
        private String instructor;
        private int duracionHoras;

        public Curso(String titulo, String instructor, int duracionHoras) {
            this.titulo = titulo;
            this.instructor = instructor;
            this.duracionHoras = duracionHoras;
        }

        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        public String getInstructor() { return instructor; }
        public void setInstructor(String instructor) { this.instructor = instructor; }
        public int getDuracionHoras() { return duracionHoras; }
        public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }
    }

