package booking.broker.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class BrokerMainArgs extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        final Logger logger = LoggerFactory.getLogger(getClass());

        URL url  = getClass().getClassLoader().getResource("broker.fxml");
        if (url != null) {

            FXMLLoader loader = new FXMLLoader(url);

            // Create a controller instance and set it in FXMLLoader
            BrokerController controller = new BrokerController();
            loader.setController(controller);

            Parent root = loader.load();
            primaryStage.setTitle("BROKER");

            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/broker.png")));
            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.show();
        }
        else {
            logger.error("Could not load frame from broker.fxml.");
        }
    }

}
