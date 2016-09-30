package vc.bjn.catalyst.test.jersey.client;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import javax.inject.Provider;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * All-in-one mocking of Jersey 2 client requests for classes under test which inject a Provider{@code <Client>}.
 * 
 * <h1>Usage</h1>
 * 
 * <h2><code>MyService.java</code></h2>
 * <pre>
 * {@code @}Service
 * public class MyService {
 *
 *     {@code @}Autowired private Provider{@code <Client>} httpClientProvider;
 *
 *     public String fetch(){
 *         try (AutoClosableClient client = new AutoClosableClientImpl(httpClientProvider.get())) {
 *             
 *             String response = client.target("http://127.0.0.1")
 *                 .path("path")
 *                 .queryParam("query", "value")
 *                 .request()
 *                 .get(String.class);
 *             return response;
 *             
 *         } catch(WebApplicationException | ProcessingException e){
 *             throw new RuntimeException("HTTP error", e);
 *         }
 *     }
 * }</pre>
 * 
 * <h2><code>MyServiceTest.java</code></h2>
 * <pre>
 * public class MyServiceTest {
 *
 *     private MyService myService;
 *     private MockClientProvider mockClientProvider;
 *
 *     {@code @}BeforeMethod
 *     private void init(){
 *         myService = new MyService();
 *         mockClientProvider = new MockClientProvider();
 *         Whitebox.setInternalState(myService, mockClientProvider);
 *     }
 *
 *     {@code @}Test
 *     public void testMyServiceFetch(){
 *         TestBuilder mockRequest = mock(TestBuilder.class);
 *         when(mockRequest.get(String.class)).thenReturn("sample text/plain HTTP response body");
 *         mockClientProvider.enqueueMockBuilder(mockRequest);
 *         
 *         String actual = myService.fetch();
 *         assertEquals(actual, "sample text/plain HTTP response body");
 *         
 *         verify(mockRequest).get(eq(String.class));
 *         verify(mockRequest).requested("http://127.0.0.1/path?query=value");
 *     }
 * }</pre>
 * 
 * @see TestWebTargetFactory
 */
public class MockClientProvider implements Provider<Client>, TestResponseProvider {

	private final TestResponseProvider testResponseProvider;

	public MockClientProvider(final TestResponseProvider testResponseProvider) {
		this.testResponseProvider = testResponseProvider;
	}

	public MockClientProvider() {
		this(new TestResponseProviderImpl());
	}

	@Override
	public Client get() {
		final Client mockClient = mock(Client.class);

		when(mockClient.target(anyString())).thenAnswer(new Answer<WebTarget>() {
			@Override
			public WebTarget answer(final InvocationOnMock invocation) throws Throwable {
				final String uri = invocation.getArgumentAt(0, String.class);
				return new TestJerseyWebTarget(uri, testResponseProvider);
			}
		});

		when(mockClient.target(any(URI.class))).thenAnswer(new Answer<WebTarget>() {
			@Override
			public WebTarget answer(final InvocationOnMock invocation) throws Throwable {
				final URI uri = invocation.getArgumentAt(0, URI.class);
				return new TestJerseyWebTarget(uri, testResponseProvider);
			}
		});

		return mockClient;
	}

	@Override
	public void enqueueMockBuilder(final TestBuilder mockBuilder) {
		testResponseProvider.enqueueMockBuilder(mockBuilder);
	}

	@Override
	public void reset() {
		testResponseProvider.reset();
	}

	@Override
	public TestBuilder getNextMockBuilder() {
		return testResponseProvider.getNextMockBuilder();
	}

	@Override
	public TestBuilder addNewMockBuilder() {
		return testResponseProvider.addNewMockBuilder();
	}

}