package com.omnistock.app.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Alimentos: precio base (0% IVA). Requiere validar la fecha de caducidad. */
public class Alimento extends ProductoBase {

    private final long fechaCaducidad; // milisegundos

    public Alimento(int id, String nombre, double precioBase, int stock, int categoriaId,
                    long fechaCaducidad) {
        super(id, nombre, precioBase, stock, categoriaId);
        this.fechaCaducidad = fechaCaducidad;
    }

    @Override
    public double calcularPrecioFinal() {
        return redondear(getPrecioBase()); // 0% IVA
    }

    @Override
    public String getTipo() { return TIPO_ALIMENTO; }

    @Override
    public String getDetalleImpuestos() { return "IVA 0%"; }

    @Override
    public String getInfoExtra() {
        if (fechaCaducidad == 0) return "Sin fecha de caducidad";
        String fecha = formatearFecha(fechaCaducidad);
        return estaVencido() ? "VENCIDO (" + fecha + ")" : "Caduca: " + fecha;
    }

    @Override
    public Producto toEntity() {
        Producto p = super.toEntity();
        p.setFechaCaducidad(fechaCaducidad);
        return p;
    }

    /** Validación de caducidad (el Módulo 3 la usará para bloquear la venta). */
    public boolean estaVencido() {
        return estaVencida(fechaCaducidad);
    }

    public long getFechaCaducidad() { return fechaCaducidad; }

    // ---- utilidades estáticas ----

    /** Una fecha está vencida si es anterior a hoy (hoy mismo todavía sirve). */
    public static boolean estaVencida(long fecha) {
        if (fecha == 0) return false;
        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        return fecha < hoy.getTimeInMillis();
    }

    public static String formatearFecha(long fecha) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(fecha));
    }
}
