package com.example.omnistock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "historial_ventas",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuario_id",
                onDelete = ForeignKey.CASCADE
        ))
public class HistorialVentas {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "usuario_id")
    public int usuarioId;

    @ColumnInfo(name = "total")
    public double total;

    @ColumnInfo(name = "fecha")
    public String fecha;

    public HistorialVentas(int usuarioId, double total, String fecha) {
        this.usuarioId = usuarioId;
        this.total = total;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public double getTotal() { return total; }
    public String getFecha() { return fecha; }
}
