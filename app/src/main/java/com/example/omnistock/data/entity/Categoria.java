package com.example.omnistock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categorias")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre_categoria")
    public String nombreCategoria;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    public Categoria(String nombreCategoria, String descripcion) {
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getNombreCategoria() { return nombreCategoria; }
    public String getDescripcion() { return descripcion; }

    // Necesario para mostrar el nombre de la categoría en los Spinners o Adapters
    @Override
    public String toString() {
        return nombreCategoria;
    }
}