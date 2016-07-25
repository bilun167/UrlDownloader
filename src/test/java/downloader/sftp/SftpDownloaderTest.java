package downloader.sftp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import generator.FileNameGenerator;

public class SftpDownloaderTest {
    private FileNameGenerator fng = mock(FileNameGenerator.class);
    private BasicSftpClient client = mock(BasicSftpClient.class);
    private JSch sftp = mock(JSch.class);
    private Session session = mock(Session.class);
    private ChannelSftp channel = mock(ChannelSftp.class);
    
    private SftpDownloader dl = new SftpDownloader(fng, client);
    
	@Test
	public void testGetFile() throws Exception {
		URI uri = new URI("sftp://demo:password@test.rebex.net/readme.txt");
		when(fng.generate(uri)).thenReturn("readme.txt");
		when(client.getSftp()).thenReturn(sftp);
		when(client.connect(uri)).thenReturn(session);
		when(session.openChannel(uri.getScheme())).thenReturn(channel);
		when(channel.get(uri.getPath()))
			.thenReturn(new FileInputStream("src/test/resources/readme.txt"));
		
		File file = dl._getFile(uri);
		
		verify(fng, times(1)).generate(uri);
		verify(client, times(1)).connect(uri);
		verify(session, times(1)).openChannel(uri.getScheme());
		verify(channel, times(1)).get(uri.getPath());
		verify(fng, times(1)).generate(uri);
		assertThat(file.getName(), equalTo("readme.txt"));
	}

}
