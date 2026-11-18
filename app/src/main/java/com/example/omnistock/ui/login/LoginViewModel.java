package com.example.omnistock.ui.login;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.omnistock.data.dao.UsuarioDao;
import com.example.omnistock.data.database.AppDatabase;
import com.example.omnistock.data.entity.Usuario;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {

    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;
    private final MutableLiveData<Usuario> usuarioAutenticado = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Usuario> getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public void autenticar(String nombre, String pass) {
        if (nombre.trim().isEmpty() || pass.trim().isEmpty()) {
            mensajeError.setValue("Por favor, complete todos los campos.");
            return;
        }

        executorService.execute(() -> {
            Usuario user = usuarioDao.login(nombre.trim(), pass.trim());
            if (user != null) {
                usuarioAutenticado.postValue(user);
            } else {
                mensajeError.postValue("Credenciales inválidas. Usuario no encontrado.");
            }
        });
    }
}