package com.example.fullcolordesign.models;

public class ModelShop {
    private String uid, email, nombre, nombrePilonera, telefono, deliveryFree, pais, provincia,
            ciudad, direccion, latitud, longitud, timestamp, tipoCuenta, online, shopOpen, imagenPerfil;

    public ModelShop() {

    }

    public ModelShop(String uid, String email, String nombre, String nombrePilonera, String telefono,
                     String deliveryFree, String pais, String provincia, String ciudad, String direccion,
                     String latitud, String longitud, String timestamp, String tipoCuenta, String online,
                     String shopOpen, String imagenPerfil) {
        this.uid = uid;
        this.email = email;
        this.nombre = nombre;
        this.nombrePilonera = nombrePilonera;
        this.telefono = telefono;
        this.deliveryFree = deliveryFree;
        this.pais = pais;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.timestamp = timestamp;
        this.tipoCuenta = tipoCuenta;
        this.online = online;
        this.shopOpen = shopOpen;
        this.imagenPerfil = imagenPerfil;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombrePilonera() {
        return nombrePilonera;
    }

    public void setNombrePilonera(String nombrePilonera) {
        this.nombrePilonera = nombrePilonera;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDeliveryFree() {
        return deliveryFree;
    }

    public void setDeliveryFree(String deliveryFree) {
        this.deliveryFree = deliveryFree;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }
}
