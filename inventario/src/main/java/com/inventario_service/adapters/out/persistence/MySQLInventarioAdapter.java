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

    public MySQLInventarioAdapter(SpringDataInventarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Lote guardarLote(Lote lote) {
        // 1. Convertimos el Modelo de Dominio de Producto a Entidad JPA incluyendo campos DIGEMID
        ProductoEntity productoEntity = ProductoEntity.builder()
                .id(lote.getProducto().getId())
                .nombre(lote.getProducto().getNombre())
                .categoria(lote.getProducto().getCategoria().name())
                .formaFarmaceutica(lote.getProducto().getFormaFarmaceutica())
                .presentacion(lote.getProducto().getPresentacion())
                .unidadInventario(lote.getProducto().getUnidadInventario())
                .unidadStock(lote.getProducto().getUnidadStock())
                .stockMinimo(lote.getProducto().getStockMinimo())
                .fiscalizadoDigemid(lote.getProducto().isFiscalizadoDigemid()) // <-- NUEVO: Control DIGEMID
                .registroSanitario(lote.getProducto().getRegistroSanitario())   // <-- NUEVO: Registro Sanitario
                .build();

        // 2. Convertimos el Modelo de Dominio de Lote a Entidad JPA
        LoteEntity loteEntity = LoteEntity.builder()
                .id(lote.getId())
                .codigoLote(lote.getCodigoLote())
                .cantidad(lote.getCantidad())
                .precioVenta(lote.getPrecioVenta())
                .fechaVencimiento(lote.getFechaVencimiento())
                .sucursal(lote.getSucursal().name())
                .usuarioRegistro(lote.getUsuarioRegistro())
                .producto(productoEntity)
                .build();

        LoteEntity guardado = repository.save(loteEntity);

        // 3. Traducimos el resultado guardado de regreso al formato del Dominio Puro
        return mapearAFormatoDominio(guardado);
    }

    @Override
    public List<Lote> buscarPorSucursalYCategoria(Sucursal sucursal, Categoria categoria) {
        return repository.findBySucursalAndProducto_Categoria(sucursal.name(), categoria.name())
                .stream()
                .map(this::mapearAFormatoDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lote> buscarProximosAVencer(Sucursal sucursal, LocalDate fechaLimite) {
        return repository.findBySucursalAndFechaVencimientoLessThanEqual(sucursal.name(), fechaLimite)
                .stream()
                .map(this::mapearAFormatoDominio)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lote> buscarStockBajo(Sucursal sucursal) {
        return repository.findStockBajo(sucursal.name())
                .stream()
                .map(this::mapearAFormatoDominio)
                .collect(Collectors.toList());
    }

    // Método auxiliar reutilizable para convertir de Entidad JPA hacia Dominio Puro
    private Lote mapearAFormatoDominio(LoteEntity entity) {
        // ACTUALIZADO: Construimos el objeto Producto con sus 10 parámetros correspondientes
        Producto productoDominio = new Producto(
                entity.getProducto().getId(),
                entity.getProducto().getNombre(),
                Categoria.valueOf(entity.getProducto().getCategoria()),
                entity.getProducto().getFormaFarmaceutica(),
                entity.getProducto().getPresentacion(),
                entity.getProducto().getUnidadInventario(),
                entity.getProducto().getUnidadStock(),
                entity.getProducto().getStockMinimo(),
                entity.getProducto().isFiscalizadoDigemid(), // <-- NUEVO: Mapeo DIGEMID
                entity.getProducto().getRegistroSanitario()   // <-- NUEVO: Mapeo Registro Sanitario
        );

        return new Lote(
                entity.getId(),
                entity.getCodigoLote(),
                entity.getCantidad(),
                entity.getPrecioVenta(),
                entity.getFechaVencimiento(),
                Sucursal.valueOf(entity.getSucursal()),
                productoDominio,
                entity.getUsuarioRegistro()
        );
    }
}
