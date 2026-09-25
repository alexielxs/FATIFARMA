package com.venta.domain.service;

import com.venta.domain.model.DetalleVenta;
import com.venta.domain.model.RecetaMedica;
import com.venta.domain.model.Venta;
import com.venta.ports.in.VentasInputPort;
import com.venta.ports.out.InventarioClientPort;
import com.venta.ports.out.VentasOutputPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class VentasUseCase implements VentasInputPort {

    private final VentasOutputPort ventasOutputPort;
    private final InventarioClientPort inventarioClientPort;

    public VentasUseCase(VentasOutputPort ventasOutputPort, InventarioClientPort inventarioClientPort) {
        this.ventasOutputPort = ventasOutputPort;
        this.inventarioClientPort = inventarioClientPort;
    }

    @Override
    public Venta procesarVenta(Venta venta) {
        double subtotalAcumulado = 0.0;
        boolean requiereControlDigemid = false;

        // 1. Validar el stock y capturar datos llamando al microservicio de Inventario
        for (DetalleVenta detalle : venta.getDetalles()) {
            Map<String, Object> datosLote = inventarioClientPort.buscarLotePorId(detalle.getLoteId());

            if (datosLote == null) {
                throw new RuntimeException("Error: El lote con ID " + detalle.getLoteId() + " no existe.");
            }

            int stockDisponible = (Integer) datosLote.get("cantidad");
            if (stockDisponible < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente. Unidades disponibles en lote: " + stockDisponible);
            }

            Map<String, Object> producto = (Map<String, Object>) datosLote.get("producto");
            boolean fiscalizado = (Boolean) producto.get("fiscalizadoDigemid");

            if (fiscalizado) {
                requiereControlDigemid = true;
            }

            double precio = Double.parseDouble(datosLote.get("precioVenta").toString());
            detalle.setPrecioUnitario(precio);
            detalle.setProductoNombre((String) producto.get("nombre"));
            subtotalAcumulado += (precio * detalle.getCantidad());
        }

        // =========================================================================
        // CLASIFICACIÓN LOGICA OBLIGATORIA INMUTABLE: CON_RECETA y NORMAL
        // =========================================================================
        if (requiereControlDigemid) {
            venta.setTipoVenta("CON_RECETA"); // <-- OBLIGATORIO: No se modifica

            // Si suben la receta de inmediato en caja, aplicamos la validación adaptativa de vigencia
            if (venta.getReceta() != null && venta.getReceta().getNumeroReceta() != null) {
                RecetaMedica receta = venta.getReceta();

                // Inyectamos la auditoría del sistema al momento del registro
                receta.setFechaRegistroSistema(LocalDateTime.now());

                // VALIDACIÓN ADAPTATIVA: Solo valida si la receta posee una fecha de vigencia (Institucionales)
                // Si es null (como en la receta del odontólogo), se ignora el bloqueo
                if (receta.getFechaVigencia() != null) {
                    if (receta.getFechaVigencia().isBefore(LocalDate.now())) {
                        throw new RuntimeException("VENTA RECHAZADA: La receta médica ingresada ya se encuentra vencida.");
                    }
                }
            }
        } else {
            venta.setTipoVenta("NORMAL"); // <-- OBLIGATORIO: No se modifica
        }

        // 3. Descontar las existencias físicas en el inventario llamando al puerto de red OpenFeign
        for (DetalleVenta detalle : venta.getDetalles()) {
            inventarioClientPort.descontarStockFisico(detalle.getLoteId(), detalle.getCantidad());
        }

        venta.setFechaVenta(LocalDateTime.now());
        venta.setTotal(subtotalAcumulado);

        return ventasOutputPort.guardarVenta(venta);
    }

    @Override
    public Venta adjuntarRecetaAVentaRealizada(Long idVenta, RecetaMedica receta) {
        Venta ventaExistente = ventasOutputPort.buscarPorId(idVenta)
                .orElseThrow(() -> new RuntimeException("Error: La boleta con ID " + idVenta + " no existe."));

        if (ventaExistente.getReceta() != null && ventaExistente.getReceta().getNumeroReceta() != null) {
            throw new RuntimeException("Error: Esta venta ya cuenta con una receta archivada en el folder digital.");
        }

        // VALIDACIÓN ADAPTATIVA PARA ARCHIVADO DIFERIDO:
        // Solo valida la vigencia contra el día histórico de la transacción si la fecha de vigencia no es nula.
        if (receta.getFechaVigencia() != null) {
            if (receta.getFechaVigencia().isBefore(ventaExistente.getFechaVenta().toLocalDate())) {
                throw new RuntimeException("Error Sanitario: La receta fotocopiada ya estaba vencida el día de la venta.");
            }
        }

        // =====================================================================
        // CANDADO DE AUDITORÍA Y TRAZABILIDAD:
        // La fecha original de la venta comercial se mantiene intacta.
        // Se inyecta la hora actual del servidor al momento preciso de subir el archivo.
        // =====================================================================
        receta.setFechaRegistroSistema(LocalDateTime.now());

        // Se le inyecta la receta fotocopiada y cambia formalmente de estado ante DIGEMID
        ventaExistente.setReceta(receta);
        ventaExistente.setTipoVenta("CON_RECETA"); // <-- OBLIGATORIO: Forzado por auditoría regulatoria

        return ventasOutputPort.guardarVenta(ventaExistente);
    }
}
