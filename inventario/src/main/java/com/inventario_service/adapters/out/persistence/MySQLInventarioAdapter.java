package com.inventario_service.adapters.out.persistence;

import com.inventario_service.domain.model.Categoria;
import com.inventario_service.domain.model.Lote;
import com.inventario_service.domain.model.Producto;
import com.inventario_service.domain.model.Sucursal;
import com.inventario_service.ports.out.InventarioOutputPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MySQLInventarioAdapter implements InventarioOutputPort {

    private final SpringDataInventarioRepository repository;
    private final SpringDataProductoRepository productoRepository;
    private final SpringDataCategoriaRepository categoriaRepository;
    private final SpringDataSucursalRepository sucursalRepository;

    public MySQLInventarioAdapter(SpringDataInventarioRepository repository,
                                  SpringDataProductoRepository productoRepository,
                                  SpringDataCategoriaRepository categoriaRepository,
                                  SpringDataSucursalRepository sucursalRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public Lote guardarLote(Lote lote) {
        String nombreCatInput = lote.getProducto().getCategoria() != null ? lote.getProducto().getCategoria().getNombreCategoria() : "MEDICAMENTOS";
        CategoriaEntity categoriaEntity = categoriaRepository.findByNombreCategoria(nombreCatInput.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("La categoría '" + nombreCatInput + "' no existe en MySQL."));

        String nombreSucursalInput = lote.getSucursal() != null ? lote.getSucursal().getNombreSucursal() : "FATIFARMA_ATE_CENTRAL";
        SucursalEntity sucursalEntity = sucursalRepository.findByNombreSucursal(nombreSucursalInput.toUpperCase().trim())
                .orElseThrow(() -> new RuntimeException("La sucursal '" + nombreSucursalInput + "' no existe en MySQL."));

        ProductoEntity productoEntity = ProductoEntity.builder()
                .id(lote.getProducto().getId())
                .nombre(lote.getProducto().getNombre())
                .categoria(categoriaEntity)
                .formaFarmaceutica(lote.getProducto().getFormaFarmaceutica())
                .presentacion(lote.getProducto().getPresentacion())
                .unidadInventario(lote.getProducto().getUnidadInventario())
                .unidadStock(lote.getProducto().getUnidadStock())
                .stockMinimo(lote.getProducto().getStockMinimo())
                .fiscalizadoDigemid(lote.getProducto().isFiscalizadoDigemid())
                .registroSanitario(lote.getProducto().getRegistroSanitario())
                .codigoMedicamento(lote.getProducto().getCodigoMedicamento())
                .build();

        ProductoEntity productoGuardado = productoRepository.save(productoEntity);

        LoteEntity loteEntity = LoteEntity.builder()
                .id(lote.getId())
                .codigoLote(lote.getCodigoLote())
                .cantidad(lote.getCantidad())
                .precioVenta(lote.getPrecioVenta())
                .javaFechaVencimiento(lote.getFechaVencimiento())
                .sucursal(sucursalEntity)
                .usuarioRegistro(lote.getUsuarioRegistro())
                .producto(productoGuardado)
                .ubicacionAnaquel(lote.getUbicacionAnaquel())
                .build();

        LoteEntity guardado = repository.save(loteEntity);
        return mapearAFormatoDominio(guardado);
    }

    @Override
    public List<Lote> buscarPorSucursalYCategoria(String sucursal, String categoria) {
        return repository.findBySucursal_NombreSucursalAndProducto_Categoria_NombreCategoria(sucursal, categoria)
                .stream().map(this::maFormatoDominio).collect(Collectors.toList());
    }

    @Override
    public List<Lote> buscarProximosAVencer(String sucursal, LocalDate fechaLimite) {
        return repository.findBySucursal_NombreSucursalAndJavaFechaVencimientoLessThanEqual(sucursal, fechaLimite)
                .stream().map(this::maFormatoDominio).collect(Collectors.toList());
    }

    @Override
    public List<Lote> buscarStockBajo(String sucursal) {
        return repository.findStockBajo(sucursal)
                .stream().map(this::maFormatoDominio).collect(Collectors.toList());
    }

    private Lote maFormatoDominio(LoteEntity entity) {
        return mapearAFormatoDominio(entity);
    }

    private Lote mapearAFormatoDominio(LoteEntity entity) {
        Categoria categoriaDominio = new Categoria(
                entity.getProducto().getCategoria().getId(),
                entity.getProducto().getCategoria().getNombreCategoria()
        );

        Sucursal sucursalDominio = new Sucursal(
                entity.getSucursal().getId(),
                entity.getSucursal().getNombreSucursal()
        );

        Producto productoDominio = Producto.builder()
                .id(entity.getProducto().getId())
                .nombre(entity.getProducto().getNombre())
                .categoria(categoriaDominio)
                .formaFarmaceutica(entity.getProducto().getFormaFarmaceutica())
                .presentacion(entity.getProducto().getPresentacion())
                .unidadInventario(entity.getProducto().getUnidadInventario())
                .unidadStock(entity.getProducto().getUnidadStock())
                .stockMinimo(entity.getProducto().getStockMinimo())
                .fiscalizadoDigemid(entity.getProducto().isFiscalizadoDigemid())
                .registroSanitario(entity.getProducto().getRegistroSanitario())
                .codigoMedicamento(entity.getProducto().getCodigoMedicamento())
                .build();

        return new Lote(
                entity.getId(),
                productoDominio,
                entity.getCodigoLote(),
                entity.getCantidad(),
                entity.getPrecioVenta(),
                entity.getJavaFechaVencimiento(),
                sucursalDominio,
                entity.getUbicacionAnaquel(),
                entity.getUsuarioRegistro()
        );
    }
}
