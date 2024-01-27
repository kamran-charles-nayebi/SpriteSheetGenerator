package util;

import java.awt.image.BufferedImage;

import static java.lang.Math.abs;

public class ImageUtils {
    public enum SIDE{TOP,RIGHT,LEFT,BOTTOM}

    public static int findRowsOfEmptyPixels(BufferedImage image, SIDE side) {
        int rowsOfEmptyPixels = 0, direction, start, wrap;
        boolean rotated = false;
        switch (side){
            case TOP: start = 0; direction = 1; wrap = image.getWidth(); break;
            case BOTTOM: start = image.getHeight() - 1; direction = -1; wrap = image.getWidth(); break;
            case LEFT: start = 0; direction = 1; wrap = image.getHeight(); rotated = true; break;
            case RIGHT: start = image.getWidth() - 1; direction = -1; wrap = image.getWidth(); rotated = true; break;
            default: throw new RuntimeException("No side provided");
        }
        int stop = start == 0 ? rotated ? image.getWidth(): image.getHeight() : 0;

        for (int x = start; x != stop && rowsOfEmptyPixels == 0; x += direction) {
            for (int y = 0; y < wrap; y++) {
                if ((image.getRGB(rotated ? x : y, rotated ? y : x) & 0xff000000) >>> 24 != 0){
                    rowsOfEmptyPixels = abs(x-start);
                }
            }
        }
        return rowsOfEmptyPixels;
    }
}
