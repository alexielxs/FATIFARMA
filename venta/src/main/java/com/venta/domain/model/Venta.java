package com.venta.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Venta {
    private Long id;
    private String tipoVenta;
    private LocalDateTime fechaVenta;
    private Double total;
    private String usuarioResponsable;
    private String sucursal; // Propiedad de la farmacia
    private String metodoPago; // <-- NUEVO: Guardará "EFECTIVO", "YAPE", "PLIN" o "TARJETA"
    private List<DetalleVenta> detalles;
    private RecetaMedica receta;

    // Constructor vacío obligatorio para Jackson/Spring
    public Venta() {
    }

    // Constructor completo actualizado con los 9 parámetros exactos
    public Venta(Long id, String tipoVenta, LocalDateTime fechaVenta, Double total,
                 String usuarioResponsable, String sucursal, String metodoPago, List<DetalleVenta> detalles, RecetaMedica receta) {
        this.id = id;
        this.tipoVenta = tipoVenta;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.usuarioResponsable = usuarioResponsable;
        this.sucursal = sucursal;
        this.metodoPago = metodoPago; // <-- Asignación del nuevo campo
        this.detalles = detalles;
        this.receta = receta;
    }

    // ==========================================
    // GETTERS Y SETTERS MANUALES (INMUNE A ERRORES DE LOMBOK)
    // ==========================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public void setTipoVenta(String tipoVenta) {
        this.tipoVenta = tipoVenta;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getMetodoPago() { // <-- NUEVO GETTER
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago; 
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public RecetaMedica getReceta() {
        return receta;
    }

    public void setReceta(RecetaMedica receta) {
        this.receta = receta;
    }
}
