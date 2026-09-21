package com.venta.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime; // <-- NUEVO IMPORT OBLIGATORIO

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecetaMedicaEmbeddable {

    @Column(name = "receta_numero")
    private String numeroReceta;

    @Column(name = "medico_nombre")
    private String medicoNombre;

    @Column(name = "medico_colegiatura")
    private String colegiaturaColegioMedico;

    @Column(name = "medico_rne")
    private String registroEspecialista;

    @Column(name = "receta_fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "receta_fecha_vigencia")
    private LocalDate fechaVigencia;

    @Column(name = "cliente_dni")
    private String dniCliente;

    @Lob
    @Column(name = "receta_fotocopiada_base64", columnDefinition = "LONGTEXT")
    private String imagenBase64; // Guarda la foto de la fotocopia del folder de auditoría

    // =========================================================================
    // CANDADO DE AUDITORÍA FORENSE DE LA DIGEMID (Agregado para la HU15)
    // =========================================================================
    @Column(name = "receta_fecha_registro_sistema")
    private LocalDateTime fechaRegistroSistema; // <-- Sella la fecha y hora en que se digitalizó el archivo en la BD
}
