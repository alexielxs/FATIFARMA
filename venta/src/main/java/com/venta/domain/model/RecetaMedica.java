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
    private LocalDate fechaVigencia; // Permite null para recetas como las del odontólogo
    private String dniCliente; // Opcional según el diseño de tu flujo
    private String imagenBase64; // Almacena la foto o PDF digitalizado de la receta
    private LocalDateTime fechaRegistroSistema; // <-- NUEVO: Registra la hora exacta en que se sube el PDF/Foto
}
