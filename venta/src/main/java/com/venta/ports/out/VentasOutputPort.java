package com.venta.ports.out;

import com.venta.domain.model.Venta;

import java.util.Optional;

public interface VentasOutputPort {
    Venta guardarVenta(Venta venta);
    Optional<Venta> buscarPorId(Long idVenta);
}
