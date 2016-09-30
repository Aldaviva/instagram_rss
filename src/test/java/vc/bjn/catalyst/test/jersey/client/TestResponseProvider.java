package vc.bjn.catalyst.test.jersey.client;

/**
 * {@code TestResponseProvider} allows you to mock Jersey HTTP requests and responses. You'll probably want to use one of the 
 * implementing classes:
 * <ul><li>{@link MockClientProvider} if your class under test has an injected {@code Provider<Client>}</li>
 * <li>{@link vc.bjn.catalyst.listenerservice.remote.endpointcontrol.connection.TestWebTargetFactory TestWebTargetFactory} if your 
 * class under test is a Sleeper Cell {@link vc.bjn.catalyst.listenerservice.remote.endpointcontrol.dispatcher.Dispatcher Dispatcher}</li>
 * <li>{@link TestResponseProviderImpl} if you want to mock a {@link javax.ws.rs.client.Client Client} manually</li></ul>
 * 
 * <h2>Usage</h2>
 * See each of the above subclasses for usage examples.
 */
public interface TestResponseProvider {

	/**
	 * <p>Add a previously created and configured mock Jersey request Builder to this TestResponseProvider.</p>
	 * <p>The Builder will be appended to the list of mock responses to provide to the mock Jersey client. It will be provided 
	 * after any already-registered responses.</p>
	 * <p>If you created the Builder using {@link #addNewMockBuilder()}, you don't need to call this method.</p>
	 * 
	 * <h1>Usage</h1>
	 * <pre>
	 * TestBuilder request = mock(TestBuilder.class);
	 * when(request.get(eq(String.class))).thenReturn("my response body");
	 * mockClientProvider.enqueueMockBuilder(request);
	 * 
	 * myService.doRequest();
	 * 
	 * verify(request).requested("http://127.0.0.1:80/path?query=value");
	 * verify(request).get(eq("my request body"));
	 * </pre>
	 * 
	 * @see #addNewMockBuilder()
	 */
	void enqueueMockBuilder(TestBuilder mockBuilder);

	/**
	 * Internal method. The mock Jersey client will call this method when it wants to mock sending a request and getting a response.
	 * The result of this call will be used to generate the response based on how you mocked the {@code .get()} (or whatever) of that builder.
	 */
	TestBuilder getNextMockBuilder();

	/**
	 * If you want an existing instance of TestResponseBuilder to forget all enqueued TestBuilders, you can call {@code reset()}.
	 * Alternately, you can try automatically creating and wiring in new instances of TestResponseBuilder for every test method
	 * by using {@code @BeforeMethod}.
	 */
	void reset();

	/**
	 * <p>Create and automatically enqueue a new mocked {@link TestBuilder}, which is a shortcut for {@link #enqueueMockBuilder(TestBuilder)}.</p>
	 * <p>You should take the return value of this method and stub the {@code .get(String.class)} or whatever method is called by your class under test.
	 * It's here so you don't need to remember which constructor to call.</p>
	 * 
	 * <h1>Usage</h1>
	 * <pre>
	 * TestBuilder request = mockClientProvider.addNewMockBuilder();
	 * when(request.get(eq(String.class))).thenReturn("my response body");
	 * 
	 * myService.doRequest();
	 * 
	 * verify(request).requested("http://127.0.0.1:80/path?query=value");
	 * verify(request).get(eq("my request body"));
	 * </pre>
	 */
	TestBuilder addNewMockBuilder();

}