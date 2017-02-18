package com.aldaviva.instagram_rss.common.marshal;

import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.testng.Assert.*;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import javax.ws.rs.WebApplicationException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

@PrepareForTest(WireFeedOutput.class)
@PowerMockIgnore({"javax.ws.rs.*"})
public class ChannelWriterTest extends PowerMockTestCase {

    @Test
    public void size(){
        assertEquals(new ChannelWriter().getSize(null, null, null, null, null), -1);
    }

    @Test(expectedExceptions = {WebApplicationException.class})
    public void feedSerializationError() throws Exception {
        WireFeedOutput wireFeedOutput = mock(WireFeedOutput.class);
        doThrow(new FeedException("hargle")).when(wireFeedOutput).output(any(WireFeed.class), any(Writer.class));
        PowerMockito.whenNew(WireFeedOutput.class).withNoArguments().thenReturn(wireFeedOutput);

        Channel channel = new Channel();
        channel.setEncoding("UTF-8");

        OutputStream entityStream = new ByteArrayOutputStream();
        new ChannelWriter().writeTo(channel, null, null, null, null, null, entityStream);

    }
}
