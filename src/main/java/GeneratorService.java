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
import util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static util.ImageUtils.findRowsOfEmptyPixels;

public class GeneratorService extends Service<BufferedImage> {

    int imageX;
    int imageY;
    int spriteSheetX;
    int spriteSheetY;
    int rowsOfEmptyPixelsLeft   = Integer.MAX_VALUE;
    int rowsOfEmptyPixelsTop    = Integer.MAX_VALUE;
    int rowsOfEmptyPixelsBottom = Integer.MAX_VALUE;
    int rowsOfEmptyPixelsRight  = Integer.MAX_VALUE;

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

            // Find the most amount of empty space that all sprites have in common
            for (File file : files) {
                BufferedImage sprite = null;
                try {
                    sprite = ImageIO.read(file);
                } catch (IOException e) {
                    System.out.println("Error while reading file:" + file.getName());
                }
                int newValue = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.LEFT);
                if (newValue < rowsOfEmptyPixelsLeft)
                    rowsOfEmptyPixelsLeft = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.LEFT);
                newValue = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.RIGHT);
                if (newValue < rowsOfEmptyPixelsRight)
                    rowsOfEmptyPixelsRight = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.RIGHT);
                newValue = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.TOP);
                if (newValue < rowsOfEmptyPixelsTop)
                    rowsOfEmptyPixelsTop = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.TOP);
                newValue = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.BOTTOM);
                if (newValue < rowsOfEmptyPixelsBottom)
                    rowsOfEmptyPixelsBottom = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.BOTTOM);
            }
            imageX -= rowsOfEmptyPixelsLeft + rowsOfEmptyPixelsRight;
            imageY -= rowsOfEmptyPixelsTop + rowsOfEmptyPixelsBottom;
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
                    if (x >= sprite.getWidth() || y >= sprite.getHeight())
                        spriteSheet.setRGB(x + index % spriteSheetX * imageX, y + index / spriteSheetX * imageY, 0);
                    else {
                        spriteSheet.setRGB(x + index % spriteSheetX * imageX, y + index / spriteSheetX * imageY,
                                sprite.getRGB(x + rowsOfEmptyPixelsLeft, y + rowsOfEmptyPixelsTop));
                    }
                }
            }
        }

    }
}
