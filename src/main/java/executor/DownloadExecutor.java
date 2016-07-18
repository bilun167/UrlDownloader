package executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import downloader.Downloader;
import downloader.FtpDownloader;
import downloader.SftpDownloader;
import downloader.http.HttpDownloader;
import exception.DownloadException;
import guice.MainModule;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

/**
 * Created by taihuynh on 10/7/16.
 */
public class DownloadExecutor {
    public final int RETRY = 3;

    public static final Injector dlInjector;
    public static final ExecutorService executorService;
    public static final HttpDownloader httpDownloader;
    public static final FtpDownloader ftpDownloader;
    public static final SftpDownloader sftpDownloader;

    static {
        dlInjector = Guice.createInjector(new MainModule());
        httpDownloader = dlInjector.getInstance(HttpDownloader.class);
        ftpDownloader = dlInjector.getInstance(FtpDownloader.class);
        sftpDownloader = dlInjector.getInstance(SftpDownloader.class);

        ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("Downloader-%d").setDaemon(true).build();
        executorService = Executors.newFixedThreadPool(10, tf);
    }

    private Downloader getDownloader(String url) {
        if (url.startsWith("sftp"))
            return sftpDownloader;
        else if (url.startsWith("ftp"))
            return ftpDownloader;
        else
            return httpDownloader;
    }

    protected void __download__(String site, CompletableFuture<File> cf, int rem) {
        try {
            File f = getDownloader(site).download(site);
            cf.complete(f);
        } catch (DownloadException e) {
            if (rem > 0)
                __download__(site, cf, rem - 1);
            else
                cf.completeExceptionally(e);
        }
    }

    private CompletableFuture<List<File>> download(String... urls) {
        List<CompletableFuture<File>> futures = Arrays.stream(urls).map(site -> {
            CompletableFuture<File> cf = new CompletableFuture<>();
            // spawn Thread manually instead of supplyAsync to support
            // retry logic when exception occurs
            new Thread( () -> __download__(site, cf, RETRY)).start();
            return cf;
        }).collect(Collectors.toList());

        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

        return allDoneFuture.thenApply(v -> futures.stream().
                map(future -> future.join()).collect(Collectors.toList())
        );
    }

    public static void main(String[] args) {
        DownloadExecutor dl = new DownloadExecutor();
        CompletableFuture<List<File>> files =
                dl.download("http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip");

        //
        files.join();
    }
}
