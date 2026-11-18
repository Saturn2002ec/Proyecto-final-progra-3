package com.example.omnistock.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.omnistock.data.entity.Categoria;
import java.util.List;

@Dao
public interface CategoriaDao {

    @Insert
    void insertar(Categoria categoria);

    @Query("SELECT * FROM categorias")
    List<Categoria> obtenerTodas();

    @Delete
    void eliminar(Categoria categoria);
}
