package com.example.omnistock.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.omnistock.data.entity.HistorialVentas;
import java.util.List;

@Dao
public interface VentaDao {

    @Insert
    long insertarVenta(HistorialVentas venta);

    @Query("SELECT * FROM historial_ventas")
    List<HistorialVentas> obtenerHistorial();
}