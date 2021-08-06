package com.example.rutasadm.Clases;

public class Persona {
    private int id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String usuario;
    private String contrasenia;
    private int tipo;
    private String agregado;
    private String modificado;
    private int colectivo;

    public Persona() {
    }

    public Persona(int id, String nombre, String apellidos, String telefono, String direccion, String usuario, String contrasenia, int tipo, String agregado, String modificado, int colectivo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.tipo = tipo;
        this.agregado = agregado;
        this.modificado = modificado;
        this.colectivo = colectivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getAgregado() {
        return agregado;
    }

    public void setAgregado(String agregado) {
        this.agregado = agregado;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public int getColectivo() {
        return colectivo;
    }

    public void setColectivo(int colectivo) {
        this.colectivo = colectivo;
    }
}
