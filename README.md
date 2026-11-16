# Proyecto-final-progra-3
Poryecto final progra 3 
CONTEXTO GEMINI (trabajar con este chat): https://share.gemini.google/otPkcy6Z6bTW 

Estructura Diagrama tipo Arbol para Android Studio: 
com.example.omnistock
|-- data
|   |-- dao
|   |   |-- UsuarioDao.java
|   |   |-- CategoriaDao.java
|   |   |-- ProductoDao.java
|   |   |-- VentaDao.java
|   |-- database
|   |   |-- AppDatabase.java
|   |-- entity
|       |-- Usuario.java
|       |-- Categoria.java
|       |-- Producto.java
|       |-- HistorialVentas.java
|-- model
|   |-- ProductoBase.java
|   |-- Electronico.java
|   |-- Ropa.java
|   |-- Alimento.java
|-- ui
|   |-- login
|   |   |-- LoginActivity.java
|   |   |-- LoginViewModel.java
|   |-- main
|   |   |-- MainActivity.java
|   |   |-- InventarioViewModel.java
|   |   |-- ProductoAdapter.java
|   |-- carrito
|       |-- CarritoActivity.java
|       |-- CarritoViewModel.java
|       |-- CarritoAdapter.java
|
res/layout
|-- activity_login.xml
|-- activity_main.xml
|-- activity_carrito.xml
|-- item_producto.xml
|-- item_carrito.xml
|-- dialog_producto.xml

Estructura Orden en Repositorio GitHub:
Repositorio: OmniStock
|-- main (Rama principal - Proyecto integrado y funcional)
|   |-- base-datos (Persistencia Room y relaciones 1:N)
|   |   |-- build.gradle (Dependencias de Room y Lifecycle)
|   |   |-- data/entity/Usuario.java
|   |   |-- data/entity/Categoria.java
|   |   |-- data/entity/Producto.java
|   |   |-- data/entity/HistorialVentas.java
|   |   |-- data/dao/UsuarioDao.java
|   |   |-- data/dao/CategoriaDao.java
|   |   |-- data/dao/ProductoDao.java
|   |   |-- data/dao/VentaDao.java
|   |   |-- data/database/AppDatabase.java
|   |
|   |-- modulo-1 (Control de Acceso y Roles)
|   |   |-- AndroidManifest.xml (LoginActivity como LAUNCHER)
|   |   |-- ui/login/LoginActivity.java
|   |   |-- ui/login/LoginViewModel.java
|   |   |-- res/layout/activity_login.xml
|   |   |-- ui/main/MainActivity.java (Validacion de roles ADMIN y VENDEDOR)
|   |
|   |-- modulo-2 (Inventario, Polimorfismo y Herencia)
|   |   |-- model/ProductoBase.java
|   |   |-- model/Electronico.java
|   |   |-- model/Ropa.java
|   |   |-- model/Alimento.java
|   |   |-- ui/main/MainActivity.java (CRUD y validacion try-catch)
|   |   |-- ui/main/InventarioViewModel.java
|   |   |-- ui/main/ProductoAdapter.java
|   |   |-- res/layout/activity_main.xml
|   |   |-- res/layout/item_producto.xml
|   |   |-- res/layout/dialog_producto.xml
|   |
|   |-- modulo-3 (Carrito de Compras y Transaccionalidad)
|       |-- ui/carrito/CarritoActivity.java

Conversacion gemini https://share.gemini.google/KhFkntz849Eq


|       |-- ui/carrito/CarritoViewModel.java
|       |-- ui/carrito/CarritoAdapter.java
|       |-- res/layout/activity_carrito.xml
|       |-- res/layout/item_carrito.xml
