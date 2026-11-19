package com.omnistock.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.omnistock.app.data.AppDatabase;
import com.omnistock.app.data.CategoriaDao;
import com.omnistock.app.data.ProductoDao;
import com.omnistock.app.model.Categoria;
import com.omnistock.app.model.CategoriaConProductos;
import com.omnistock.app.model.Producto;
import com.omnistock.app.model.ProductoBase;
import com.omnistock.app.model.ProductoFactory;

import java.util.List;

/**
 * ViewModel del inventario. Se comparte entre la MainActivity y el diálogo del formulario.
 * La lista que expone ya son objetos ProductoBase (Electronico, Ropa o Alimento).
 */
public class InventarioViewModel extends AndroidViewModel {

    public static final int TODAS = -1;

    private final ProductoDao productoDao;
    private final CategoriaDao categoriaDao;

    private final MutableLiveData<Integer> filtroCategoria = new MutableLiveData<>(TODAS);
    private final LiveData<List<ProductoBase>> productos;
    private final LiveData<List<Categoria>> categorias;
    private final LiveData<List<CategoriaConProductos>> categoriasConProductos;
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public InventarioViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        productoDao = db.productoDao();
        categoriaDao = db.categoriaDao();

        categorias = categoriaDao.obtenerTodas();
        categoriasConProductos = categoriaDao.obtenerCategoriasConProductos();

        // Cuando cambia el filtro, se vuelve a consultar y se convierte a ProductoBase
        productos = Transformations.switchMap(filtroCategoria, id -> {
            LiveData<List<Producto>> origen = (id == TODAS)
                    ? productoDao.obtenerTodos()
                    : productoDao.obtenerPorCategoria(id);
            return Transformations.map(origen, ProductoFactory::crearLista);
        });
    }

    public LiveData<List<ProductoBase>> getProductos() { return productos; }
    public LiveData<List<Categoria>> getCategorias() { return categorias; }
    public LiveData<List<CategoriaConProductos>> getCategoriasConProductos() { return categoriasConProductos; }
    public LiveData<String> getMensaje() { return mensaje; }

    public int getFiltroActual() {
        Integer actual = filtroCategoria.getValue();
        return actual == null ? TODAS : actual;
    }

    public void setFiltro(int categoriaId) {
        if (getFiltroActual() != categoriaId) {
            filtroCategoria.setValue(categoriaId);
        }
    }

    public void guardarProducto(Producto producto) {
        AppDatabase.databaseExecutor.execute(() -> {
            if (producto.getId() == 0) {
                productoDao.insertar(producto);
                mensaje.postValue("Producto agregado");
            } else {
                productoDao.actualizar(producto);
                mensaje.postValue("Producto actualizado");
            }
        });
    }

    public void eliminarProducto(int id) {
        AppDatabase.databaseExecutor.execute(() -> {
            productoDao.eliminarPorId(id);
            mensaje.postValue("Producto eliminado");
        });
    }

    public void crearCategoria(String nombre, String descripcion) {
        AppDatabase.databaseExecutor.execute(() -> {
            categoriaDao.insertar(new Categoria(nombre, descripcion));
            mensaje.postValue("Categoría creada");
        });
    }

    public void eliminarCategoria(Categoria categoria) {
        AppDatabase.databaseExecutor.execute(() -> {
            categoriaDao.eliminar(categoria);
            mensaje.postValue("Categoría eliminada junto con sus productos");
        });
    }

    public void limpiarMensaje() {
        mensaje.setValue(null);
    }
}
