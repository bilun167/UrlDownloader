package guice;

import com.google.inject.AbstractModule;
import generator.FileNameGenerator;

/**
 * Created by taihuynh on 8/7/16.
 */
public class MainModule extends AbstractModule {
    private FileNameGenerator fng = new FileNameGenerator();

    @Override
    protected void configure() {
        bind(FileNameGenerator.class).toInstance(fng);
        install(new HttpModule());
    }
}
