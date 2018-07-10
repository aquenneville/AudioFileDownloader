package github.aq.audiofiledownloader.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AudioFileParser {

    public static final String ALLOWED_EXTENSIONS = ".*mp3|.*mp4";
    public static final String HREF_ATTRIBUTE = "href";
    public static final String HTML_ANCHOR = "a";
    
    /**
     * parse the links in the file
     * @param filePath  the file path to the file
     * @return  A list of urls links 
     */
    public static List<String> parseAudioLinks(Path filePath) {
        List<String> resourceList = new ArrayList<>();
        byte[] byteContent;
        
        try {            
            byteContent = Files.readAllBytes(filePath);
            String stringContent = new String(byteContent);
            Document doc = Jsoup.parse(stringContent);
            Elements elmts = doc.select(HTML_ANCHOR);
            
            for (Element elmt: elmts) {	
                if (elmt.attr(HREF_ATTRIBUTE).matches(ALLOWED_EXTENSIONS)) {
                    resourceList.add(elmt.attr(HREF_ATTRIBUTE));
                }
            }         
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourceList;		
    }
}
