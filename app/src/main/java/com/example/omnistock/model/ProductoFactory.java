package com.omnistock.app.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Convierte el registro de Room (Producto) en el objeto de negocio que corresponde
 * según tipo_producto. Es el único lugar donde se decide la clase hija.
 */
public class ProductoFactory {

    private ProductoFactory() { }

    public static ProductoBase crear(Producto p) {
        switch (p.getTipoProducto()) {
            case ProductoBase.TIPO_ELECTRONICO:
                return new Electronico(p.getId(), p.getNombre(), p.getPrecioBase(),
                        p.getStock(), p.getCategoriaId());
            case ProductoBase.TIPO_ROPA:
                return new Ropa(p.getId(), p.getNombre(), p.getPrecioBase(),
                        p.getStock(), p.getCategoriaId(), p.isTemporadaAnterior());
            case ProductoBase.TIPO_ALIMENTO:
                return new Alimento(p.getId(), p.getNombre(), p.getPrecioBase(),
                        p.getStock(), p.getCategoriaId(), p.getFechaCaducidad());
            default:
                throw new IllegalArgumentException(
                        "Tipo de producto desconocido: " + p.getTipoProducto());
        }
    }

    public static List<ProductoBase> crearLista(List<Producto> productos) {
        List<ProductoBase> resultado = new ArrayList<>();
        if (productos != null) {
            for (Producto p : productos) {
                resultado.add(crear(p));
            }
        }
        return resultado;
    }
}
