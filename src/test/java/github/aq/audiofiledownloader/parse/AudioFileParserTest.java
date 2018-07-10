package github.aq.audiofiledownloader.parse;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class AudioFileParserTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldReturnAudioLinks() {
        List<String> links = AudioFileParser.parseAudioLinks(Paths.get("src/test/resources/index.html"));
        assertEquals(155, links.size());
        for (String link: links) {
            assertTrue(link.endsWith(".mp3"));
        }
    }

}
