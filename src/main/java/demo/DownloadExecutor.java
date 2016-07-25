package demo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import downloader.Downloader;
import downloader.ftp.FtpDownloader;
import downloader.http.HttpDownloader;
import downloader.sftp.SftpDownloader;
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

    protected Downloader getDownloader(String url) throws DownloadException {
        if (url.startsWith("sftp"))
            return sftpDownloader;
        else if (url.startsWith("ftp"))
            return ftpDownloader;
        else if (url.startsWith(("http")))
            return httpDownloader;
        else
            throw new DownloadException("Unsupported protocol.");
    }

    public CompletableFuture<List<File>> download(String... urls) {
        List<CompletableFuture<File>> futures = Arrays.stream(urls).map(site -> {
            CompletableFuture<File> cf = CompletableFuture.supplyAsync(() -> {
                File f = getDownloader(site).download(site);
                return f;
            }, executorService);
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
        CompletableFuture<List<File>> files = dl.download(args);
                /*dl.download("http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip",
                "ftp://speedtest.tele2.net/1MB.zip",
                "sftp://demo:password@test.rebex.net/readme.txt");*/
        files.join();
    }
}
