import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScalorService extends Service<Void> {
    int spriteSheetX;
    int spriteSheetY;

    BufferedImage initialImage;

    File path;

    public ScalorService(int spriteSheetX, int spriteSheetY, BufferedImage initialImage, File path) {
        this.spriteSheetX = spriteSheetX;
        this.spriteSheetY = spriteSheetY;
        this.initialImage = initialImage;
        this.path = path;
    }

    @Override
    protected Task<Void> createTask() {
        return new ScalorTask();
    }

    class ScalorTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            ImageIO.write(resizeImage(initialImage, spriteSheetX*2160, spriteSheetY*2160), "png", new File(path.getAbsolutePath() + "4k.png"));
            ImageIO.write(resizeImage(initialImage, spriteSheetX*1440, spriteSheetY*1440), "png", new File(path.getAbsolutePath() + "1440p.png"));
            ImageIO.write(resizeImage(initialImage, spriteSheetX*1080, spriteSheetY*1080), "png", new File(path.getAbsolutePath() + "1080p.png"));
            ImageIO.write(resizeImage(initialImage, spriteSheetX*720, spriteSheetY*720), "png", new File(path.getAbsolutePath() + "720p.png"));
            return null;
        }
        BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
            Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
            return outputImage;
        }
    }
}
