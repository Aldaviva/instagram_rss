package vc.bjn.catalyst.test.jersey.client;

import static org.mockito.Mockito.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * <p>This class is meant to be used by other test utilities, not by tests themselves.</p><p>If no suitable test utility exists for your 
 * use case, you can make a new one and use this class internally.</p>
 * 
 * <h1>Usage</h1>
 * <p>You'll want to find some way to mock the process by which your class under test executes HTTP requests.</p>
 * <p>To see how we did this for classes under test that inject a {@code Provider<Client>}, check out {@link MockClientProvider}.</p>
 * <p>You basically need to use subclasses or mocks during tests to get your class under test to use {@link TestJerseyWebTarget} 
 * instead of WebTarget, and to supply this {@code TestResponseProvider} to the {@code TestJerseyWebTarget} so it can return your
 * mocked responses and call the special verification methods ({@link TestBuilder#requested(String)} and {@link TestBuilder#headerAdded(String, Object)}.
 */
public class TestResponseProviderImpl implements TestResponseProvider {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestResponseProviderImpl.class);

	private final Queue<TestBuilder> mockBuilders = new LinkedList<>();

	@Override
	public void enqueueMockBuilder(final TestBuilder mockBuilder) {
		if(mockBuilders.contains(mockBuilder)) {
			LOGGER.warn("This mock Jersey Builder has already been enqueued.\n"
			    + "Did you accidentally call .addNewMockBuilder() and then .enqueueMockBuilder()?\n"
			    + "If so, remove the call to .enqueueMockBuilder(). \n"
			    + "Do you want to return the same HTTP response for multiple requests? \n"
			    + "If so, then you can safely ignore this warning.");
		}
		mockBuilders.offer(mockBuilder);
	}

	@Override
	public TestBuilder getNextMockBuilder() {
		return mockBuilders.poll();
	}

	@Override
	public void reset() {
		mockBuilders.clear();
	}

	@Override
	public TestBuilder addNewMockBuilder() {
		final TestBuilder testBuilder = mock(TestBuilder.class);
		enqueueMockBuilder(testBuilder);
		return testBuilder;
	}
}