package downloader.http;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class BasicResponseInterceptorTest {
	private HttpResponse response = mock(HttpResponse.class);
	private HttpContext context = mock(HttpContext.class);
	private HttpEntity entity = mock(HttpEntity.class);
	private Header header = mock(Header.class);
	private BasicHeaderElement he_gzip = mock(BasicHeaderElement.class);
	private BasicHeaderElement he_deflate = mock(BasicHeaderElement.class);
	
	private BasicResponseInterceptor sut = new BasicResponseInterceptor();
	
	@Test
	public void testProcess() throws Exception {
		when(response.getEntity()).thenReturn(entity);
		when(entity.getContentEncoding()).thenReturn(header);
		BasicHeaderElement[] hes = new BasicHeaderElement[1];
		hes[0] = he_gzip;
		when(header.getElements()).thenReturn(hes);
		when(he_gzip.getName()).thenReturn("gzip");
		
		sut.process(response, context);
		
		verify(response, times(1)).setEntity(any(GzipDecompressingEntity.class));
	}

	@Test
	public void testAnotherProcess() throws Exception {
		when(response.getEntity()).thenReturn(entity);
		when(entity.getContentEncoding()).thenReturn(header);
		BasicHeaderElement[] hes = new BasicHeaderElement[1];
		hes[0] = he_deflate;
		when(header.getElements()).thenReturn(hes);
		when(he_deflate.getName()).thenReturn("deflate");
		
		sut.process(response, context);
		
		verify(response, times(1)).setEntity(any(DeflateDecompressingEntity.class));
	}
}
