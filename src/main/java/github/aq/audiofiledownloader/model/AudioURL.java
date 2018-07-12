package github.aq.audiofiledownloader.model;

import java.net.MalformedURLException;
import java.net.URL;

public class AudioURL {
    
    private String url;
    
    public AudioURL(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        if (!url.endsWith("/")) {
            url = url+"/";
        }
        return url;
    }
    
    public String getHost() {
        String host = null;
        try {
            host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return host;
    }
    
    public String getFilename() {
        String filename = null;
        try {
            filename = new URL(url).getFile();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filename;
    }
    
    public String getPath() {
        if (!url.startsWith("http")) {
            return url.substring(0, url.lastIndexOf("/"));
        } else {
            if (!url.endsWith(".mp3") && !url.endsWith("/")) {
                url = getUrl();
            }
            String host = getHost();
            int hostLastPosition = url.indexOf(host) + host.length();
            return url.substring(hostLastPosition, url.lastIndexOf("/")+1);
        }
    }
    
}
