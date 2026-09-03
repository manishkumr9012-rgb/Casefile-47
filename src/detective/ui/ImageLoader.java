package detective.ui;

import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

/** Loads portraits from the IDE project or from the packaged JAR. */
public final class ImageLoader {
    private ImageLoader() {}

    public static Image load(String fileName) {
        // First try the classpath. This is what makes the runnable JAR self-contained.
        try (InputStream in = ImageLoader.class.getResourceAsStream("/images/" + fileName)) {
            if (in != null) return ImageIO.read(in);
        } catch (Exception ignored) {
            // Fall through to the IDE/project path below.
        }

        // Then support running directly from IntelliJ without configuring resources.
        try {
            File direct = new File("resources/images", fileName);
            if (direct.isFile()) return ImageIO.read(direct);
        } catch (Exception ignored) {
            // A missing portrait should never crash the investigation.
        }
        return null;
    }
}
