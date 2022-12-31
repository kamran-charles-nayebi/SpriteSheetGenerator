import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ImageRotatorService extends Service<Void> {

    private java.util.List<File> files;

    private File savePath;

    public ImageRotatorService(List<File> files, File savePath) {
        this.files = files;
        this.savePath = savePath;
    }

    @Override
    protected Task<Void> createTask() {
        return new ImageRotatorTask();
    }

    class ImageRotatorTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            if (files == null)
                return null;
            for (int i = 0; i < files.size(); i++) {
                updateProgress(i+1, files.size());
                BufferedImage sprite = ImageIO.read(files.get(i));
                BufferedImage rotatedImage = rotate(sprite);
                ImageIO.write(sprite, "png", new File(savePath.getAbsolutePath() + File.separator + "0 " + files.get(i).getName()));
                ImageIO.write(rotatedImage, "png", new File(savePath.getAbsolutePath() + File.separator + "90 " + files.get(i).getName()));
                rotatedImage = rotate(rotatedImage);
                ImageIO.write(rotatedImage, "png", new File(savePath.getAbsolutePath() + File.separator + "180 " + files.get(i).getName()));
                rotatedImage = rotate(rotatedImage);
                ImageIO.write(rotatedImage, "png", new File(savePath.getAbsolutePath() + File.separator + "270 " + files.get(i).getName()));
            }
            return null;
        }

        public static BufferedImage rotate(BufferedImage img)
        {

            // Getting Dimensions of image
            int width = img.getWidth();
            int height = img.getHeight();

            // Creating a new buffered image
            BufferedImage newImage = new BufferedImage(
                    img.getWidth(), img.getHeight(), img.getType());

            // creating Graphics in buffered image
            Graphics2D g2 = newImage.createGraphics();

            // Rotating image by degrees using toradians()
            // method
            // and setting new dimension t it
            g2.rotate(Math.toRadians(90), width / 2,
                    height / 2);
            g2.drawImage(img, null, 0, 0);

            // Return rotated buffer image
            return newImage;
        }
    }
}

