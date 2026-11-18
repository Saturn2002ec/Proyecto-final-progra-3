package com.example.omnistock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "pass")
    public String pass;

    @ColumnInfo(name = "rol")
    public String rol; // "ADMIN" o "VENDEDOR"

    public Usuario(String nombre, String pass, String rol) {
        this.nombre = nombre;
        this.pass = pass;
        this.rol = rol;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getPass() { return pass; }
    public String getRol() { return rol; }
}