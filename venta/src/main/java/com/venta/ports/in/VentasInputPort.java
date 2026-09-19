package com.venta.ports.in;

import com.venta.domain.model.RecetaMedica;
import com.venta.domain.model.Venta;

public interface VentasInputPort {
    Venta procesarVenta(Venta venta);
    Venta adjuntarRecetaAVentaRealizada(Long idVenta, RecetaMedica receta);
}
