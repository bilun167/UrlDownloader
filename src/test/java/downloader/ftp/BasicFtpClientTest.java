package downloader.ftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class BasicFtpClientTest {
	private FtpDownloadConfig config = mock(FtpDownloadConfig.class);
	private BasicFtpClient client;
	@Before
	public void init() {
		when(config.getPort()).thenReturn(21);
		when(config.getUser()).thenReturn("anonymous");
		when(config.getPassword()).thenReturn("");
		
		client = new BasicFtpClient(config);
	}
	
	@Test
	public void testConnect() throws Exception {
		client.connect(new URI("ftp://speedtest.tele2.net/1MB.zip"));
		assertThat(client.getFtp().getReplyCode(), equalTo(200));
	}

	@Test
	public void testAnotherConnect() throws Exception {
		client.connect(new URI("ftp://anonymous@speedtest.tele2.net/1MB.zip"));
		assertThat(client.getFtp().getReplyCode(), equalTo(200));
	}

}
