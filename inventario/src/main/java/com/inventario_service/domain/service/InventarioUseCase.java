package com.inventario_service.domain.service;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Sucursal;
import com.inventario_service.ports.in.InventarioInputPort;
import com.inventario_service.ports.out.InventarioOutputPort;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventarioUseCase implements InventarioInputPort {

    private final InventarioOutputPort inventarioOutputPort;

    // Inyección pura por constructor (Sin tocar anotaciones de Spring aquí)
    public InventarioUseCase(InventarioOutputPort inventarioOutputPort) {
        this.inventarioOutputPort = inventarioOutputPort;
    }

    @Override
    public Lote registrarIngresoLote(Lote lote) {
        // Regla de negocio: Validar que el lote traiga un producto asociado
        if (lote.getProducto() == null || lote.getProducto().getNombre() == null) {
            throw new RuntimeException("Error: El lote debe contener obligatoriamente los datos de un producto.");
        }

        // Regla de negocio: Impedir el ingreso de medicamentos ya caducados a la farmacia
        if (lote.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new RuntimeException("Error: No se puede registrar en inventario un lote cuya fecha de vencimiento ya expiró.");
        }

        return inventarioOutputPort.guardarLote(lote);
    }

    @Override
    public List<Lote> listarInventarioPorSucursalYCategoria(Sucursal sucursal, Categoria categoria) {
        return inventarioOutputPort.buscarPorSucursalYCategoria(sucursal, categoria);
    }

    @Override
    public Map<String, Object> obtenerMetricasDashboard(Sucursal sucursal) {
        Map<String, Object> metricas = new HashMap<>();

        // REQUERIMIENTO DEL PROTOTIPO: Alerta de vencimientos inminentes (Límite de 5 días según la vista)
        LocalDate fechaLimiteVencimiento = LocalDate.now().plusDays(5);
        List<Lote> proximosAVencer = inventarioOutputPort.buscarProximosAVencer(sucursal, fechaLimiteVencimiento);

        // REQUERIMIENTO DEL PROTOTIPO: Alerta de productos con stock al mínimo o en cero
        List<Lote> stockBajo = inventarioOutputPort.buscarStockBajo(sucursal);

        // REQUERIMIENTO DEL PROTOTIPO: Calcular la sumatoria de unidades totales en los estantes de la farmacia
        int stockTotalUnidades = stockBajo.stream()
                .mapToInt(Lote::getCantidad)
                .sum();

        // Estructura del JSON que alimentará las tarjetas de la interfaz gráfica
        metricas.put("sucursal", sucursal.name());
        metricas.put("stockTotalUnidades", stockTotalUnidades);
        metricas.put("cantidadProximosAVencer", proximosAVencer.size());
        metricas.put("listaProximosAVencer", proximosAVencer);
        metricas.put("cantidadStockMinimo", stockBajo.size());
        metricas.put("listaStockBajo", stockBajo);

        return metricas;
    }
}
