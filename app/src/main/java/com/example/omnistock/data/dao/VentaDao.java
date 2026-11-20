package com.example.omnistock.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import com.example.omnistock.data.entity.HistorialVentas;
import com.example.omnistock.model.ItemCarrito;
import java.util.List;

@Dao
public interface VentaDao {

    @Insert
    long insertarVenta(HistorialVentas venta);

    @Query("SELECT * FROM historial_ventas")
    List<HistorialVentas> obtenerHistorial();
    @Query("SELECT * FROM historial_ventas ORDER BY id DESC")
    LiveData<List<HistorialVentas>> obtenerTodasLasVentas();

    @Query("SELECT * FROM historial_ventas WHERE usuario_id = :usuarioId ORDER BY id DESC")
    LiveData<List<HistorialVentas>> obtenerVentasPorUsuario(int usuarioId);

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :productoId AND stock >= :cantidad")
    int descontarStock(int productoId, int cantidad);
    @Transaction
    default void procesarVentaCompleta(HistorialVentas venta, List<ItemCarrito> items) {
        for (ItemCarrito item : items) {
            int filasAfectadas = descontarStock(item.getProducto().getId(), item.getCantidad());
            if (filasAfectadas == 0) {
                throw new IllegalStateException("Stock insuficiente para: " + item.getProducto().getNombre());
            }
        }
        insertarVenta(venta);
    }

    
}
