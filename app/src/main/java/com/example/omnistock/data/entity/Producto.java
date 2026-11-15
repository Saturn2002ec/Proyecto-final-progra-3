package com.example.omnistock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "productos",
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "id",
                childColumns = "categoria_id",
                onDelete = ForeignKey.CASCADE // Borrado en cascada (Prueba de Integridad)[cite: 4]
        ))
public class Producto {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "precio_base")
    public double precioBase;

    @ColumnInfo(name = "stock")
    public int stock;

    @ColumnInfo(name = "tipo_producto")
    public String tipoProducto; // "ELECTRONICO", "ROPA", "ALIMENTO"

    @ColumnInfo(name = "categoria_id")
    public int categoriaId;

    public Producto(String nombre, double precioBase, int stock, String tipoProducto, int categoriaId) {
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.stock = stock;
        this.tipoProducto = tipoProducto;
        this.categoriaId = categoriaId;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public int getStock() { return stock; }
    public String getTipoProducto() { return tipoProducto; }
    public int getCategoriaId() { return categoriaId; }
    public void setStock(int stock) { this.stock = stock; }
}