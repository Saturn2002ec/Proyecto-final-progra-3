package com.omnistock.app.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.omnistock.app.R;
import com.omnistock.app.model.Alimento;
import com.omnistock.app.model.Categoria;
import com.omnistock.app.model.Producto;
import com.omnistock.app.model.ProductoBase;
import com.omnistock.app.viewmodel.InventarioViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Formulario para crear o editar un producto.
 * Es un DialogFragment (y no un AlertDialog suelto) para que sobreviva a la rotación
 * de pantalla sin perder lo que el usuario ya escribió.
 */
public class ProductoDialogFragment extends DialogFragment {

    private static final String ARG_ID = "id";
    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_PRECIO = "precio";
    private static final String ARG_STOCK = "stock";
    private static final String ARG_TIPO = "tipo";
    private static final String ARG_CATEGORIA = "categoria";
    private static final String ARG_CADUCIDAD = "caducidad";
    private static final String ARG_TEMPORADA = "temporada";

    private static final String STATE_CATEGORIA = "state_categoria";
    private static final String STATE_CADUCIDAD = "state_caducidad";

    private static final String[] TIPOS = {
            ProductoBase.TIPO_ELECTRONICO, ProductoBase.TIPO_ROPA, ProductoBase.TIPO_ALIMENTO};
    private static final String[] ETIQUETAS_TIPOS = {"Electrónico", "Ropa", "Alimento"};

    private InventarioViewModel viewModel;

    private EditText etNombre, etPrecio, etStock;
    private Spinner spCategoria, spTipo;
    private CheckBox cbTemporada;
    private View layoutAlimento;
    private TextView tvFecha;
    private Button btnFecha;

    private final List<Categoria> categorias = new ArrayList<>();
    private ArrayAdapter<String> categoriaAdapter;

    private int productoId = 0;
    private int categoriaIdSeleccionada = -1;
    private long fechaCaducidad = 0;

    public static ProductoDialogFragment nuevo() {
        return new ProductoDialogFragment();
    }

    public static ProductoDialogFragment editar(ProductoBase producto) {
        Producto e = producto.toEntity();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, e.getId());
        args.putString(ARG_NOMBRE, e.getNombre());
        args.putDouble(ARG_PRECIO, e.getPrecioBase());
        args.putInt(ARG_STOCK, e.getStock());
        args.putString(ARG_TIPO, e.getTipoProducto());
        args.putInt(ARG_CATEGORIA, e.getCategoriaId());
        args.putLong(ARG_CADUCIDAD, e.getFechaCaducidad());
        args.putBoolean(ARG_TEMPORADA, e.isTemporadaAnterior());

        ProductoDialogFragment fragment = new ProductoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(InventarioViewModel.class);

        View vista = requireActivity().getLayoutInflater().inflate(R.layout.dialog_producto, null);
        etNombre = vista.findViewById(R.id.etNombre);
        etPrecio = vista.findViewById(R.id.etPrecio);
        etStock = vista.findViewById(R.id.etStock);
        spCategoria = vista.findViewById(R.id.spCategoria);
        spTipo = vista.findViewById(R.id.spTipo);
        cbTemporada = vista.findViewById(R.id.cbTemporada);
        layoutAlimento = vista.findViewById(R.id.layoutAlimento);
        tvFecha = vista.findViewById(R.id.tvFecha);
        btnFecha = vista.findViewById(R.id.btnFecha);

        // La selección de categoría la manejo yo (las categorías llegan de forma asíncrona)
        spCategoria.setSaveEnabled(false);

        Bundle args = getArguments();
        boolean edicion = args != null && args.containsKey(ARG_ID);

