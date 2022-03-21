package com.example.fullcolordesign.models;

public class ModelProduct {
    private String productId,productTitulo,ProductDescripcion,productCategoria,productCantidad,
            productIcon,OriginalPrecio,timestamp,uid;

    public ModelProduct(){

    }

    public ModelProduct(String productId, String productTitulo, String productDescripcion,
                        String productCategoria, String productCantidad, String productIcon,
                        String originalPrecio, String timestamp, String uid) {
        this.productId = productId;
        this.productTitulo = productTitulo;
        this.ProductDescripcion = productDescripcion;
        this.productCategoria = productCategoria;
        this.productCantidad = productCantidad;
        this.productIcon = productIcon;
        this.OriginalPrecio = originalPrecio;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitulo() {
        return productTitulo;
    }

    public void setProductTitulo(String productTitulo) {
        this.productTitulo = productTitulo;
    }

    public String getProductDescripcion() {
        return ProductDescripcion;
    }

    public void setProductDescripcion(String productDescripcion) {
        ProductDescripcion = productDescripcion;
    }

    public String getProductCategoria() {
        return productCategoria;
    }

    public void setProductCategoria(String productCategoria) {
        this.productCategoria = productCategoria;
    }

    public String getProductCantidad() {
        return productCantidad;
    }

    public void setProductCantidad(String productCantidad) {
        this.productCantidad = productCantidad;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getOriginalPrecio() {
        return OriginalPrecio;
    }

    public void setOriginalPrecio(String originalPrecio) {
        OriginalPrecio = originalPrecio;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
