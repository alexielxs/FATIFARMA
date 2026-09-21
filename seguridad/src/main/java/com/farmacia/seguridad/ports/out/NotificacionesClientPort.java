package com.farmacia.seguridad.ports.out;

import java.util.Map;

public interface NotificacionesClientPort {
    void enviarAlertaRecuperacion(Map<String, Object> datosAlerta);
}
