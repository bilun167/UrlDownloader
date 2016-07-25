package demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import downloader.ftp.FtpDownloader;
import downloader.http.HttpDownloader;
import downloader.sftp.SftpDownloader;

public class DownloadExecutorTest {
	private DownloadExecutor dl = new DownloadExecutor();
	
	@Test
	public void testDownload() throws Exception {
		CompletableFuture<List<File>> cf = dl.download(
				"http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip",
		        "ftp://speedtest.tele2.net/1MB.zip",
				"sftp://demo:password@test.rebex.net/readme.txt");
		List<File> files = cf.join();
		
		assertThat(files.size(), equalTo(3));
		assertThat(files.get(0).getName(), equalTo("FL_insurance_sample.csv.zip"));
		assertThat(files.get(1).getName(), equalTo("1MB.zip"));
		assertThat(files.get(2).getName(), equalTo("readme.txt"));
	}

	@Test
	public void testGetDownloader() throws Exception {
		assertThat(dl.getDownloader("http://*"), instanceOf(HttpDownloader.class));
		assertThat(dl.getDownloader("ftp://*"), instanceOf(FtpDownloader.class));
		assertThat(dl.getDownloader("sftp://*"), instanceOf(SftpDownloader.class));
	}

}
