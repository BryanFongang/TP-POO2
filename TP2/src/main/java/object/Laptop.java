package object;

import java.sql.*;
import java.time.LocalDate;

public class Laptop extends Object {
    public Laptop(Integer codeBarre, LocalDate dateFabrication, String marque, String model, String stockage, String ram) {
        super(codeBarre, dateFabrication, marque, model, stockage, ram);
    }

    // Constructor avec tous les attributs
    public Laptop(Integer codeBarre, LocalDate dateFabrication, String marque, String model,
                  String stockage, String ram, boolean isStolen, LocalDate dateVol,
                  String proprietaire, String contactProprietaire) {
        super(codeBarre, dateFabrication, marque, model, stockage, ram);
        this.isStolen = isStolen;
        this.dateVol = dateVol;
        this.proprietaire = proprietaire;
        this.contactProprietaire = contactProprietaire;
    }

    @Override
    public Laptop rechercherDansBD(Connection conn, Integer codeBarre) throws SQLException {
        String query = "SELECT * FROM Laptop WHERE codeBarre = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codeBarre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                LocalDate dateFabrication = rs.getDate("dateFabrication").toLocalDate();
                String marque = rs.getString("marque");
                String model = rs.getString("model");
                String stockage = rs.getString("stockage");
                String ram = rs.getString("ram");
                boolean isStolen = rs.getBoolean("isStolen");

                LocalDate dateVol = null;
                if (rs.getDate("dateVol") != null) {
                    dateVol = rs.getDate("dateVol").toLocalDate();
                }

                String proprietaire = rs.getString("proprietaire");
                String contactProprietaire = rs.getString("contactProprietaire");

                return new Laptop(codeBarre, dateFabrication, marque, model, stockage, ram,
                        isStolen, dateVol, proprietaire, contactProprietaire);
            }
        }

        return null; // Aucun laptop trouvé avec ce code-barres
    }

    @Override
    public boolean signalerVol(Connection conn, LocalDate dateVol, String proprietaire, String contactProprietaire) throws SQLException {
        String query = "UPDATE Laptop SET isStolen = ?, dateVol = ?, proprietaire = ?, contactProprietaire = ? WHERE codeBarre = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBoolean(1, true);
            stmt.setDate(2, Date.valueOf(dateVol));
            stmt.setString(3, proprietaire);
            stmt.setString(4, contactProprietaire);
            stmt.setInt(5, this.codeBarre);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                this.isStolen = true;
                this.dateVol = dateVol;
                this.proprietaire = proprietaire;
                this.contactProprietaire = contactProprietaire;
                return true;
            }
        }

        return false;
    }

    public boolean enregistrerDansBD(Connection conn) throws SQLException {
        String query = "INSERT INTO Laptop (codeBarre, dateFabrication, marque, model, stockage, ram, isStolen, dateVol, proprietaire, contactProprietaire) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "dateFabrication = ?, marque = ?, model = ?, stockage = ?, ram = ?, isStolen = ?, dateVol = ?, proprietaire = ?, contactProprietaire = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert values
            stmt.setInt(1, this.codeBarre);
            stmt.setDate(2, Date.valueOf(this.dateFabrication));
            stmt.setString(3, this.marque);
            stmt.setString(4, this.model);
            stmt.setString(5, this.stockage);
            stmt.setString(6, this.ram);
            stmt.setBoolean(7, this.isStolen);

            if (this.dateVol != null) {
                stmt.setDate(8, Date.valueOf(this.dateVol));
            } else {
                stmt.setNull(8, Types.DATE);
            }

            stmt.setString(9, this.proprietaire);
            stmt.setString(10, this.contactProprietaire);

            // Update values (for ON DUPLICATE KEY UPDATE)
            stmt.setDate(11, Date.valueOf(this.dateFabrication));
            stmt.setString(12, this.marque);
            stmt.setString(13, this.model);
            stmt.setString(14, this.stockage);
            stmt.setString(15, this.ram);
            stmt.setBoolean(16, this.isStolen);

            if (this.dateVol != null) {
                stmt.setDate(17, Date.valueOf(this.dateVol));
            } else {
                stmt.setNull(17, Types.DATE);
            }

            stmt.setString(18, this.proprietaire);
            stmt.setString(19, this.contactProprietaire);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}