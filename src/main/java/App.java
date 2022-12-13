import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("fxml/interface.fxml"));
        Pane pane = loader.load();
        ((ButtonController)loader.getController()).setWindow(primaryStage);

        Scene scene = new Scene(pane);
        primaryStage.setTitle("Sprite sheet generator");
        primaryStage.setScene(scene);
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.show();
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (gigabytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1000000000));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (gigabytes): " +
                Runtime.getRuntime().totalMemory()/1000000000);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
