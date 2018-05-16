package audiofiledownloader.parse;

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

    public static List<String> parse(Path pagePath) {
        List<String> resourceList = new ArrayList<>();
        byte[] byteContent;
        try {
            byteContent = Files.readAllBytes(pagePath);
            String stringContent = new String(byteContent);
            Document doc = Jsoup.parse(stringContent);
            Elements elmts = doc.select("a");
            for (Element elmt: elmts) {	
                if (elmt.attr("href").matches(".*mp3|.*mp4")) {
                    resourceList.add(elmt.attr("href"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resourceList;		
    }

}
