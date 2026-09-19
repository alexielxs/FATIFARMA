package com.venta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
}
