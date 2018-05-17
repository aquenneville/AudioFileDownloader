package audiofiledownloader.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloader {
    
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
    private static final int BUFFER_SIZE = 4096;
    private static final String HTML_INDEX = "index.html";
    
    private static Path fileDownloadPath;
    public static int requestTimeWait = 2;
    
    /**
     * Simple class for collecting the failures
     */
    public static class DownloadReport {
            
        public static int totalDownloads = 0;
        public static int totalFailedDownloads = 0;
        public static int totalDownloadsWithZeroContentSize = 0;

        public static String downloadReport() {
            return "Downloads number: " + totalDownloads + ", downloads with empty content: "+ totalDownloadsWithZeroContentSize + ", downloads failed: " + totalFailedDownloads;
        }
    }
    
    /**
     *  Was the target save file path provided ?
     *  @return Path target path for the download
     */
    public static Path getTargetSaveFilePath(String fileURL, String saveFilePathTarget) throws MalformedURLException {
        if ("".equals(saveFilePathTarget)) {
            return Paths.get(new URL(fileURL).getHost());
        } else {
            return Paths.get(saveFilePathTarget).getParent();
        }
    }
    
    /**
     * Does the download path exist on the disk ?
     */
    public static void createDirectoryIfNotExists(Path downloadPath) throws IOException {
        if (Files.notExists(downloadPath)) {
            Files.createDirectory(downloadPath);
        }
    }
    
    /**
     * Parse the filename of the URL
     */
    public static String parseURLFilename(String fileURL) throws MalformedURLException, UnsupportedEncodingException {
        String fileName = Paths.get(new URL(fileURL).getFile()).getFileName().toString();
        fileName = java.net.URLDecoder.decode(fileName, StandardCharsets.UTF_8.displayName());
        if (fileURL.endsWith(File.separator)) {
            fileName = HTML_INDEX;
        }
        return fileName;
    }
    
    /**
     * construct the absolute file download path
     * @param localDownloadPath   the local download path
     * @param fileName   the filename
     * @return  the absolute file download path
     */
    public static String getAbsoluteFileDownloadPath(Path localDownloadPath, String fileName) {
        return localDownloadPath.toString() + File.separator + fileName;
    }
    
    /**
     * download the file to disk in 
     * @param fileURL   the url 
     * @param targetSaveFilePath 
     * @return true if file was downloaded
     */
    public static boolean downloadFile(String fileURL, String targetSaveFilePath) {
        int contentLength = 0;
        
        try {
            Path localDownloadPath = null;  
            DownloadReport.totalDownloads ++;
            
            // Was the target save file path provided ?
            localDownloadPath = getTargetSaveFilePath(fileURL, targetSaveFilePath);

            // Does the download path exist on the disk ?
            createDirectoryIfNotExists(localDownloadPath);

            // Parse the filename of the URL
            String fileName = parseURLFilename(fileURL);
        
            fileDownloadPath = Paths.get(getAbsoluteFileDownloadPath(localDownloadPath, fileName));
            
            // Does file already exist ?
            if (Files.notExists(fileDownloadPath)) {

                // start download
                URL url = new URL(fileURL);	        
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestMethod("GET"); 
                httpConn.setRequestProperty("User-Agent", USER_AGENT);
                int responseCode = httpConn.getResponseCode();

                // always check HTTP response code first
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    String contentType = httpConn.getContentType();
                    contentLength = httpConn.getContentLength();

                    // is file URL a folder ?
                    if (fileURL.endsWith(File.separator) && contentType.contains("text/html;")) {
                        fileName = HTML_INDEX;
                    }

                    // opens input stream from the HTTP connection
                    InputStream inputStream = httpConn.getInputStream();

                    // opens an output stream to save into file
                    FileOutputStream outputStream = new FileOutputStream(getAbsoluteFileDownloadPath(localDownloadPath, fileName));

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.close();
                    inputStream.close();

                    System.out.println("Download: {content-type = " + contentType + ", content-Length = " + contentLength + ", download path = " + getAbsoluteFileDownloadPath(localDownloadPath, fileName) + "}");

                    if (contentLength == 0) {
                        DownloadReport.totalDownloadsWithZeroContentSize ++;
                    }

                    try {
                        Thread.sleep(requestTimeWait * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                } else {
                    System.out.println("Failure - No file to download for " + fileURL + ". Server replied HTTP code: " + responseCode);
                    DownloadReport.totalFailedDownloads ++;
                    return false;
                }
                httpConn.disconnect();
                
            } else {
                System.out.println("File found on disk - " + getAbsoluteFileDownloadPath(localDownloadPath, fileName));
            }
            
        } catch (IOException exc) {
            exc.printStackTrace();
        }
     
        return contentLength > 0;
    }

    public static Path getDownloadPath() {
        return fileDownloadPath;
    }

}
