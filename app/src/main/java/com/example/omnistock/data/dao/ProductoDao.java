package com.example.omnistock.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.omnistock.data.entity.Producto;
import java.util.List;

@Dao
public interface ProductoDao {

    @Insert
    void insertar(Producto producto);

    @Update
    void actualizar(Producto producto);

    @Delete
    void eliminar(Producto producto);

    @Query("SELECT * FROM productos")
    List<Producto> obtenerTodos();

    // Consulta con relación 1:N para filtrar productos por categoría específica[cite: 2]
    @Query("SELECT * FROM productos WHERE categoria_id = :categoriaId")
    List<Producto> obtenerPorCategoria(int categoriaId);

    // Actualización automática del stock tras finalizar una venta[cite: 2]
    @Query("UPDATE productos SET stock = :nuevoStock WHERE id = :productoId")
    void actualizarStock(int productoId, int nuevoStock);
}
