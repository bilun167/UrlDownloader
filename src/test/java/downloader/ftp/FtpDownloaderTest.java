package downloader.ftp;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Test;

import generator.FileNameGenerator;

public class FtpDownloaderTest {
    private FileNameGenerator fng = mock(FileNameGenerator.class);
    private BasicFtpClient client = mock(BasicFtpClient.class);
    private FTPClient ftp = mock(FTPClient.class);
    
    private FtpDownloader dl = new FtpDownloader(fng, client);
    
	@Test
	public void testGetFile() throws Exception {
		URI uri = new URI("ftp://speedtest.tele2.net/1MB.zip");
		when(fng.generate(uri)).thenReturn("1MB.zip");
		when(client.getFtp()).thenReturn(ftp);
		when(ftp.retrieveFileStream(uri.getPath()))
			.thenReturn(new FileInputStream("src/test/resources/1MB.zip"));
		when(ftp.completePendingCommand()).thenReturn(true);
		
		File file = dl._getFile(uri);
		
		verify(ftp, times(1)).retrieveFileStream(any(String.class));
		verify(fng, times(1)).generate(uri);
		assertThat(file.getName(), equalTo("1MB.zip"));
	}

}
