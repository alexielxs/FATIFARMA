package com.notificaciones.domain.service;

import com.notificaciones.domain.model.AlertaCaducidad;
import com.notificaciones.domain.model.AlertaStock;
import com.notificaciones.ports.out.InventarioClientPort;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import java.util.Map;

public class NotificacionUseCase {
    public final InventarioClientPort inventarioClientPort;

    public NotificacionUseCase(InventarioClientPort inventarioClientPort) {
        this.inventarioClientPort = inventarioClientPort;
    }
    public Map<String, Object> generarReporteAlertas(String sucursal) {
        // 1. Jalamos las métricas crudas desde el microservicio de Inventario
        Map<String, Object> datosInventario = inventarioClientPort.obtenerMetricasDashboard(sucursal);

        List<Map<String, Object>> listaProximosAVencer = (List<Map<String, Object>>) datosInventario.get("listaProximosAVencer");
        List<Map<String, Object>> listaStockBajo = (List<Map<String, Object>>) datosInventario.get("listaStockBajo");
        // 2. Procesamos la Tabla de Vencimientos (Izquierda de la imagen)
        List<AlertaCaducidad> tablaCaducidad = new ArrayList<>();
        if(listaProximosAVencer!=null){
            for (Map<String, Object> lote : listaProximosAVencer) {
                Map<String, Object> prod =(Map<String, Object>) lote.get("producto");
                LocalDate fechaVenc = LocalDate.parse((String) lote.get("fechaVencimiento"));

                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVenc);
                String estado = "Proximo";
                if(diasRestantes < 0) estado = "Vencido";
                else if (diasRestantes == 0) estado = "Hoy vence";

                tablaCaducidad.add(new AlertaCaducidad(
                        (String) prod.get("nombre"),
                        (String) lote.get("codigoLote"),
                        fechaVenc.toString(),
                        diasRestantes,
                        estado
                ));
            }
        }
        // 3. Procesamos la Tabla de Reposición (Derecha de la imagen)
        List<AlertaStock> tablaStock = new ArrayList<>();
        if (listaStockBajo != null) {
            for (Map<String, Object> lote : listaStockBajo) {
                Map<String, Object> prod = (Map<String, Object>) lote.get("producto");

                tablaStock.add(new AlertaStock(
                        (String) prod.get("nombre"),
                        (Integer) lote.get("cantidad"),
                        (Integer) prod.get("stockMinimo"),
                        "Reponer"
                ));
            }
        }

        // Estructura unificada para que el Frontend dibuje todo el prototipo
        Map<String, Object> respuestaVisual = new HashMap<>();
        respuestaVisual.put("tablaCaducidad", tablaCaducidad);
        respuestaVisual.put("tablaStock", tablaStock);
        return respuestaVisual;
    }
}
