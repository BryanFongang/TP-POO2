package object;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public abstract class Object {
    protected Integer codeBarre;
    protected LocalDate dateFabrication;
    protected String marque;
    protected String model;
    protected String stockage;
    protected String ram;
    protected boolean isStolen;
    protected LocalDate dateVol;
    protected String proprietaire;
    protected String contactProprietaire;

    public Object(Integer codeBarre, LocalDate dateFabrication, String marque, String model, String stockage, String ram) {
        this.codeBarre = codeBarre;
        this.dateFabrication = dateFabrication;
        this.marque = marque;
        this.model = model;
        this.stockage = stockage;
        this.ram = ram;
        this.isStolen = false;
        this.dateVol = null;
        this.proprietaire = null;
        this.contactProprietaire = null;
    }

    public Integer getCodeBarre() {
        return codeBarre;
    }

    public void setCodeBarre(Integer codeBarre) {
        this.codeBarre = codeBarre;
    }

    public LocalDate getDateFabrication() {
        return dateFabrication;
    }

    public void setDateFabrication(LocalDate dateFabrication) {
        this.dateFabrication = dateFabrication;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStockage() {
        return stockage;
    }

    public void setStockage(String stockage) {
        this.stockage = stockage;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public boolean isStolen() {
        return isStolen;
    }

    public void setStolen(boolean stolen) {
        isStolen = stolen;
    }

    public LocalDate getDateVol() {
        return dateVol;
    }

    public void setDateVol(LocalDate dateVol) {
        this.dateVol = dateVol;
    }

    public String getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getContactProprietaire() {
        return contactProprietaire;
    }

    public void setContactProprietaire(String contactProprietaire) {
        this.contactProprietaire = contactProprietaire;
    }

    public abstract Object rechercherDansBD(Connection con, Integer codeBarre) throws SQLException;

    public abstract boolean signalerVol(Connection con, LocalDate dateVol, String proprietaire, String contactProprietaire) throws SQLException;
}
