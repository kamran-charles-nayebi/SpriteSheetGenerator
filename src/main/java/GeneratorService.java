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

import static util.ImageUtils.SIDE.*;
import static util.ImageUtils.findRowsOfEmptyPixels;

public class GeneratorService extends Service<BufferedImage> {

    private static final int DEFAULT_ROWS_OF_EMPTY_PIXELS = Integer.MAX_VALUE;

    private int imageX;
    private int imageY;
    private int spriteSheetX;
    private int spriteSheetY;
    private int rowsOfEmptyPixelsLeft = DEFAULT_ROWS_OF_EMPTY_PIXELS;
    private int rowsOfEmptyPixelsTop = DEFAULT_ROWS_OF_EMPTY_PIXELS;
    private int rowsOfEmptyPixelsBottom = DEFAULT_ROWS_OF_EMPTY_PIXELS;
    private int rowsOfEmptyPixelsRight = DEFAULT_ROWS_OF_EMPTY_PIXELS;

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
        private void updateRowsOfEmptyPixels(BufferedImage sprite, ImageUtils.SIDE side) {
            int emptyPixels = findRowsOfEmptyPixels(sprite, side);
            switch (side) {
                case LEFT:
                    rowsOfEmptyPixelsLeft = Math.min(emptyPixels, rowsOfEmptyPixelsLeft);
                    break;
                case RIGHT:
                    rowsOfEmptyPixelsRight = Math.min(emptyPixels, rowsOfEmptyPixelsRight);
                    break;
                case TOP:
                    rowsOfEmptyPixelsTop = Math.min(emptyPixels, rowsOfEmptyPixelsTop);
                    break;
                case BOTTOM:
                    rowsOfEmptyPixelsBottom = Math.min(emptyPixels, rowsOfEmptyPixelsBottom);
                    break;
            }
        }

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
                    System.out.println(e.toString());
                }
                updateRowsOfEmptyPixels(sprite, TOP);
                updateRowsOfEmptyPixels(sprite, RIGHT);
                updateRowsOfEmptyPixels(sprite, BOTTOM);
                updateRowsOfEmptyPixels(sprite, LEFT);
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
