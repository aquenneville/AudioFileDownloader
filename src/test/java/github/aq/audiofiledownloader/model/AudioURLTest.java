package github.aq.audiofiledownloader.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AudioURLTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetUrl() {
        assertTrue(new AudioURL("http://www.gutenberg.org/media/files/9147").getUrl().equals("http://www.gutenberg.org/media/files/9147/"));
    }
    
    @Test
    public void testGetPath() {
        assertTrue(new AudioURL("http://www.gutenberg.org/media/files/9147").getPath().equals("/media/files/9147/"));
    }

}
