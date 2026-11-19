package com.omnistock.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.omnistock.app.R;
import com.omnistock.app.data.SesionManager;
import com.omnistock.app.model.Categoria;
import com.omnistock.app.model.CategoriaConProductos;
import com.omnistock.app.model.ProductoBase;
import com.omnistock.app.viewmodel.InventarioViewModel;

import java.util.ArrayList;
import java.util.List;

/** Pantalla principal: lista de productos (RecyclerView), filtro por categoría y opciones según el rol. */
public class MainActivity extends AppCompatActivity {

    private InventarioViewModel viewModel;
    private SesionManager sesion;
    private ProductoAdapter adapter;
    private Spinner spFiltro;

    private final List<Integer> idsFiltro = new ArrayList<>();
    private List<CategoriaConProductos> categoriasConProductos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sesion = new SesionManager(this);
        viewModel = new ViewModelProvider(this).get(InventarioViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("OmniStock");
        getSupportActionBar().setSubtitle(sesion.getNombre() + " (" + sesion.getRol() + ")");

        spFiltro = findViewById(R.id.spFiltro);
        RecyclerView rvProductos = findViewById(R.id.rvProductos);
        FloatingActionButton fab = findViewById(R.id.fabAgregar);

        adapter = new ProductoAdapter(sesion.esAdmin(), new ProductoAdapter.OnProductoListener() {
            @Override
            public void onEditar(ProductoBase producto) {
                ProductoDialogFragment.editar(producto)
                        .show(getSupportFragmentManager(), "producto");
            }

            @Override
            public void onEliminar(ProductoBase producto) {
                confirmarEliminarProducto(producto);
            }
        });
        rvProductos.setLayoutManager(new LinearLayoutManager(this));
        rvProductos.setAdapter(adapter);

        // UI dinámica: solo el ADMIN ve el botón flotante
        fab.setVisibility(sesion.esAdmin() ? View.VISIBLE : View.GONE);
        fab.setOnClickListener(v ->
                ProductoDialogFragment.nuevo().show(getSupportFragmentManager(), "producto"));

        configurarFiltro();
        observarViewModel();
    }

    private void configurarFiltro() {
        spFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < idsFiltro.size()) {
                    viewModel.setFiltro(idsFiltro.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void observarViewModel() {
        viewModel.getProductos().observe(this, adapter::setProductos);

        viewModel.getCategorias().observe(this, categorias -> {
            adapter.setCategorias(categorias);

            List<String> nombres = new ArrayList<>();
            idsFiltro.clear();
            nombres.add("Todas las categorías");
            idsFiltro.add(InventarioViewModel.TODAS);
            for (Categoria c : categorias) {
                nombres.add(c.getNombreCategoria());
                idsFiltro.add(c.getId());
            }
            spFiltro.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, nombres));

            int posicion = idsFiltro.indexOf(viewModel.getFiltroActual());
            if (posicion == -1) { // la categoría filtrada fue eliminada
                viewModel.setFiltro(InventarioViewModel.TODAS);
                posicion = 0;
            }
            spFiltro.setSelection(posicion);
        });

        viewModel.getCategoriasConProductos().observe(this,
                lista -> categoriasConProductos = lista);

        viewModel.getMensaje().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                viewModel.limpiarMensaje();
            }
        });
    }

    // ---------- Menú ----------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean admin = sesion.esAdmin();
        menu.findItem(R.id.action_nueva_categoria).setVisible(admin);
        menu.findItem(R.id.action_eliminar_categoria).setVisible(admin);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_nueva_categoria) {
            mostrarDialogoNuevaCategoria();
            return true;
        } else if (id == R.id.action_eliminar_categoria) {
            mostrarDialogoEliminarCategoria();
            return true;
        } else if (id == R.id.action_cerrar_sesion) {
            sesion.cerrarSesion();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------- Diálogos ----------

    private void confirmarEliminarProducto(ProductoBase producto) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que deseas eliminar \"" + producto.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminarProducto(producto.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoNuevaCategoria() {
        View vista = getLayoutInflater().inflate(R.layout.dialog_categoria, null);
        EditText etNombreCat = vista.findViewById(R.id.etNombreCategoria);
        EditText etDescripcion = vista.findViewById(R.id.etDescripcionCategoria);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle("Nueva categoría")
                .setView(vista)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null)
                .create();
        dialogo.show();

        dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nombre = etNombreCat.getText().toString().trim();
            if (nombre.isEmpty()) {
                etNombreCat.setError("El nombre es obligatorio");
                return;
            }
            viewModel.crearCategoria(nombre, etDescripcion.getText().toString().trim());
            dialogo.dismiss();
        });
    }

    private void mostrarDialogoEliminarCategoria() {
        if (categoriasConProductos.isEmpty()) {
            Toast.makeText(this, "No hay categorías para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<CategoriaConProductos> lista = new ArrayList<>(categoriasConProductos);
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            nombres[i] = lista.get(i).categoria.getNombreCategoria();
        }
        new AlertDialog.Builder(this)
                .setTitle("¿Qué categoría deseas eliminar?")
                .setItems(nombres, (d, which) -> confirmarEliminarCategoria(lista.get(which)))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarCategoria(CategoriaConProductos item) {
        int cantidad = item.productos.size();
        String mensaje = (cantidad == 0)
                ? "¿Eliminar la categoría \"" + item.categoria.getNombreCategoria() + "\"?"
                : "La categoría \"" + item.categoria.getNombreCategoria() + "\" tiene " + cantidad
                  + " producto(s). Al eliminarla también se eliminarán todos sus productos. ¿Continuar?";

        new AlertDialog.Builder(this)
                .setTitle("Eliminar categoría")
                .setMessage(mensaje)
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminarCategoria(item.categoria))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
