package com.venta.adapters.out.persistence;

import com.venta.domain.model.DetalleVenta;
import com.venta.domain.model.RecetaMedica;
import com.venta.domain.model.Venta;
import com.venta.ports.out.VentasOutputPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MySQLVentasAdapter implements VentasOutputPort {

    private final SpringDataVentasRepository repository;

    public MySQLVentasAdapter(SpringDataVentasRepository repository) {
        this.repository = repository;
    }

    @Override
    public Venta guardarVenta(Venta venta) {
        RecetaMedicaEmbeddable recetaEmbeddable = null;
        if (venta.getReceta() != null) {
            // Se utiliza el patrón Builder actualizado de RecetaMedicaEmbeddable
            recetaEmbeddable = RecetaMedicaEmbeddable.builder()
                    .numeroReceta(venta.getReceta().getNumeroReceta())
                    .medicoNombre(venta.getReceta().getMedicoNombre())
                    .colegiaturaColegioMedico(venta.getReceta().getColegiaturaColegioMedico())
                    .registroEspecialista(venta.getReceta().getRegistroEspecialista())
                    .fechaEmision(venta.getReceta().getFechaEmision())
                    .fechaVigencia(venta.getReceta().getFechaVigencia())
                    .dniCliente(venta.getReceta().getDniCliente())
                    .imagenBase64(venta.getReceta().getImagenBase64())
                    .fechaRegistroSistema(venta.getReceta().getFechaRegistroSistema()) // <-- PERSISTENCIA DE LA AUDITORÍA
                    .build();
        }

        VentaEntity ventaEntity = VentaEntity.builder()
                .id(venta.getId())
                .tipoVenta(venta.getTipoVenta())
                .fechaVenta(venta.getFechaVenta())
                .total(venta.getTotal())
                .usuarioResponsable(venta.getUsuarioResponsable())
                .sucursal(venta.getSucursal())
                .metodoPago(venta.getMetodoPago())
                .receta(recetaEmbeddable)
                .build();

        List<DetalleVentaEntity> detalleEntities = venta.getDetalles().stream()
                .map(d -> DetalleVentaEntity.builder()
                        .id(d.getId())
                        .loteId(d.getLoteId())
                        .productoNombre(d.getProductoNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .venta(ventaEntity)
                        .build())
                .collect(Collectors.toList());

        // Usamos el helper method que añadimos en tu VentaEntity para asegurar la relación bidireccional limpia
        ventaEntity.asignarDetalles(detalleEntities);

        VentaEntity guardada = repository.save(ventaEntity);
        return mapearADominio(guardada);
    }

    @Override
    public Optional<Venta> buscarPorId(Long idVenta) {
        return repository.findById(idVenta).map(this::mapearADominio);
    }

    private Venta mapearADominio(VentaEntity entity) {
        RecetaMedica recetaDominio = null;
        if (entity.getReceta() != null) {
            // El constructor de tu dominio puro RecetaMedica recupera los 9 campos de auditoría inmutable
            recetaDominio = new RecetaMedica(
                    entity.getReceta().getNumeroReceta(),
                    entity.getReceta().getMedicoNombre(),
                    entity.getReceta().getColegiaturaColegioMedico(),
                    entity.getReceta().getRegistroEspecialista(),
                    entity.getReceta().getFechaEmision(),
                    entity.getReceta().getFechaVigencia(),
                    entity.getReceta().getDniCliente(),
                    entity.getReceta().getImagenBase64(),
                    entity.getReceta().getFechaRegistroSistema() // <-- RECUPERACIÓN DE LA AUDITORÍA DESDE LA BD
            );
        }

        List<DetalleVenta> detallesDominio = entity.getDetalles().stream()
                .map(d -> new DetalleVenta(d.getId(), d.getLoteId(), d.getProductoNombre(), d.getCantidad(), d.getPrecioUnitario()))
                .collect(Collectors.toList());

        return new Venta(
                entity.getId(),
                entity.getTipoVenta(),
                entity.getFechaVenta(),
                entity.getTotal(),
                entity.getUsuarioResponsable(),
                entity.getSucursal(),
                entity.getMetodoPago(),
                detallesDominio,
                recetaDominio
        );
    }
}
