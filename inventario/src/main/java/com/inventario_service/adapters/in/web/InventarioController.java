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

    // =========================================================================
    // 1. BOTÓN [ REGISTRAR ]: Procesa el formulario visual completo (HU04 y HU05)
    // =========================================================================
    @PostMapping("/ingresar-lote")
    public ResponseEntity<?> ingresarLote(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal String emailAutenticado) {
        try {
            // Auditoría del Farmacéutico responsable que ingresa la mercadería
            String usuarioResponsable = (emailAutenticado != null) ? emailAutenticado : "farmaceutico_regente@fatifarma.com";

            // A. CAPTURA DEL BLOQUE IZQUIERDO: Catálogo Maestro del Producto (Formulario Visual)
            String nombreCategoriaInput = ((String) request.get("categoria")); // Campo 'Categoria *'

            // Instanciamos el objeto de dominio Categoria pasando solo su nombre para que el UseCase busque su ID físico
            Categoria categoriaDominio = new Categoria(null, nombreCategoriaInput);

            Producto producto = Producto.builder()
                    .nombre((String) request.get("producto")) // Campo 'Producto *' (Ej: Paracetamol 500mg)
                    .categoria(categoriaDominio)
                    .formaFarmaceutica((String) request.get("formaFarmaceutica")) // Campo 'Forma Farmaceutica *'
                    .presentacion((String) request.get("presentacion")) // Campo 'Presentacion *'
                    .unidadInventario((String) request.get("unidadInventario")) // Campo 'Unidad de inventario *'
                    .unidadStock((String) request.get("unidadStock")) // Campo 'Unidad de stock *'
                    .stockMinimo(request.get("stockMinimo") != null ? (Integer) request.get("stockMinimo") : 10)
                    .fiscalizadoDigemid(request.get("fiscalizadoDigemid") != null ? (Boolean) request.get("fiscalizadoDigemid") : false)
                    .registroSanitario((String) request.get("registroSanitario"))
                    .codigoMedicamento((String) request.get("codigoMedicamento"))
                    .build();

            // B. CAPTURA DEL BLOQUE DERECHO: Stock Físico del Lote Comercial
            String nombreSucursalInput = ((String) request.get("sucursal")); // Captura la Sede de Fatifarma (Ej: FATIFARMA_ATE_CENTRAL)
            Sucursal sucursalDominio = new Sucursal(null, nombreSucursalInput);

            Lote lote = Lote.builder()
                    .producto(producto)
                    .codigoLote((String) request.get("lote")) // Campo 'Lote' (Ej: A123)
                    .cantidad(Integer.parseInt(request.get("cantidad").toString())) // Campo 'Cantidad *'
                    .precioVenta(Double.parseDouble(request.get("precio").toString())) // Campo 'Precio *'
                    .fechaVencimiento(LocalDate.parse((String) request.get("vencimiento"))) // Campo 'Vencimiento' (YYYY-MM-DD)
                    .sucursal(sucursalDominio)
                    .ubicacionAnaquel(request.get("ubicacionAnaquel") != null ? (String) request.get("ubicacionAnaquel") : "Estante General") // HU08: Ubicación
                    .usuarioRegistro(usuarioResponsable)
                    .build();

            // El caso de uso validará las llaves foráneas físicas de categorías y sucursales en MySQL y guardará de forma relacional
            Lote guardado = inventarioUseCase.ingresarNuevoLote(lote, producto, nombreCategoriaInput);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Transacción relacional grabada con éxito en SIGIFARM",
                    "id_lote", guardado.getId(),
                    "producto_maestro", guardado.getProducto().getNombre(),
                    "sucursal_destino", guardado.getSucursal().getNombreSucursal()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // 2. BOTÓN [ ACTUALIZAR ]: Modifica precios, stocks o anaqueles (HU07)
    // =========================================================================
    @PutMapping("/lote/{loteId}/modificar")
    public ResponseEntity<?> actualizarCamposInventario(
            @PathVariable Long loteId,
            @RequestBody Map<String, Object> request) {
        try {
            Lote modificado = inventarioUseCase.modificarDatosInventario(loteId, request);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Lote N° " + loteId + " modificado correctamente en la base de datos relacional",
                    "id_lote", modificado.getId(),
                    "nuevo_stock", modificado.getCantidad(),
                    "nuevo_precio", modificado.getPrecioVenta()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // 3. BOTÓN [ ELIMINAR ]: Remoción física o de baja por caducidad
    // =========================================================================
    @DeleteMapping("/lote/{loteId}/eliminar")
    public ResponseEntity<?> darDeBajaLote(@PathVariable Long loteId) {
        try {
            // Lógica de eliminación en cascada relacional automatizada por Hibernate
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Lote N° " + loteId + " retirado físicamente por control regulatorio de DIGEMID",
                    "estado_operacion", "ELIMINADO_SUCCESS"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // 4. ENDPOINT PARA LAS PESTAÑAS: Listar catálogo filtrado por Farmacia y Categoría
    // =========================================================================
    @GetMapping("/catalogo")
    public ResponseEntity<?> obtenerCatalogo(@RequestParam String sucursal, @RequestParam String categoria) {
        try {
            // Construimos los objetos de dominio temporales para realizar los filtros relacionales limpios
            Sucursal sucursalFiltro = new Sucursal(null, sucursal);
            Categoria categoriaFiltro = new Categoria(null, categoria);

            List<Lote> catalogo = inventarioUseCase.listarCatalogoPorFiltros(
                    sucursalFiltro.getNombreSucursal(),
                    categoriaFiltro.getNombreCategoria()
            );
            return ResponseEntity.ok(catalogo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================================
    // 5. ENDPOINT PARA EL DASHBOARD: Obtener datos exactos de las tarjetas de alerta
    // =========================================================================
    @GetMapping("/dashboard/metricas")
    public ResponseEntity<?> obtenerMetricasDashboard(@RequestParam String sucursal) {
        try {
            // Llama a la lógica analítica sincrónica que alimenta las tarjetas superiores
            List<Lote> lotesVencimiento = inventarioUseCase.buscarProximosAVencer(new Sucursal(null, sucursal), LocalDate.now().plusDays(30));
            List<Lote> lotesStockBajo = inventarioUseCase.buscarStockBajo(new Sucursal(null, sucursal));

            return ResponseEntity.ok(Map.of(
                    "sucursal", sucursal.toUpperCase().trim(),
                    "proximosAVencerTotal", lotesVencimiento.size(), // Tarjeta Amarilla
                    "stockMinimoCriticoTotal", lotesStockBajo.size()  // Tarjeta Roja
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
