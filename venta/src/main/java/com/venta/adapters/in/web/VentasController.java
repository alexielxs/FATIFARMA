package com.venta.adapters.in.web;

import com.venta.domain.model.Venta;
import com.venta.domain.model.DetalleVenta;
import com.venta.domain.model.RecetaMedica;
import com.venta.domain.service.VentasUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/ventas")
public class VentasController {

    private final VentasUseCase ventasUseCase;

    public VentasController(VentasUseCase ventasUseCase) {
        this.ventasUseCase = ventasUseCase;
    }

    // =========================================================================
    // MOMENTO 1: COBRAR EN CAJA (HU09 - Venta Financiera Inmutable)
    // =========================================================================
    @PostMapping("/procesar")
    public ResponseEntity<?> registrarCompra(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Email", required = false) String emailAutenticado) {
        try {
            // Auditoría automática del usuario responsable
            String usuarioResponsable = (emailAutenticado != null) ? emailAutenticado : "caja_anonima@fatifarma.com";

            // 1. Extraer los productos del carrito de compras (HU10 - Soporte para venta fraccionada)
            List<Map<String, Object>> detallesMap = (List<Map<String, Object>>) request.get("detalles");
            List<DetalleVenta> detalles = detallesMap.stream().map(d -> DetalleVenta.builder()
                    .id(null)
                    .loteId(Long.parseLong(d.get("loteId").toString()))
                    .productoNombre(null) // El UseCase lo jalará por red desde inventario-service
                    .cantidad(d.get("amount") != null ? (Integer) d.get("amount") : (Integer) d.get("cantidad"))
                    .precioUnitario(null) // El UseCase inyectará el precio real de MySQL
                    .build()
            ).collect(Collectors.toList());

            // 2. Si se decide subir los datos de la receta en el mismo instante del cobro
            RecetaMedica recetaDirecta = null;
            if (request.containsKey("receta") && request.get("receta") != null) {
                Map<String, Object> recMap = (Map<String, Object>) request.get("receta");

                // CORREGIDO: Se añade 'null' al final para mapear el nuevo atributo fechaRegistroSistema
                recetaDirecta = new RecetaMedica(
                        (String) recMap.get("numeroReceta"),
                        (String) recMap.get("medicoNombre"),
                        (String) recMap.get("colegiaturaColegioMedico"),
                        (String) recMap.get("registroEspecialista"),
                        LocalDate.parse((String) recMap.get("fechaEmision")),
                        LocalDate.parse((String) recMap.get("fechaVigencia")),
                        (String) recMap.get("dniCliente"),
                        (String) recMap.get("imagenBase64"),
                        null // <-- La fecha de auditoría del sistema nace en vacío al cobrar
                );
            }

            // Validar que se reciba el método de pago para evitar punteros nulos en caja
            String metodoPagoInput = request.containsKey("metodoPago") && request.get("metodoPago") != null
                    ? ((String) request.get("metodoPago")).toUpperCase().trim()
                    : "EFECTIVO";

            // 3. Construcción de cabecera usando el Builder de Lombok (Inmune a fallos de orden)
            Venta venta = Venta.builder()
                    .id(null)
                    .tipoVenta(null) // El Caso de Uso autodetectará e insertará "NORMAL" o "CON_RECETA" de forma inmutable
                    .fechaVenta(null)
                    .total(null)
                    .usuarioResponsable(usuarioResponsable)
                    .sucursal(((String) request.get("sucursal")).toUpperCase().trim()) // Filtro geográfico relacional plano
                    .metodoPago(metodoPagoInput) // EFECTIVO, YAPE o PLIN
                    .detalles(detalles)
                    .receta(recetaDirecta)
                    .build();

            Venta procesada = ventasUseCase.procesarVenta(venta);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Transacción grabada correctamente en la caja registradora",
                    "id_venta", procesada.getId(),
                    "total_cobrado", "S/. " + String.format("%.2f", procesada.getTotal()),
                    "tipo_venta_aplicado", procesada.getTipoVenta()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // MOMENTO 2: ADJUNTAR LA RECETA AL FOLDER DIGITAL (Auditoría DIGEMID)
    // =========================================================================
    @PutMapping("/asociar-receta/{idVenta}")
    public ResponseEntity<?> asociarRecetaPosterior(
            @PathVariable Long idVenta,
            @RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> recMap = (Map<String, Object>) request.get("receta");
            if (recMap == null) {
                throw new RuntimeException("Error: Datos de la receta fotocopiada ausentes.");
            }

            // Mapeamos el bloque completo de la fotocopia (CORREGIDO: se añade 'null' inicial para la auditoría)
            RecetaMedica recetaFotocopiada = new RecetaMedica(
                    (String) recMap.get("numeroReceta"),
                    (String) recMap.get("medicoNombre"),
                    (String) recMap.get("colegiaturaColegioMedico"),
                    (String) recMap.get("registroEspecialista"),
                    LocalDate.parse((String) recMap.get("fechaEmision")),
                    LocalDate.parse((String) recMap.get("fechaVigencia")),
                    (String) recMap.get("dniCliente"),
                    (String) recMap.get("imagenBase64"),
                    null // <-- Nace en null; el caso de uso le estampará el LocalDateTime.now() inmutable
            );

            Venta ventaActualizada = ventasUseCase.adjuntarRecetaAVentaRealizada(idVenta, recetaFotocopiada);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Receta fotocopiada archivada correctamente en la boleta N° " + idVenta,
                    "estado_transaccion", "AUDITADO_Y_REGULARIZADO",
                    "tipo_venta_final", ventaActualizada.getTipoVenta()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