        spTipo.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, ETIQUETAS_TIPOS));

        if (edicion) {
            productoId = args.getInt(ARG_ID);
        }

        if (savedInstanceState != null) {
            // Rotación: los EditText se restauran solos, yo recupero lo demás
            categoriaIdSeleccionada = savedInstanceState.getInt(STATE_CATEGORIA, -1);
            fechaCaducidad = savedInstanceState.getLong(STATE_CADUCIDAD, 0);
        } else if (edicion) {
            etNombre.setText(args.getString(ARG_NOMBRE));
            etPrecio.setText(String.valueOf(args.getDouble(ARG_PRECIO)));
            etStock.setText(String.valueOf(args.getInt(ARG_STOCK)));
            cbTemporada.setChecked(args.getBoolean(ARG_TEMPORADA));
            categoriaIdSeleccionada = args.getInt(ARG_CATEGORIA);
            fechaCaducidad = args.getLong(ARG_CADUCIDAD);
            spTipo.setSelection(indiceTipo(args.getString(ARG_TIPO)));
        }
        actualizarTextoFecha();

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipo = TIPOS[position];
                cbTemporada.setVisibility(ProductoBase.TIPO_ROPA.equals(tipo) ? View.VISIBLE : View.GONE);
                layoutAlimento.setVisibility(ProductoBase.TIPO_ALIMENTO.equals(tipo) ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        categoriaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
        spCategoria.setAdapter(categoriaAdapter);
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < categorias.size()) {
                    categoriaIdSeleccionada = categorias.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        viewModel.getCategorias().observe(this, lista -> {
            categorias.clear();
            categoriaAdapter.clear();
            int posicion = 0;
            for (int i = 0; i < lista.size(); i++) {
                categorias.add(lista.get(i));
                categoriaAdapter.add(lista.get(i).getNombreCategoria());
                if (lista.get(i).getId() == categoriaIdSeleccionada) {
                    posicion = i;
                }
            }
            categoriaAdapter.notifyDataSetChanged();
            spCategoria.setSelection(posicion);
        });

        btnFecha.setOnClickListener(v -> mostrarSelectorFecha());

        return new AlertDialog.Builder(requireContext())
                .setTitle(edicion ? "Editar producto" : "Nuevo producto")
                .setView(vista)
                .setPositiveButton("Guardar", null) // se sobreescribe en onStart
                .setNegativeButton("Cancelar", null)
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Así el diálogo no se cierra solo cuando hay un error de validación
        AlertDialog dialogo = (AlertDialog) getDialog();
        if (dialogo != null) {
            dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> guardar());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CATEGORIA, categoriaIdSeleccionada);
        outState.putLong(STATE_CADUCIDAD, fechaCaducidad);
    }

    private void guardar() {
        try {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                etNombre.setError("El nombre es obligatorio");
                return;
            }

            // Si aquí hay letras o está vacío, salta NumberFormatException
            double precio = Double.parseDouble(
                    etPrecio.getText().toString().trim().replace(',', '.'));
            if (precio <= 0) {
                etPrecio.setError("El precio debe ser mayor a 0");
                return;
            }

            int stock = Integer.parseInt(etStock.getText().toString().trim());
            if (stock < 0) {
                etStock.setError("La cantidad no puede ser negativa");
                return;
            }

            if (categorias.isEmpty() || categoriaIdSeleccionada == -1) {
                Toast.makeText(requireContext(),
                        "Primero crea una categoría", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipo = TIPOS[spTipo.getSelectedItemPosition()];
            Producto producto = new Producto(nombre, precio, stock, tipo, categoriaIdSeleccionada);
            producto.setId(productoId);

            if (ProductoBase.TIPO_ROPA.equals(tipo)) {
                producto.setTemporadaAnterior(cbTemporada.isChecked());
            } else if (ProductoBase.TIPO_ALIMENTO.equals(tipo)) {
                if (fechaCaducidad == 0) {
                    Toast.makeText(requireContext(),
                            "Elige la fecha de caducidad", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Alimento.estaVencida(fechaCaducidad)) {
                    Toast.makeText(requireContext(),
                            "No puedes registrar un alimento ya vencido", Toast.LENGTH_SHORT).show();
                    return;
                }
                producto.setFechaCaducidad(fechaCaducidad);
            }

            viewModel.guardarProducto(producto);
            dismiss();

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(),
                    "Ingresa solo números válidos en Precio y Cantidad", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarSelectorFecha() {
        Calendar c = Calendar.getInstance();
        if (fechaCaducidad != 0) {
            c.setTimeInMillis(fechaCaducidad);
        }
        new DatePickerDialog(requireContext(), (view, anio, mes, dia) -> {
            Calendar elegida = Calendar.getInstance();
            elegida.clear();
            elegida.set(anio, mes, dia);
            fechaCaducidad = elegida.getTimeInMillis();
            actualizarTextoFecha();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarTextoFecha() {
        tvFecha.setText(fechaCaducidad == 0
                ? "Sin fecha elegida"
                : "Caduca: " + Alimento.formatearFecha(fechaCaducidad));
    }

    private int indiceTipo(String tipo) {
        for (int i = 0; i < TIPOS.length; i++) {
            if (TIPOS[i].equals(tipo)) return i;
        }
        return 0;
    }
}
