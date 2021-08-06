package com.example.rutasadm.Clases;

public class ColectivosLibres {
    private int id;
    private String num_ruta;
    private String num_econom;

    public ColectivosLibres() {
    }

    public ColectivosLibres(int id, String num_ruta, String num_econom) {
        this.id = id;
        this.num_ruta = num_ruta;
        this.num_econom = num_econom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNum_ruta() {
        return num_ruta;
    }

    public void setNum_ruta(String num_ruta) {
        this.num_ruta = num_ruta;
    }

    public String getNum_econom() {
        return num_econom;
    }

    public void setNum_econom(String num_econom) {
        this.num_econom = num_econom;
    }
}
