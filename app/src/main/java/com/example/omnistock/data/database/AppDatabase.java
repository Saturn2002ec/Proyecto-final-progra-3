package com.example.omnistock.data.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.omnistock.data.dao.CategoriaDao;
import com.example.omnistock.data.dao.ProductoDao;
import com.example.omnistock.data.dao.UsuarioDao;
import com.example.omnistock.data.dao.VentaDao;
import com.example.omnistock.data.entity.Categoria;
import com.example.omnistock.data.entity.HistorialVentas;
import com.example.omnistock.data.entity.Producto;
import com.example.omnistock.data.entity.Usuario;

import java.util.concurrent.Executors;

@Database(entities = {Usuario.class, Categoria.class, Producto.class, HistorialVentas.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UsuarioDao usuarioDao();
    public abstract CategoriaDao categoriaDao();
    public abstract ProductoDao productoDao();
    public abstract VentaDao ventaDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "omnistock_db"
                            )
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase database = INSTANCE;

                // 1. Precarga de usuarios para el Módulo 1 (ADMIN y VENDEDOR)[cite: 4]
                database.usuarioDao().insertar(new Usuario("admin", "1234", "ADMIN"));
                database.usuarioDao().insertar(new Usuario("vendedor", "1234", "VENDEDOR"));

                // 2. Precarga de categorías iniciales para el Módulo 2[cite: 2, 4]
                database.categoriaDao().insertar(new Categoria("Electrónica", "Dispositivos y tecnología"));
                database.categoriaDao().insertar(new Categoria("Ropa", "Prendas de vestir"));
                database.categoriaDao().insertar(new Categoria("Alimentos", "Víveres y comestibles"));
            });
        }
    };
}