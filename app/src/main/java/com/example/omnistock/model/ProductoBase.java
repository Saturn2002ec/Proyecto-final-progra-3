package com.omnistock.app.model;

/**
 * Clase base ABSTRACTA de los productos (Herencia + Polimorfismo).
 * Cada hija define cómo calcula su precio final, su detalle de impuestos
 * y su información extra. Quien use un ProductoBase (la UI o el carrito)
 * no necesita saber de qué tipo es: solo llama a los métodos.
 */
public abstract class ProductoBase {

    public static final String TIPO_ELECTRONICO = "ELECTRONICO";
    public static final String TIPO_ROPA = "ROPA";
    public static final String TIPO_ALIMENTO = "ALIMENTO";

    private final int id;
    private final String nombre;
    private final double precioBase;
    private final int stock;
    private final int categoriaId;

    public ProductoBase(int id, String nombre, double precioBase, int stock, int categoriaId) {
        this.id = id;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.stock = stock;
        this.categoriaId = categoriaId;
    }

    // ---- Métodos polimórficos: cada hija los implementa a su manera ----

    /** Precio final de UNA unidad, con impuestos y descuentos incluidos. */
    public abstract double calcularPrecioFinal();

    /** Tipo de producto (una de las constantes TIPO_*). */
    public abstract String getTipo();

    /** Texto corto con los impuestos que aplican, para mostrar en la tarjeta. */
    public abstract String getDetalleImpuestos();

    /** Información propia del tipo (temporada, caducidad...). Puede ser vacío. */
    public abstract String getInfoExtra();

    // ---- Comportamiento común (heredado tal cual) ----

    /** Total de varias unidades. Lo usará el carrito en el Módulo 3. */
    public double calcularSubtotal(int cantidad) {
        return redondear(calcularPrecioFinal() * cantidad);
    }

    /** Convierte el objeto de negocio de vuelta a la entidad de Room. */
    public Producto toEntity() {
        Producto p = new Producto(nombre, precioBase, stock, getTipo(), categoriaId);
        p.setId(id);
        return p;
    }

    /** Redondeo a 2 decimales para evitar cosas como 120.00000000000001 */
    protected double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getPrecioBase() { return precioBase; }
    public int getStock() { return stock; }
    public int getCategoriaId() { return categoriaId; }
}
