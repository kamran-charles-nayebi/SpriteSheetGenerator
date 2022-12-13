import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class ButtonController {

    public javafx.stage.Window window;

    private List<File> files;

    @FXML
    private Canvas canvas;

    @FXML
    private ProgressBar progressBar;

    int imageX, imageY, spriteSheetX, spriteSheetY;

    public void setWindow(javafx.stage.Window window){
        this.window = window;
        files = new ArrayList<>();
    }
    @FXML
    void generate(ActionEvent event){
        GeneratorService service = new GeneratorService(imageX, imageY, spriteSheetX, spriteSheetY, files);
        File location = new FileChooser().showSaveDialog(window);
        if (location != null) {
            service.setOnSucceeded(event1 -> {
                try {
                    ImageIO.write(service.getValue(), "png", new File(location.getAbsolutePath() + ".png"));
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            service.progressProperty().addListener((a, o, n) -> {
                if (n != null)
                    progressBar.setProgress(n.doubleValue());
            });
            service.setOnFailed(event1 -> {
                try {
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                    throw event1.getSource().getException();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            progressBar.setVisible(true);
            service.restart();
        }
    }

    @FXML
    void generateDiffRes(ActionEvent event){
        GeneratorService service = new GeneratorService(imageX, imageY, spriteSheetX, spriteSheetY, files);
        File location = new FileChooser().showSaveDialog(window);
        if (location != null) {
            service.setOnSucceeded(event1 -> {
                progressBar.setProgress(0);
                ScalorService scalorService = new ScalorService(spriteSheetX, spriteSheetY, service.getValue(), location);
                progressBar.setVisible(false);
                scalorService.setOnFailed(event2 -> {
                    try {
                        throw event2.getSource().getException();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
                scalorService.restart();
            });
            service.progressProperty().addListener((a, o, n) -> {
                if (n != null)
                    progressBar.setProgress(n.doubleValue());
            });
            progressBar.setVisible(true);
            service.restart();
        }
    }

    @FXML
    void reset(ActionEvent event) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        files = new ArrayList<>();
        progressBar.setVisible(false);
    }

    @FXML
    void addFiles(ActionEvent event) throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose files");

        files.addAll(fileChooser.showOpenMultipleDialog(window));

        if (files != null) {
            javafx.scene.image.Image imageTemp = new javafx.scene.image.Image(files.get(0).toURI().toURL().toString());
            imageX = (int) imageTemp.getWidth();
            imageY = (int) imageTemp.getHeight();
            spriteSheetX = getSmallestSquareSide(files.size());
            spriteSheetY = files.size() / spriteSheetX + 1;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            PreviewService previewService = new PreviewService(files);
            previewService.setOnSucceeded(event1 -> {
                progressBar.setProgress(0);
                List<ImageView> imageViews = previewService.getValue();
                for (int i = 0; i < files.size(); i++) {
                    imageViews.get(i).setPreserveRatio(false);
                    imageViews.get(i).setFitHeight(canvas.getWidth()/spriteSheetX);
                    imageViews.get(i).setFitWidth(canvas.getHeight()/spriteSheetX);
                    gc.drawImage(imageViews.get(i).snapshot(null, null), i % spriteSheetX * canvas.getWidth()/spriteSheetX, i / spriteSheetX * canvas.getHeight()/spriteSheetX);
                }
                imageViews = null;
                System.gc();
                progressBar.setVisible(false);
            });
            previewService.setOnFailed(event2 -> {
                try {
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                    throw event2.getSource().getException();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            previewService.progressProperty().addListener((a, o, n) -> {
                if (n != null)
                    progressBar.setProgress(n.doubleValue());
            });
            progressBar.setVisible(true);
            previewService.restart();
        }
    }

    private int getSmallestSquareSide(int num){
        return (int)Math.sqrt(num)+1;
    }

}
