package object;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class SignalerController {

    // Onglet Objet existant
    @FXML
    private ComboBox<String> comboTypeObjetExistant;

    @FXML
    private TextField txtCodeBarreExistant;

    @FXML
    private VBox objetTrouveContainer;

    @FXML
    private Label lblMarqueExistant;

    @FXML
    private Label lblModelExistant;

    @FXML
    private Label lblStatutVolExistant;

    @FXML
    private DatePicker dateVolExistant;

    @FXML
    private TextField txtProprietaireExistant;

    @FXML
    private TextField txtContactExistant;

    @FXML
    private Label lblNotFoundExistant;

    // Onglet Nouvel objet
    @FXML
    private ComboBox<String> comboTypeObjetNouveau;

    @FXML
    private TextField txtCodeBarreNouveau;

    @FXML
    private TextField txtMarqueNouveau;

    @FXML
    private TextField txtModelNouveau;

    @FXML
    private DatePicker dateFabricationNouveau;

    @FXML
    private TextField txtStockageNouveau;

    @FXML
    private TextField txtRamNouveau;

    @FXML
    private DatePicker dateVolNouveau;

    @FXML
    private TextField txtProprietaireNouveau;

    @FXML
    private TextField txtContactNouveau;

    // Variables privées pour stocker les objets trouvés
    private Object objetExistant;
    private String typeObjetExistant;

    @FXML
    public void initialize() {
        comboTypeObjetNouveau.getSelectionModel().selectFirst();

    }



    @FXML
    private void onSignalerExistantClick() {
        if (objetExistant == null) {
            showAlert("Erreur", "Aucun objet n'a été trouvé.");
            return;
        }

        // Validation des champs
        if (dateVolExistant.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date de vol.");
            return;
        }

        if (txtProprietaireExistant.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer le nom du propriétaire.");
            return;
        }

        if (txtContactExistant.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un contact pour le propriétaire.");
            return;
        }

        try (Connection conn = MainFrmApplication.getConnection()) {
            boolean success = objetExistant.signalerVol(
                    conn,
                    dateVolExistant.getValue(),
                    txtProprietaireExistant.getText().trim(),
                    txtContactExistant.getText().trim()
            );

            if (success) {
                showInfo("Succès", "L'objet a été signalé comme volé avec succès.");
                // Mettre à jour l'affichage
                lblStatutVolExistant.setText("⚠️ CET OBJET A ÉTÉ SIGNALÉ COMME VOLÉ ⚠️");
                lblStatutVolExistant.setTextFill(Color.RED);
            } else {
                showAlert("Erreur", "Une erreur est survenue lors du signalement de l'objet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la connexion à la base de données.");
        }
    }

    @FXML
    private void onEnregistrerNouveauClick() {
        String typeObjet = comboTypeObjetNouveau.getValue();

        // Validation des champs
        if (txtCodeBarreNouveau.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un code-barres.");
            return;
        }

        if (txtMarqueNouveau.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer une marque.");
            return;
        }

        if (txtModelNouveau.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un modèle.");
            return;
        }

        if (dateVolNouveau.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une date de vol.");
            return;
        }

        if (txtProprietaireNouveau.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer le nom du propriétaire.");
            return;
        }

        if (txtContactNouveau.getText().trim().isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un contact pour le propriétaire.");
            return;
        }

        Integer codeBarre;
        try {
            codeBarre = Integer.parseInt(txtCodeBarreNouveau.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le code-barres doit être un nombre entier.");
            return;
        }

        try (Connection conn = MainFrmApplication.getConnection()) {
            // Vérifier si l'objet existe déjà
            Object objetExistant = null;
            if ("Téléphone".equals(typeObjet)) {
                Phone phone = new Phone(0, null, "", "", "", "");
                objetExistant = phone.rechercherDansBD(conn, codeBarre);
            } else if ("Laptop".equals(typeObjet)) {
                Laptop laptop = new Laptop(0, null, "", "", "", "");
                objetExistant = laptop.rechercherDansBD(conn, codeBarre);
            }

            if (objetExistant != null) {
                showAlert("Erreur", "Un objet avec ce code-barres existe déjà. Utilisez l'onglet 'Objet existant'.");
                return;
            }

            // Créer et enregistrer le nouvel objet
            Object nouvelObjet;
            if ("Téléphone".equals(typeObjet)) {
                nouvelObjet = new Phone(
                        codeBarre,
                        dateFabricationNouveau.getValue(),
                        txtMarqueNouveau.getText().trim(),
                        txtModelNouveau.getText().trim(),
                        txtStockageNouveau.getText().trim(),
                        txtRamNouveau.getText().trim(),
                        true,  // isStolen
                        dateVolNouveau.getValue(),
                        txtProprietaireNouveau.getText().trim(),
                        txtContactNouveau.getText().trim()
                );

                boolean success = ((Phone) nouvelObjet).enregistrerDansBD(conn);
                if (success) {
                    showInfo("Succès", "Le téléphone a été enregistré et signalé comme volé avec succès.");
                    resetNouveauForm();
                } else {
                    showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement du téléphone.");
                }
            } else if ("Laptop".equals(typeObjet)) {
                nouvelObjet = new Laptop(
                        codeBarre,
                        dateFabricationNouveau.getValue(),
                        txtMarqueNouveau.getText().trim(),
                        txtModelNouveau.getText().trim(),
                        txtStockageNouveau.getText().trim(),
                        txtRamNouveau.getText().trim(),
                        true,  // isStolen
                        dateVolNouveau.getValue(),
                        txtProprietaireNouveau.getText().trim(),
                        txtContactNouveau.getText().trim()
                );

                boolean success = ((Laptop) nouvelObjet).enregistrerDansBD(conn);
                if (success) {
                    showInfo("Succès", "Le laptop a été enregistré et signalé comme volé avec succès.");
                    resetNouveauForm();
                } else {
                    showAlert("Erreur", "Une erreur est survenue lors de l'enregistrement du laptop.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la connexion à la base de données.");
        }
    }
    /*pour reintialiser le formulaire*/
    private void resetNouveauForm() {
        txtCodeBarreNouveau.clear();
        txtMarqueNouveau.clear();
        txtModelNouveau.clear();
        txtStockageNouveau.clear();
        txtRamNouveau.clear();
        dateFabricationNouveau.setValue(LocalDate.now());
        dateVolNouveau.setValue(LocalDate.now());
        txtProprietaireNouveau.clear();
        txtContactNouveau.clear();
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}