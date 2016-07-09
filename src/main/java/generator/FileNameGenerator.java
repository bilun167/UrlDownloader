package generator;

import com.google.inject.Singleton;

import java.net.URI;
import java.util.UUID;

/**
 * Created by taihuynh on 8/7/16.
 *
 * When a file name cannot be suggested from protocol connection metadata,
 * invoke this singleton for generating file name.
 */
@Singleton
public class FileNameGenerator {
    public static String generate(URI url) {
        String path = url.getPath();
        String fileName;
        while (path.lastIndexOf("/") == path.length() - 1)
            path = path.substring(0, path.length() - 1);

        if (path.lastIndexOf("/") >= 0)
            fileName = path.substring(path.lastIndexOf("/") + 1);
        else
            fileName = UUID.randomUUID().toString();

        return fileName;
    }
}
