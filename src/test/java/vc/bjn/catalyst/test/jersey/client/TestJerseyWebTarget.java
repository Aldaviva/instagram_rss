package vc.bjn.catalyst.test.jersey.client;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientlessClientConfig;
import org.glassfish.jersey.client.JerseyInvocation.Builder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestJerseyWebTarget extends JerseyWebTarget {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestJerseyWebTarget.class);

	private final TestResponseProvider testResponseProvider;

	public TestJerseyWebTarget(final URI uri, final TestResponseProvider testResponseProvider) {
		super(UriBuilder.fromUri(uri), new ClientlessClientConfig());
		this.testResponseProvider = testResponseProvider;
	}

	public TestJerseyWebTarget(final String uri, final TestResponseProvider testResponseProvider) {
		super(UriBuilder.fromUri(uri), new ClientlessClientConfig());
		this.testResponseProvider = testResponseProvider;
	}

	public TestJerseyWebTarget(final JerseyWebTarget target, final TestResponseProvider testResponseProvider) {
		super(target.getUriBuilder(), target.getConfiguration());
		this.testResponseProvider = testResponseProvider;
	}

	@Override
	public Builder request() {
		return createTestBuilder();
	}

	@Override
	public Builder request(final String... acceptedResponseTypes) {
		return createTestBuilder();
	}

	@Override
	public Builder request(final MediaType... acceptedResponseTypes) {
		return createTestBuilder();
	}

	private TestBuilder createTestBuilder() {
		final TestBuilder builder = testResponseProvider.getNextMockBuilder();
		final String uri = getUri().toString();
		if(builder == null) {
			throw new RuntimeException("No mock builder found. Did you forget to call "
			    + "testWebTargetFactory.enqueueMockBuilder(mockRequest) for " + uri + "?");
		}

		doAnswer(new Answer<Builder>() {
			@Override
			public Builder answer(final InvocationOnMock invocation) throws Throwable {
				builder.headerAdded(invocation.getArgumentAt(0, String.class), invocation.getArgumentAt(1, Object.class));
				return builder;
			}
		}).when(builder).header(anyString(), any());

		builder.requested(uri);
		LOGGER.trace(uri);
		return builder;
	}

	@Override
	public JerseyWebTarget path(final String path) throws NullPointerException {
		final JerseyWebTarget newTarget = super.path(path);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget queryParam(final String name, final Object... values) throws NullPointerException {
		final JerseyWebTarget newTarget = super.queryParam(name, values);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget matrixParam(final String name, final Object... values) throws NullPointerException {
		final JerseyWebTarget newTarget = super.matrixParam(name, values);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplate(final String name, final Object value) throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplate(name, value);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplate(final String name, final Object value, final boolean encodeSlashInPath)
	    throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplate(name, value, encodeSlashInPath);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplateFromEncoded(final String name, final Object value) throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplateFromEncoded(name, value);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplates(final Map<String, Object> templateValues) throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplates(templateValues);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplates(final Map<String, Object> templateValues, final boolean encodeSlashInPath)
	    throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplates(templateValues, encodeSlashInPath);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

	@Override
	public JerseyWebTarget resolveTemplatesFromEncoded(final Map<String, Object> templateValues) throws NullPointerException {
		final JerseyWebTarget newTarget = super.resolveTemplatesFromEncoded(templateValues);
		return new TestJerseyWebTarget(newTarget, testResponseProvider);
	}

}