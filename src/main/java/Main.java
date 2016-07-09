import com.google.inject.Guice;
import com.google.inject.Injector;
import guice.MainModule;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by taihuynh on 9/7/16.
 */
public class Main {
    public static void main(String[] args) {
        Injector dlInjector = Guice.createInjector(new MainModule());
        HttpDownloader httpDownloader = dlInjector.getInstance(HttpDownloader.class);
        try {
            httpDownloader.download(new URI("http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
