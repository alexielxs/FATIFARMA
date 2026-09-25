package com.farmacia.seguridad.ports.out;

import java.util.Map;

public interface NotificacionesClientPort {

    // =========================================================================
    // FLUJO DE CREDENCIALES: Envía el código de verificación para recuperar clave
    // =========================================================================
    void enviarAlertaRecuperacion(Map<String, Object> datosAlerta);

    // =========================================================================
    // CANDADO DE AUDITORÍA FORENSE: Notifica a la propietaria intentos de fraude
    // =========================================================================
    void enviarAlertaFraude(Map<String, Object> datosAlerta);
}
