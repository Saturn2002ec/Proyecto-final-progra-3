package com.omnistock.app.model;

/**
 * Ropa: precio base + IVA (12%).
 * Si es de temporada anterior se aplica un 20% de descuento sobre el precio base
 * y luego se calcula el IVA (con multiplicaciones el orden no cambia el resultado).
 */
public class Ropa extends ProductoBase {

    private static final double IVA = 0.12;
    private static final double DESCUENTO = 0.20;

    private final boolean temporadaAnterior;

    public Ropa(int id, String nombre, double precioBase, int stock, int categoriaId,
                boolean temporadaAnterior) {
        super(id, nombre, precioBase, stock, categoriaId);
        this.temporadaAnterior = temporadaAnterior;
    }

    @Override
    public double calcularPrecioFinal() {
        double precio = getPrecioBase();
        if (temporadaAnterior) {
            precio = precio * (1 - DESCUENTO);
        }
        // Ejemplo: 100 -> 112 | temporada anterior: 100 -> 80 -> 89.60
        return redondear(precio * (1 + IVA));
    }

    @Override
    public String getTipo() { return TIPO_ROPA; }

    @Override
    public String getDetalleImpuestos() {
        return temporadaAnterior ? "IVA 12% - Descuento 20%" : "IVA 12%";
    }

    @Override
    public String getInfoExtra() {
        return temporadaAnterior ? "Temporada anterior" : "Temporada actual";
    }

    @Override
    public Producto toEntity() {
        Producto p = super.toEntity();
        p.setTemporadaAnterior(temporadaAnterior);
        return p;
    }

    public boolean isTemporadaAnterior() { return temporadaAnterior; }
}
