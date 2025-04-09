package object;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class VerifierController {

    @FXML
    private ComboBox<String> comboTypeObjet;

    @FXML
    private TextField txtCodeBarre;

    @FXML
    private VBox resultatContainer;

    @FXML
    private Label lblStatutVol;

    @FXML
    private Label lblMarque;

    @FXML
    private Label lblModel;

    @FXML
    private Label lblDateFabrication;

    @FXML
    private Label lblStockage;

    @FXML
    private Label lblRam;

    @FXML
    private Label lblDateVol;

    @FXML
    private Label lblContactProprietaire;

    @FXML
    private Label lblTitreVol;

    @FXML
    private Label lblTitreContact;

    @FXML
    private Label lblNotFound;

    @FXML
    public void initialize() {
        comboTypeObjet.getSelectionModel().selectFirst();
    }

    @FXML
    private void onRechercherClick() {
        String typeObjet = comboTypeObjet.getValue();
        String codeBarreText = txtCodeBarre.getText().trim();

        // Validation des champs
        if (codeBarreText.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un code-barres.");
            return;
        }

        Integer codeBarre;
        try {
            codeBarre = Integer.parseInt(codeBarreText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le code-barres doit être un nombre entier.");
            return;
        }

        // Recherche dans la base de données
        try (Connection conn = MainFrmApplication.getConnection()) {
            Object objetTrouve = null;

            if ("Phone".equals(typeObjet)) {
                Phone phone = new Phone(0, null, "", "", "", "");
                objetTrouve = phone.rechercherDansBD(conn, codeBarre);
            } else if ("Laptop".equals(typeObjet)) {
                Laptop laptop = new Laptop(0, null, "", "", "", "");
                objetTrouve = laptop.rechercherDansBD(conn, codeBarre);
            }

            // Affichage des résultats
            if (objetTrouve != null) {
                lblNotFound.setVisible(false);
                resultatContainer.setVisible(true);

                lblMarque.setText(objetTrouve.getMarque());
                lblModel.setText(objetTrouve.getModel());
                lblDateFabrication.setText(objetTrouve.getDateFabrication().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                lblStockage.setText(objetTrouve.getStockage());
                lblRam.setText(objetTrouve.getRam());

                if (objetTrouve.isStolen()) {
                    lblStatutVol.setText("⚠️ CET OBJET A ÉTÉ SIGNALÉ COMME VOLÉ ⚠️");
                    lblStatutVol.setTextFill(Color.RED);

                    lblTitreVol.setVisible(true);
                    lblDateVol.setVisible(true);
                    lblDateVol.setText(objetTrouve.getDateVol().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    lblTitreContact.setVisible(true);
                    lblContactProprietaire.setVisible(true);
                    lblContactProprietaire.setText(objetTrouve.getContactProprietaire());
                } else {
                    lblStatutVol.setText("✓ Cet objet n'a pas été signalé comme volé");
                    lblStatutVol.setTextFill(Color.GREEN);

                    lblTitreVol.setVisible(false);
                    lblDateVol.setVisible(false);
                    lblTitreContact.setVisible(false);
                    lblContactProprietaire.setVisible(false);
                }
            } else {
                resultatContainer.setVisible(false);
                lblNotFound.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la connexion à la base de données.");
        }
    }

    @FXML
    private void onRetourClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Acceuil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Système de Gestion des Objets Volés");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}