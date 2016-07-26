package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import generator.FileNameGenerator;

/**
 * {@link Guice} module that is meant to bind production app dependencies.
 * Created by taihuynh on 8/7/16.
 */
public class MainModule extends AbstractModule {
    private FileNameGenerator fng = new FileNameGenerator();

    @Override
    protected void configure() {
        bind(FileNameGenerator.class).toInstance(fng);
        install(new HttpModule());
        install(new FtpModule());
        install(new SftpModule());
    }
}
