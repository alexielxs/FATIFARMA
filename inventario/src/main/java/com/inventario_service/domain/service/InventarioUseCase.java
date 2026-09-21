package com.inventario_service.domain.service;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Producto;
import com.inventario_service.domain.model.Sucursal;
import com.inventario_service.ports.in.InventarioInputPort;
import com.inventario_service.ports.out.InventarioOutputPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class InventarioUseCase implements InventarioInputPort {

    private final InventarioOutputPort inventarioOutputPort;

    public InventarioUseCase(InventarioOutputPort inventarioOutputPort) {
        this.inventarioOutputPort = inventarioOutputPort;
    }

    // =========================================================================
    // HU04 Y HU05: REGISTRAR LOTE Y MAESTRO RELACIONAL (Botón Registrar)
    // =========================================================================
    @Override
    public Lote ingresarNuevoLote(Lote lote, Producto producto, String nombreCategoria) {
        // Toda la lógica relacional pesada de inserción y búsquedas en cascada de las 4 tablas
        // se delegó de manera limpia al puerto de persistencia físico del adaptador para evitar acoplamiento.
        return inventarioOutputPort.guardarLote(lote);
    }

    // =========================================================================
    // HU06: DESCUENTO SINCÓNICO EN TIEMPO REAL (Llamado vía OpenFeign por Ventas)
    // =========================================================================
    @Override
    public void actualizarStockPorVenta(Long loteId, Integer cantidadVendida) {
        // El adaptador de persistencia recuperará el lote unificado
        List<Lote> todosLosLotes = inventarioOutputPort.buscarPorSucursalYCategoria("", "");

        // Buscamos el lote transaccional afectado
        Lote lote = todosLosLotes.stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error: El lote físico especificado no existe en MySQL."));

        if (lote.getCantidad() < cantidadVendida) {
            throw new RuntimeException("Error de Caja: Stock insuficiente en lote. Disponibles: " + lote.getCantidad());
        }

        lote.setCantidad(lote.getCantidad() - cantidadVendida);
        inventarioOutputPort.guardarLote(lote);
    }

    // =========================================================================
    // HU07: MODIFICACIÓN GENERAL DE STOCK O PRECIOS (Botón Actualizar)
    // =========================================================================
    @Override
    public Lote modificarDatosInventario(Long loteId, Map<String, Object> actualizaciones) {
        List<Lote> todosLosLotes = inventarioOutputPort.buscarPorSucursalYCategoria("", "");
        Lote lote = todosLosLotes.stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lote no encontrado para modificación."));

        if (actualizaciones.containsKey("precioVenta")) {
            lote.setPrecioVenta(Double.parseDouble(actualizaciones.get("precioVenta").toString()));
        }
        if (actualizaciones.containsKey("cantidad")) {
            lote.setCantidad(Integer.parseInt(actualizaciones.get("cantidad").toString()));
        }
        if (actualizaciones.containsKey("ubicacionAnaquel")) {
            lote.setUbicacionAnaquel((String) actualizaciones.get("ubicacionAnaquel"));
        }

        return inventarioOutputPort.guardarLote(lote);
    }

    // =========================================================================
    // HU08: CONSULTAR UBICACIÓN EN ANAQUELES (Agilidad de Atención)
    // =========================================================================
    @Override
    public String consultarUbicacionFisica(Long loteId) {
        List<Lote> todosLosLotes = inventarioOutputPort.buscarPorSucursalYCategoria("", "");
        Lote lote = todosLosLotes.stream()
                .filter(l -> l.getId().equals(loteId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Lote no registrado."));

        return lote.getUbicacionAnaquel() != null ? lote.getUbicacionAnaquel() : "No asignado";
    }

    // =========================================================================
    // FILTROS DEL CATÁLOGO POR PESTAÑAS DE COLORES (Medicamentos, Suplementos)
    // =========================================================================
    @Override
    public List<Lote> listarCatalogoPorFiltros(String sucursal, String categoria) {
        return inventarioOutputPort.buscarPorSucursalYCategoria(sucursal, categoria);
    }

    @Override
    public List<Lote> obtenerTodosLosLotesParaAlertas() {
        return inventarioOutputPort.buscarPorSucursalYCategoria("", "");
    }

    // =========================================================================
    // ADICIONAL: Métodos analíticos de soporte para alimentar las tarjetas superiores
    // =========================================================================
    public List<Lote> buscarProximosAVencer(Sucursal sucursal, LocalDate fechaLimite) {
        return inventarioOutputPort.buscarProximosAVencer(sucursal.getNombreSucursal(), fechaLimite);
    }

    public List<Lote> buscarStockBajo(Sucursal sucursal) {
        return inventarioOutputPort.buscarStockBajo(sucursal.getNombreSucursal());
    }
}
