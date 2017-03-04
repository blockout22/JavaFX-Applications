package downloader;

import java.util.List;
import java.util.Map;

public class DownloadStatus {
	
	private String responseMessage = "";
	private int responseCode = -1;
	private int fileSize = -1;
	private Map<String, List<String>> headerFields;
	private String fileName = "";
	private String type;
	private int percentageDone = -1;
	private long remainingTime = -1;
	
	protected void setReponseMessage(String message){
		this.responseMessage = message;
	}
	
	protected void setResponseCode(int code){
		this.responseCode = code;
	}
	
	protected void setFileSize(int fileSize){
		this.fileSize = fileSize;
	}
	
	protected void setHeaderFields(Map<String, List<String>> fields)
	{
		this.headerFields = fields;
	}
	
	protected void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	protected void setType(String type){
		this.type = type;
	}
	
	protected void setPercentageDone(int perc){
		this.percentageDone = perc;
	}
	
	protected void setRemainingTime(long remainingTime){
		this.remainingTime = remainingTime;
	}
	
	
	
	public String getResponseMessage()
	{
		return responseMessage;
	}
	
	public int getReponseCode()
	{
		return responseCode;
	}
	
	public int getFileSize()
	{
		return fileSize;
	}
	
	public Map<String, List<String>> getHeaderFields()
	{
		return headerFields;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getType()
	{
		return type;
	}
	
	public int getPercentageDone()
	{
		return percentageDone;
	}
	
	public long getRemainingTime()
	{
		return remainingTime;
	}
	
	public String getRemainingTimeHHMMSS()
	{
		long second = (remainingTime / 1000) % 60;
		long minute = (remainingTime / (1000 * 60)) % 60;
		long hour = (remainingTime / (1000 * 60 * 60)) % 24;
		
		if(minute > 0){
			
		}
		
		if(hour > 0){
			
		}
		
		return hour + ":" + minute + ":" + second;
	}
	
	public int getFileSizeKB()
	{
		return fileSize / 1024;
	}
	
	public int getFileSizeMB(){
		return fileSize / 1024 / 1024;
	}
	
}
