package com.inventario_service.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sucursales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_sucursal", nullable = false, unique = true)
    private String nombreSucursal;
}
