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
import util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;
import static util.ImageUtils.findRowsOfEmptyPixels;

public class EmptySpaceTrimmerService extends Service<Void> {

    private List<File> files;

    private File savePath;

    public EmptySpaceTrimmerService(List<File> files, File savePath) {
        this.files = files;
        this.savePath = savePath;
    }
    
    @Override
    protected Task<Void> createTask() {
        return new EmtpySpaceTrimmerTask();
    }

    class EmtpySpaceTrimmerTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            if (files == null)
                return null;
            for (int i = 0; i < files.size(); i++) {
                updateProgress(i+1, files.size());
                BufferedImage sprite = ImageIO.read(files.get(i));
                int rowsOfEmptyPixelsLeft = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.LEFT);
                int rowsOfEmptyPixelsTop = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.TOP);
                int rowsOfEmptyPixelsBottom = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.BOTTOM);
                int rowsOfEmptyPixelsRight = findRowsOfEmptyPixels(sprite, ImageUtils.SIDE.RIGHT);
                BufferedImage resizedImage = new BufferedImage(sprite.getWidth() - rowsOfEmptyPixelsRight - rowsOfEmptyPixelsLeft,
                        sprite.getHeight() - rowsOfEmptyPixelsBottom - rowsOfEmptyPixelsTop, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < resizedImage.getWidth(); x++) {
                    for (int y = 0; y < resizedImage.getHeight(); y++) {
                        resizedImage.setRGB(x, y, sprite.getRGB(rowsOfEmptyPixelsLeft + x, rowsOfEmptyPixelsTop + y));
                    }
                }
                ImageIO.write(resizedImage, "png", new File(savePath.getAbsolutePath() + File.separator + files.get(i).getName()));
            }
            return null;
        }

    }
}
