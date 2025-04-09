package object;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.SQLException;

public class MainFrmApplication extends Application {
    private static HikariDataSource dataSource;

    /*initialisation du pool de connection*/
    public static void initConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/stolen_objects");
            config.setUsername("root");
            config.setPassword("jujutsu");
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
            System.out.println("Pool de connexion initialisé !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*recpération de connection*/
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /*fermeture du pool*/
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initConnectionPool(); // Initialise le pool de connexion

        // Charge choix.fxml au lieu de hello-view.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Acceuil.fxml"));
        Scene scene = new Scene(loader.load(),600,500);
        primaryStage.setTitle("Répertoire de Contacts");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}