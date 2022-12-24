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
            ImageIO.write(resizeImage(initialImage, initialImage.getWidth() * 2160/initialImage.getHeight(), 2160), "png", new File(path.getAbsolutePath() + "2160p.png"));
            ImageIO.write(resizeImage(initialImage, initialImage.getWidth() * 1440/initialImage.getHeight(), 1440), "png", new File(path.getAbsolutePath() + "1440p.png"));
            ImageIO.write(resizeImage(initialImage, initialImage.getWidth() * 1080/initialImage.getHeight(), 1080), "png", new File(path.getAbsolutePath() + "1080p.png"));
            ImageIO.write(resizeImage(initialImage, initialImage.getWidth() * 720/initialImage.getHeight(), 720), "png", new File(path.getAbsolutePath() + "720p.png"));
            return null;
        }
        BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
            if (originalImage == null)
                return null;

            Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
            BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
            return outputImage;
        }
    }
}
