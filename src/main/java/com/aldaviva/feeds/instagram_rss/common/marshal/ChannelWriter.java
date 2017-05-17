package com.aldaviva.feeds.instagram_rss.common.marshal;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces("application/rss+xml")
public class ChannelWriter implements MessageBodyWriter<Channel> {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ChannelWriter.class);

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Channel.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(final Channel t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(final Channel t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException, WebApplicationException {
        final WireFeedOutput wireFeedOutput = new WireFeedOutput();
        final Charset channelCharset = t.getEncoding() != null ? Charset.forName(t.getEncoding()) : StandardCharsets.UTF_8;

        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(entityStream, channelCharset);
        try {
            wireFeedOutput.output(t, outputStreamWriter);
        } catch (final IllegalArgumentException | FeedException e) {
            LOGGER.error("Failed to serialize RSS channel to XML", e);
            throw new WebApplicationException("Failed to serialize RSS channel to XML", e, 500);
        }
    }

}
