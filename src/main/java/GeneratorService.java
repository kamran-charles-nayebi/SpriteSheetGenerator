import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeneratorService extends Service<BufferedImage> {

    int imageX;
    int imageY;
    int spriteSheetX;
    int spriteSheetY;

    private List<File> files;

    public GeneratorService(int imageX, int imageY, int spriteSheetX, int spriteSheetY, List<File> files) {
        this.imageX = imageX;
        this.imageY = imageY;
        this.spriteSheetX = spriteSheetX;
        this.spriteSheetY = spriteSheetY;
        this.files = files;
    }

    @Override
    protected Task<BufferedImage> createTask() {
        return new GeneratorTask();
    }

    class GeneratorTask extends Task<BufferedImage> {

        @Override
        protected BufferedImage call() throws Exception {
            BufferedImage spriteSheet = null;
            if (files != null) {
                spriteSheet = new BufferedImage(spriteSheetX * imageX, spriteSheetY * imageY, BufferedImage.TYPE_INT_ARGB);
                for (int i = 0; i < files.size(); i++) {
                    updateProgress(i+1, files.size());
                    BufferedImage image = ImageIO.read(files.get(i));
                    for (int x = 0; x < imageX; x++) {
                        for (int y = 0; y < imageY; y++) {
                            spriteSheet.setRGB(x + i % spriteSheetX * imageX, y + i / spriteSheetX * imageY, image.getRGB(x, y));
                        }
                    }
                }
            }
            return spriteSheet;
        }
    }
}
