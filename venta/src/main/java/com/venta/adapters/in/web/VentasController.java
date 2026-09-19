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
    // MOMENTO 1: COBRAR EN CAJA (Venta Financiera Inmutable)
    // =========================================================================
    @PostMapping("/procesar")
    public ResponseEntity<?> registrarCompra(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Email", required = false) String emailAutenticado) {
        try {
            // Auditoría automática del usuario responsable
            String usuarioResponsable = (emailAutenticado != null) ? emailAutenticado : "caja_anonima@fatifarma.com";

            // 1. Extraer los productos del carrito de compras
            List<Map<String, Object>> detallesMap = (List<Map<String, Object>>) request.get("detalles");
            List<DetalleVenta> detalles = detallesMap.stream().map(d -> new DetalleVenta(
                    null,
                    Long.parseLong(d.get("loteId").toString()),
                    null,
                    d.get("amount") != null ? (Integer) d.get("amount") : (Integer) d.get("cantidad"),
                    null
            )).collect(Collectors.toList());

            // 2. Si tu prima decide subir los datos de la receta en el mismo instante del cobro
            RecetaMedica recetaDirecta = null;
            if (request.containsKey("receta") && request.get("receta") != null) {
                Map<String, Object> recMap = (Map<String, Object>) request.get("receta");
                recetaDirecta = new RecetaMedica(
                        (String) recMap.get("numeroReceta"),
                        (String) recMap.get("medicoNombre"),
                        (String) recMap.get("colegiaturaColegioMedico"),
                        (String) recMap.get("registroEspecialista"),
                        LocalDate.parse((String) recMap.get("fechaEmision")),
                        LocalDate.parse((String) recMap.get("fechaVigencia")),
                        (String) recMap.get("dniCliente"),
                        (String) recMap.get("imagenBase64")
                );
            }

            // Validar que se reciba el método de pago para evitar punteros nulos
            String metodoPagoInput = request.containsKey("metodoPago") && request.get("metodoPago") != null
                    ? ((String) request.get("metodoPago")).toUpperCase().trim()
                    : "EFECTIVO";

            // 3. Construir la cabecera maestra utilizando el nuevo constructor de 9 parámetros
            Venta venta = new Venta(
                    null,
                    null, // El Caso de Uso autodetectará e insertará "NORMAL" o "CON_RECETA"
                    null,
                    null,
                    usuarioResponsable,
                    ((String) request.get("sucursal")).toUpperCase().trim(),
                    metodoPagoInput, // <-- NUEVO: Pasa el método de pago (EFECTIVO, YAPE)
                    detalles,
                    recetaDirecta
            );

            Venta procesada = ventasUseCase.procesarVenta(venta);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Transacción grabada correctamente en la caja registradora",
                    "id_venta", procesada.getId(),
                    "total_cobrado", procesada.getTotal(),
                    "tipo_venta_aplicado", procesada.getTipoVenta()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // MOMENTO 2: ADJUNTAR LA RECETA FOTOCOPIADA AL SISTEMA (Auditoría Posterior)
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

            // Mapeamos el bloque completo de la fotocopia con el DNI y la foto Base64
            RecetaMedica recetaFotocopiada = new RecetaMedica(
                    (String) recMap.get("numeroReceta"),
                    (String) recMap.get("medicoNombre"),
                    (String) recMap.get("colegiaturaColegioMedico"),
                    (String) recMap.get("registroEspecialista"),
                    LocalDate.parse((String) recMap.get("fechaEmision")),
                    LocalDate.parse((String) recMap.get("fechaVigencia")),
                    (String) recMap.get("dniCliente"),
                    (String) recMap.get("imagenBase64")
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
