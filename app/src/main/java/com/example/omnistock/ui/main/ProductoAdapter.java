package com.omnistock.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.omnistock.app.R;
import com.omnistock.app.model.Categoria;
import com.omnistock.app.model.ProductoBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Adapter de la lista de productos. El color y el icono de la tarjeta cambian según el tipo. */
public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    public interface OnProductoListener {
        void onEditar(ProductoBase producto);
        void onEliminar(ProductoBase producto);
    }

    private List<ProductoBase> productos = new ArrayList<>();
    private final Map<Integer, String> nombresCategorias = new HashMap<>();
    private final boolean esAdmin;
    private final OnProductoListener listener;

    public ProductoAdapter(boolean esAdmin, OnProductoListener listener) {
        this.esAdmin = esAdmin;
        this.listener = listener;
    }

    public void setProductos(List<ProductoBase> lista) {
        this.productos = (lista != null) ? lista : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCategorias(List<Categoria> categorias) {
        nombresCategorias.clear();
        if (categorias != null) {
            for (Categoria c : categorias) {
                nombresCategorias.put(c.getId(), c.getNombreCategoria());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder h, int position) {
        ProductoBase p = productos.get(position);
        Context ctx = h.itemView.getContext();

        String categoria = nombresCategorias.get(p.getCategoriaId());
        h.tvNombre.setText(p.getNombre());
        h.tvTipoCategoria.setText(etiquetaTipo(p.getTipo()) + "  •  "
                + (categoria != null ? categoria : "Sin categoría"));
        h.tvPrecioBase.setText(String.format(Locale.US, "Precio base: $%.2f", p.getPrecioBase()));

        // Polimorfismo: cada objeto calcula su precio y sus textos a su manera
        h.tvPrecioFinal.setText(String.format(Locale.US, "Precio final: $%.2f", p.calcularPrecioFinal()));
        h.tvDetalle.setText(p.getDetalleImpuestos());
        h.tvStock.setText("Stock: " + p.getStock());

        String extra = p.getInfoExtra();
        h.tvExtra.setVisibility(extra.isEmpty() ? View.GONE : View.VISIBLE);
        h.tvExtra.setText(extra);

        // Visualización dinámica según el tipo
        int fondo;
        int acento;
        int icono;
        switch (p.getTipo()) {
            case ProductoBase.TIPO_ELECTRONICO:
                fondo = R.color.fondo_electronico;
                acento = R.color.acento_electronico;
                icono = R.drawable.ic_electronico;
                break;
            case ProductoBase.TIPO_ROPA:
                fondo = R.color.fondo_ropa;
                acento = R.color.acento_ropa;
                icono = R.drawable.ic_ropa;
                break;
            default:
                fondo = R.color.fondo_alimento;
                acento = R.color.acento_alimento;
                icono = R.drawable.ic_alimento;
                break;
        }
        h.card.setCardBackgroundColor(ContextCompat.getColor(ctx, fondo));
        h.ivIcono.setImageResource(icono);
        h.ivIcono.setColorFilter(ContextCompat.getColor(ctx, acento));

        // UI dinámica por rol: el vendedor no ve editar/eliminar
        int visibilidad = esAdmin ? View.VISIBLE : View.GONE;
        h.btnEditar.setVisibility(visibilidad);
        h.btnEliminar.setVisibility(visibilidad);
        h.btnEditar.setOnClickListener(v -> listener.onEditar(p));
        h.btnEliminar.setOnClickListener(v -> listener.onEliminar(p));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    private String etiquetaTipo(String tipo) {
        switch (tipo) {
            case ProductoBase.TIPO_ELECTRONICO: return "Electrónico";
            case ProductoBase.TIPO_ROPA: return "Ropa";
            default: return "Alimento";
        }
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        final CardView card;
        final ImageView ivIcono;
        final TextView tvNombre, tvTipoCategoria, tvPrecioBase, tvPrecioFinal, tvDetalle, tvStock, tvExtra;
        final ImageButton btnEditar, btnEliminar;

        ProductoViewHolder(@NonNull View v) {
            super(v);
            card = v.findViewById(R.id.cardProducto);
            ivIcono = v.findViewById(R.id.ivIcono);
            tvNombre = v.findViewById(R.id.tvNombre);
            tvTipoCategoria = v.findViewById(R.id.tvTipoCategoria);
            tvPrecioBase = v.findViewById(R.id.tvPrecioBase);
            tvPrecioFinal = v.findViewById(R.id.tvPrecioFinal);
            tvDetalle = v.findViewById(R.id.tvDetalle);
            tvStock = v.findViewById(R.id.tvStock);
            tvExtra = v.findViewById(R.id.tvExtra);
            btnEditar = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}
