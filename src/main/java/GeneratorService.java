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
            if (files == null)
                return null;
            BufferedImage spriteSheet = new BufferedImage(spriteSheetX * imageX, spriteSheetY * imageY, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < files.size(); i++) {
                updateProgress(i+1, files.size());
                BufferedImage sprite = ImageIO.read(files.get(i));
                drawSpriteToSheet(spriteSheet, sprite, i);
            }
            return spriteSheet;
        }

        /**
         * Draws a sprite to a sprite sheet
         * @param spriteSheet The sprite sheet to draw on
         * @param sprite The sprite to draw on the sprite sheet
         * @param index The index of the sprite on the sprite sheet (desired position)
         */
        private void drawSpriteToSheet(BufferedImage spriteSheet, BufferedImage sprite, int index) {
            if (sprite == null)
                return;
            if (spriteSheet == null)
                return;

            for (int x = 0; x < imageX; x++) {
                for (int y = 0; y < imageY; y++) {
                    spriteSheet.setRGB(x + index % spriteSheetX * imageX, y + index / spriteSheetX * imageY, sprite.getRGB(x, y));
                }
            }
        }

    }
}
