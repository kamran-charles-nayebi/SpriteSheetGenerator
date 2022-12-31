//SpriteSheetGenerator, a simple app that generates sprite sheets and does other useful tasks for making images usable in video games
//        Copyright (C) 2022 Kamran Charles Nayebi
//
//        This program is free software: you can redistribute it and/or modify
//        it under the terms of the GNU General Public License as published by
//        the Free Software Foundation, either version 3 of the License, or
//        (at your option) any later version.
//
//        This program is distributed in the hope that it will be useful,
//        but WITHOUT ANY WARRANTY; without even the implied warranty of
//        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//        GNU General Public License for more details.
//
//        You should have received a copy of the GNU General Public License
//        along with this program.  If not, see <https://www.gnu.org/licenses/>.
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ButtonController {

    public javafx.stage.Window window;

    private List<File> files;

    private File outputDirectory;

    @FXML
    private Canvas canvas;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private CheckBox previewCheckBox;

    int imageX, imageY, spriteSheetX, spriteSheetY;

    public void setWindow(javafx.stage.Window window){
        this.window = window;
        files = new ArrayList<>();
        loadSettings();
    }
    @FXML
    void generate(ActionEvent event){
        System.out.println("Before generating");
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (gigabytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1000000000));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (gigabytes): " +
                Runtime.getRuntime().totalMemory()/1000000000);
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

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(window);
        if (selectedFiles == null || selectedFiles.isEmpty())
            return;

        files.addAll(selectedFiles);

        if (files != null) {
            javafx.scene.image.Image imageTemp = new javafx.scene.image.Image(files.get(0).toURI().toURL().toString());
            imageX = (int) imageTemp.getWidth();
            imageY = (int) imageTemp.getHeight();
            spriteSheetX = getSmallestSquareSide(files.size());
            spriteSheetY = files.size() / spriteSheetX + 1;

            if (spriteSheetX * imageX > 16384){
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "This spritesheet will be over 16384 pixels, which is more than godot can use. Do you still want to proceed?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> answer = alert.showAndWait();
                answer.ifPresent(this::updateCanvas);
            }
        }
    }

    private int getSmallestSquareSide(int num){
        return (int) Math.ceil(Math.sqrt(num)); //Your fix wasn't quite right, this is better as it allow filling the canvas fully before resizing;
    }

    @FXML
    void setOutputDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose output directory");

        File directory = directoryChooser.showDialog(window);
        if (directory == null || directory.isFile())
            return;
        outputDirectory = directory;
    }

    @FXML
    void trimImages(ActionEvent event) {
        EmptySpaceTrimmerService service = new EmptySpaceTrimmerService(files, outputDirectory);
        service.setOnFailed(event1 -> {
            try {
                progressBar.setVisible(false);
                progressBar.setProgress(0);
                throw event1.getSource().getException();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        service.restart();
    }

    @FXML
    void rotateImages(ActionEvent event) {
        ImageRotatorService service = new ImageRotatorService(files, outputDirectory);
        service.setOnFailed(event1 -> {
            try {
                progressBar.setVisible(false);
                progressBar.setProgress(0);
                throw event1.getSource().getException();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        service.restart();
    }

    void updateCanvas(ButtonType buttonType) {
        if (buttonType.equals(ButtonType.YES) && previewCheckBox.isSelected()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            PreviewService previewService = new PreviewService(files);
            previewService.setOnSucceeded(event1 -> {
                progressBar.setProgress(0);
                List<ImageView> imageViews = previewService.getValue();
                canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // You forgot to clear it before drawing on it
                for (int i = 0; i < files.size(); i++) {
                    imageViews.get(i).setPreserveRatio(false);
                    imageViews.get(i).setFitHeight(canvas.getWidth() / spriteSheetX);
                    imageViews.get(i).setFitWidth(canvas.getHeight() / spriteSheetX);
                    gc.drawImage(imageViews.get(i).snapshot(null, null), i % spriteSheetX * canvas.getWidth() / spriteSheetX, i / spriteSheetX * canvas.getHeight() / spriteSheetX);
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

    @FXML
    void onCheckboxSelected(ActionEvent event) {
        try {
            FileWriter fw = new FileWriter("settings.txt");
            fw.write(String.valueOf(previewCheckBox.isSelected()));
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void loadSettings(){
        if (new File("settings.txt").exists()) {
            try {
                FileReader fr = new FileReader("settings.txt");
                Scanner sc = new Scanner(fr);
                previewCheckBox.setSelected(Boolean.parseBoolean(sc.next()));
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
