package downloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Download {
	
	private String link;
	private String USER_AGENT;
	private boolean redirect;
	private DownloadStatus status;
	private boolean forceStop = false;
	
	public Download(String link){
		this(link, "", true);
	}
	
	public Download(String link, String userAgent){
		this(link, userAgent, true);
	}
	
	public Download(String link, String userAgent, boolean allowRedirects){
		this.link = link;
		this.USER_AGENT = userAgent;
		this.redirect = allowRedirects;
		status = new DownloadStatus();
	}
	
	
	public void start() throws IOException
	{
		URL url = new URL(link);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setInstanceFollowRedirects(redirect);
		con.setRequestMethod("GET");
		con.setAllowUserInteraction(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.connect();
		
		int responseCode = con.getResponseCode();
		status.setResponseCode(responseCode);
		
		String responseMessage = con.getResponseMessage();
		status.setReponseMessage(responseMessage);
		
		Map<String, List<String>> headerFields = con.getHeaderFields();
		status.setHeaderFields(headerFields);
		int contentLength = 0;
		String fileName = "";
		
		if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
			System.out.println("Redirected!");
		}else if(responseCode == HttpURLConnection.HTTP_OK){
			contentLength = con.getContentLength();
			status.setFileSize(contentLength);
			
			String disposition = con.getHeaderField("Content-Disposition");
			String contentType = con.getContentType();
			status.setType(contentType);
			if(disposition != null){
				int index = disposition.indexOf("filename=");
				if(index > 0){
					fileName = disposition.substring(index + 10, link.length() - 1);
				}
			}else{
				fileName = link.substring(link.lastIndexOf("/") + 1, link.length());
			}
			
			status.setFileName(fileName);
			
			File saveFile = new File(fileName + "." + contentType.split("/")[1]);
			BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(saveFile));
			byte[] buffer = new byte[32 * 1024 * 1024];
			int bytesRead = 0;
			int in = 0;
			
			long startTime = System.currentTimeMillis();
			
			while((bytesRead = con.getInputStream().read(buffer)) != -1){
				in += bytesRead;
				fout.write(buffer, 0, bytesRead);
				int perc = (int) (Double.valueOf(in) / Double.valueOf(contentLength) * 100);
				status.setPercentageDone(perc);
				
				long elapsedTime = System.currentTimeMillis() - startTime;
				long allTimeForDownloading = (elapsedTime * contentLength / in);
				long remainingTime = allTimeForDownloading - elapsedTime;
				
				status.setRemainingTime(remainingTime);
				System.out.println(perc + "%" + " : " + status.getRemainingTimeHHMMSS());
				
				if(forceStop){
					break;
				}
			}
			
			if(forceStop){
				cleanUpUndownloadedFile(saveFile);
			}
			
			fout.flush();
			fout.close();
			con.getInputStream().close();
		}
		
		con.disconnect();
	}
	
	private void cleanUpUndownloadedFile(File saveFile) {
	}

	public void stop()
	{
		forceStop = true;
	}
	
	public DownloadStatus getStatus()
	{
		return status;
	}

}
