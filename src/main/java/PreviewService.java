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
