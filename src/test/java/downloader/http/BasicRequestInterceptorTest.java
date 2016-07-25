package downloader.http;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;


public class BasicRequestInterceptorTest {
	private HttpRequest request = mock(HttpRequest.class);
	private HttpContext context = mock(HttpContext.class);
	
	private BasicRequestInterceptor sut = new BasicRequestInterceptor();
	
	@Test
	public void testProcess() throws Exception {
		when(request.containsHeader("Accept")).thenReturn(false);
		when(request.containsHeader("Accept-Language")).thenReturn(false);
		when(request.containsHeader("Cache-Control")).thenReturn(false);
		when(request.containsHeader("Accept-Encoding")).thenReturn(false);
		
		sut.process(request, context);
		
		verify(request, times(4)).addHeader(any(String.class), any(String.class));
	}

	@Test
	public void testAnotherProcess() throws Exception {
		when(request.containsHeader("Accept")).thenReturn(true);
		when(request.containsHeader("Accept-Language")).thenReturn(false);
		when(request.containsHeader("Cache-Control")).thenReturn(false);
		when(request.containsHeader("Accept-Encoding")).thenReturn(true);
		
		sut.process(request, context);
		
		verify(request, times(2)).addHeader(any(String.class), any(String.class));
	}
}
