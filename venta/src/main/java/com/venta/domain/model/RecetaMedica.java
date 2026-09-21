package com.venta.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime; // <-- NUEVO IMPORT
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaMedica {
    private String numeroReceta;
    private String medicoNombre;
    private String colegiaturaColegioMedico;
    private String registroEspecialista;
    private LocalDate fechaEmision;
    private LocalDate fechaVigencia;
    private String dniCliente;
    private String imagenBase64;
    private LocalDateTime fechaRegistroSistema; // <-- NUEVO: Registra la hora exacta en que se sube el PDF/Foto
}
