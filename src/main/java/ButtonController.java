import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    int imageX, imageY, spriteSheetX, spriteSheetY;

    public void setWindow(javafx.stage.Window window){
        this.window = window;
        files = new ArrayList<>();
    }
    @FXML
    void generate(ActionEvent event){
        GeneratorService service = new GeneratorService(imageX, imageY, spriteSheetX, spriteSheetY, files);
        service.setOnSucceeded(event1 -> {
            try {
                ImageIO.write(service.getValue(), "png", new File(new FileChooser().showSaveDialog(window).getAbsolutePath() + ".png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        service.restart();
    }

    @FXML
    void generateDiffRes(ActionEvent event){
        GeneratorService service = new GeneratorService(imageX, imageY, spriteSheetX, spriteSheetY, files);
        service.setOnSucceeded(event1 -> {
            ScalorService scalorService = new ScalorService(spriteSheetX,spriteSheetY,service.getValue(), new FileChooser().showSaveDialog(window));
            scalorService.restart();
        });
        service.restart();
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    @FXML
    void reset(ActionEvent event) {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        files = new ArrayList<>();
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
            for (int i = 0; i < files.size(); i++) {
                ImageView image = new ImageView(new javafx.scene.image.Image(files.get(i).toURI().toURL().toString()));
                image.setPreserveRatio(false);
                image.setFitHeight(canvas.getWidth()/spriteSheetX);
                image.setFitWidth(canvas.getHeight()/spriteSheetX);
                gc.drawImage(image.snapshot(null, null), i % spriteSheetX * canvas.getWidth()/spriteSheetX, i / spriteSheetX * canvas.getHeight()/spriteSheetX);
            }
        }
    }

    private int getSmallestSquareSide(int num){
        return (int)Math.sqrt(num)+1;
    }

}
