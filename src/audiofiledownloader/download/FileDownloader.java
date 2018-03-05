package audiofiledownloader.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloader {

	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";
	private static final int BUFFER_SIZE = 4096;
	
	public static int REQ_TIME_WAIT = 2; 
	private static Path downloadPath;
	private static int total_downloads = 0;
	private static int total_failed_downloads = 0;
	
	public static boolean downloadFile(String fileURL, String savePath) {
		try {
			
            total_downloads ++;
			//LocalDate now = LocalDate.now();
			Path localDownloadPath = null;			
			if ("".equals(savePath)) {
				localDownloadPath = Paths.get(new URL(fileURL).getHost());
			} else {
				localDownloadPath = Paths.get(savePath).getParent();
			}
			
			if (Files.notExists(localDownloadPath)) {
				Files.createDirectory(localDownloadPath);
			}
			
			String fileName = "";
			// extracts file name from URL
            fileName = Paths.get(new URL(fileURL).getFile()).getFileName().toString();
            if (fileURL.endsWith("/")) {
            	fileName = "index.html";
            }
            // check content type text/html 
            String saveFilePath = localDownloadPath.toString() + File.separator + fileName;
            downloadPath = Paths.get(saveFilePath);
			
            if (Files.notExists(downloadPath)) {
            
		        URL url = new URL(fileURL);	        
		        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		        httpConn.setRequestMethod("GET");
		        httpConn.setRequestProperty("User-Agent", USER_AGENT);
		        int responseCode = httpConn.getResponseCode();
		 
		        // always check HTTP response code first
		        if (responseCode == HttpURLConnection.HTTP_OK) {
		                      
		            String contentType = httpConn.getContentType();
		            int contentLength = httpConn.getContentLength();
		            
		            if (fileURL.endsWith("/") && contentType.contains("text/html;")) {
		            	fileName = "index.html";
		            }
		 
		            // opens input stream from the HTTP connection
		            InputStream inputStream = httpConn.getInputStream();
		            
		            // opens an output stream to save into file
		            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
		            
		            int bytesRead = -1;
		            byte[] buffer = new byte[BUFFER_SIZE];
		            while ((bytesRead = inputStream.read(buffer)) != -1) {
		                outputStream.write(buffer, 0, bytesRead);
		            }
		 
		            outputStream.close();
		            inputStream.close();
	
		            System.out.println("Download - Content-Type = " + contentType + 	            
		            " Content-Length = " + contentLength + 
		            " Download path = " + saveFilePath);
		            
		            try {
						Thread.sleep(REQ_TIME_WAIT * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        } else {
		            System.out.println("Failure - No file to download for " + fileURL + ". Server replied HTTP code: " + responseCode);
		            total_failed_downloads ++;
		            return false;
		        }
		        httpConn.disconnect();
            } else {
            	System.out.println("File found on disk - " + saveFilePath);
            }
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return true;
	}
	
	public static Path getDownloadPath() {
		return downloadPath;
	}

	public static int getTotal_downloads() {
		return total_downloads;
	}

	public static int getTotal_failed_downloads() {
		return total_failed_downloads;
	}
	
}
