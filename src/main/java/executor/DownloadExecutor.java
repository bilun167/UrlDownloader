package executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import downloader.*;
import guice.MainModule;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
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

    private Downloader getDownloader(String url) {
        if (url.startsWith("sftp"))
            return sftpDownloader;
        else if (url.startsWith("ftp"))
            return ftpDownloader;
        else
            return httpDownloader;
    }

    public List<CompletableFuture<File>> download(String... urls) {
        return Arrays.stream(urls).map(site -> {
            CompletableFuture<File> cf = new CompletableFuture<>();
            CompletableFuture.supplyAsync(() -> {
                try {
                    File f = getDownloader(site).download(site);
                    cf.complete(f);
                    return f;
                } catch (DownloadException e) {
                    cf.completeExceptionally(e);
                    return null;
                }
            }, executorService);
            return cf;
        }).collect(Collectors.toList());
    }

    public CompletableFuture<List<File>> allDone(List<CompletableFuture<File>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.toList())
        );
    }

    public static void main(String[] args) {
        DownloadExecutor dl = new DownloadExecutor();
        List<CompletableFuture<File>> futures =
                dl.download("http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip",
                "ftp://speedtest.tele2.net/1MB.zip",
                "sftp://taihuynh@tais-mbp://Users/taihuynh/jayeson/workspace/jayeson.portal.admin/app-client/typings.json");
        dl.allDone(futures).join();
    }
}
