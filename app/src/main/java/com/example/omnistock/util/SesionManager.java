package com.example.omnistock.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.omnistock.data.entity.Usuario;

/**
 * Guarda quién inició sesión. Los demás módulos lo usan para saber el rol
 * (ej. ocultar el botón flotante si es VENDEDOR).
 */
public class SesionManager {

    private static final String PREFS = "omnistock_sesion";
    private static final String KEY_ID = "usuario_id";
    private static final String KEY_NOMBRE = "usuario_nombre";
    private static final String KEY_ROL = "usuario_rol";

    private final SharedPreferences prefs;

    public SesionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardarSesion(Usuario usuario) {
        prefs.edit()
                .putInt(KEY_ID, usuario.getId())
                .putString(KEY_NOMBRE, usuario.getNombre())
                .putString(KEY_ROL, usuario.getRol())
                .apply();
    }

    public boolean haySesionActiva() {
        return prefs.getInt(KEY_ID, -1) != -1;
    }

    public int getUsuarioId() { return prefs.getInt(KEY_ID, -1); }

    public String getNombre() { return prefs.getString(KEY_NOMBRE, ""); }

    public String getRol() { return prefs.getString(KEY_ROL, ""); }

    public boolean esAdmin() {
        return Usuario.ROL_ADMIN.equals(getRol());
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}
