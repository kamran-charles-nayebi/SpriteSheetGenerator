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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreviewService extends Service<List<ImageView>> {
    List<File> files;

    public PreviewService(List<File> files) {
        this.files = files;
    }

    @Override
    protected Task<List<ImageView>> createTask() {
        return new ScalorTask();
    }

    class ScalorTask extends Task<List<ImageView>> {

        @Override
        protected List<ImageView> call() throws Exception {
            List<ImageView> list = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                updateProgress(i+1, files.size());
                list.add(new ImageView(new javafx.scene.image.Image(files.get(i).toURI().toURL().toString())));
            }
            return list;
        }
    }
}
