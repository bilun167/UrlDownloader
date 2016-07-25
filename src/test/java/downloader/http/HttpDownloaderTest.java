package downloader.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import generator.FileNameGenerator;

public class HttpDownloaderTest {
	private FileNameGenerator fng = mock(FileNameGenerator.class);
    private BasicHttpClient client = mock(BasicHttpClient.class);
    private DefaultHttpClient http = mock(DefaultHttpClient.class);
    private CloseableHttpResponse response = mock(CloseableHttpResponse.class);
    private HttpEntity entity = mock(HttpEntity.class);
    private HttpDownloader dl = new HttpDownloader(fng, client);
    
    @Test
	public void testGetFile() throws Exception {
		URI uri = new URI("http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip");
		when(fng.generate(uri)).thenReturn("FL_insurance_sample.csv.zip");
		when(client.getHttpClient()).thenReturn(http);
		when(http.execute(any(HttpUriRequest.class))).thenReturn(response);
		when(response.getEntity()).thenReturn(entity);
		when(entity.getContent())
			.thenReturn(new FileInputStream("src/test/resources/FL_insurance_sample.csv.zip"));
		
		File file = dl._getFile(uri);
		
		verify(http, times(1)).execute(any(HttpGet.class));
		verify(fng, times(1)).generate(uri);
		verify(entity, times(1)).getContent();
		assertThat(file.getName(), equalTo("FL_insurance_sample.csv.zip"));
	}


}
