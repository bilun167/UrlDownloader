package downloader.sftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class BasicSftpClientTest {
	private SftpDownloadConfig config = mock(SftpDownloadConfig.class);
	private BasicSftpClient client;
	
	@Before
	public void init() {
		when(config.getPort()).thenReturn(22);
		when(config.getUser()).thenReturn("demo");
		when(config.getPassword()).thenReturn("password");
		when(config.getKnownHosts()).thenReturn("conf-sample/known_hosts");
		
		client = new BasicSftpClient(config);
	}
	
	@Test
	public void testConnect() throws Exception {
		client.connect(new URI("sftp://test.rebex.net/readme.txt"));
	}

	@Test
	public void testAnotherConnect() throws Exception {
		client.connect(new URI("sftp://demo:password@test.rebex.net/readme.txt"));
	}

}
