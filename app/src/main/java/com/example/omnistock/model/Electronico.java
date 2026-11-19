package com.omnistock.app.model;

/** Electrónicos: precio base + IVA (15%) + impuesto suntuario (5%). */
public class Electronico extends ProductoBase {

    private static final double IVA = 0.15;
    private static final double SUNTUARIO = 0.05;

    public Electronico(int id, String nombre, double precioBase, int stock, int categoriaId) {
        super(id, nombre, precioBase, stock, categoriaId);
    }

    @Override
    public double calcularPrecioFinal() {
        // Ejemplo: 100 + 15 + 5 = 120
        return redondear(getPrecioBase() * (1 + IVA + SUNTUARIO));
    }

    @Override
    public String getTipo() { return TIPO_ELECTRONICO; }

    @Override
    public String getDetalleImpuestos() { return "IVA 15% + Suntuario 5%"; }

    @Override
    public String getInfoExtra() { return ""; }
}
