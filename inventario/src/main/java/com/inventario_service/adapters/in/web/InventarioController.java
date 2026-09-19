package com.inventario_service.adapters.in.web;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Producto;
import com.inventario_service.domain.model.Sucursal;
import com.inventario_service.domain.service.InventarioUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioUseCase inventarioUseCase;

    public InventarioController(InventarioUseCase inventarioUseCase) {
        this.inventarioUseCase = inventarioUseCase;
    }

    // 1. ENDPOINT PARA EL FORMULARIO: Registrar un Lote junto a su Producto y el Usuario responsable
    @PostMapping("/ingresar-lote")
    public ResponseEntity<?> ingresarLote(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal String emailAutenticado) {
        try {
            // Si realizas pruebas locales sin token, asignamos un usuario genérico para evitar errores nulos
            String usuarioResponsable = (emailAutenticado != null) ? emailAutenticado : "anonimo@fatifarma.com";

            // Extraer y construir los datos del Producto que viene dentro del JSON
            Map<String, Object> prodMap = (Map<String, Object>) request.get("producto");
            Producto producto = new Producto(
                    null,
                    (String) prodMap.get("nombre"),
                    Categoria.valueOf(((String) prodMap.get("categoria")).toUpperCase().trim()),
                    (String) prodMap.get("formaFarmaceutica"),
                    (String) prodMap.get("presentacion"),
                    (String) prodMap.get("unidadInventario"),
                    (String) prodMap.get("unidadStock"),
                    (Integer) prodMap.get("stockMinimo"),
                    (Boolean) prodMap.get("fiscalizadoDigemid"),
                    (String) prodMap.get("registroSanitario"),
                    (String) prodMap.get("codigoMedicamento") // <-- NUEVO: Recibe el código de catálogo institucional (Ej: 010400091)
            );

            // Construir el Lote con los 8 parámetros en el orden exacto de tu dominio
            Lote lote = new Lote(
                    null,
                    (String) request.get("codigoLote"),
                    (Integer) request.get("cantidad"),
                    Double.parseDouble(request.get("precioVenta").toString()),
                    LocalDate.parse((String) request.get("fechaVencimiento")),
                    Sucursal.valueOf(((String) request.get("sucursal")).toUpperCase().trim()),
                    producto,
                    usuarioResponsable
            );

            Lote guardado = inventarioUseCase.registrarIngresoLote(lote);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Lote e Inventario registrados con éxito",
                    "id_lote", guardado.getId(),
                    "registrado_por", guardado.getUsuarioRegistro()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. ENDPOINT PARA LAS PESTAÑAS: Listar catálogo filtrado por Farmacia y Categoría
    @GetMapping("/catalogo")
    public ResponseEntity<?> obtenerCatalogo(@RequestParam String sucursal, @RequestParam String categoria) {
        try {
            List<Lote> catalogo = inventarioUseCase.listarInventarioPorSucursalYCategoria(
                    Sucursal.valueOf(sucursal.toUpperCase().trim()),
                    Categoria.valueOf(categoria.toUpperCase().trim())
            );
            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. ENDPOINT PARA EL DASHBOARD: Obtener datos exactos de las tarjetas visuales
    @GetMapping("/dashboard/metricas")
    public ResponseEntity<?> obtenerMetricasDashboard(@RequestParam String sucursal) {
        try {
            Map<String, Object> metricas = inventarioUseCase.obtenerMetricasDashboard(
                    Sucursal.valueOf(sucursal.toUpperCase().trim())
            );
            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
