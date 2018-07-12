package github.aq.audiofiledownloader.download;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class FileDownloaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testShouldCreateDirectory() throws IOException {
        FileDownloader.createDirectoryIfNotExists(Paths.get("new-dir/new-dir"));
        assertTrue(new File("new-dir/new-dir/").exists());
        deleteDirectory(new File("new-dir/"));
    }
    
    @Test
    public void testShouldGetStoragePath() throws MalformedURLException {
        Path path = FileDownloader.getStoragePath("http://www.gutenberg.org/media/9147", "");
        assertTrue(path.toString().equals("www.gutenberg.org/media/9147"));
        path = FileDownloader.getStoragePath("http://www.gutenberg.org/media/9147", "test-storage");
        assertTrue(path.toString().equals("test-storage/www.gutenberg.org/media/9147"));
        path = FileDownloader.getStoragePath("http://www.gutenberg.org/media/9147/9147-1.mp3", "test-storage");
        assertTrue(path.toString().equals("test-storage/www.gutenberg.org/media/9147"));
    }

    @Test
    public void testShouldParseTheFilename() throws MalformedURLException, UnsupportedEncodingException {
        String filename = FileDownloader.parseFilename("http://wwww.gutenber.org/9147/");
        assertEquals("index.html", filename);
        filename = FileDownloader.parseFilename("http://wwww.gutenber.org/9147/9147-01.mp3");
        assertEquals("9147-01.mp3", filename);
    }
    
    @Test
    public void testShouldJoinLocalStorageWithFilename() {
        String pathFilename = FileDownloader.getAbsoluteFileDownloadPath(Paths.get("www.gutenberg.org"), "9147-01.mp3");
        assertEquals(pathFilename, "www.gutenberg.org/9147-01.mp3");
    }
    
    @Test
    public void testShouldDownloadFile() throws IOException {
        assertTrue(FileDownloader.downloadFile("http://www.gutenberg.org/files/9147/mp3/", "test-storage"));
        assertTrue(new File("test-storage/www.gutenberg.org/files/9147/mp3/index.html").exists());
        deleteDirectory(new File("test-storage/"));
    }
    
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
