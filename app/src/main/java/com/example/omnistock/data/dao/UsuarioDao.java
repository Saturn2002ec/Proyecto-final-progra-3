package com.example.omnistock.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.omnistock.data.entity.Usuario;

@Dao
public interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND pass = :pass LIMIT 1")
    Usuario login(String nombre, String pass);

    @Insert
    void insertar(Usuario usuario);
}