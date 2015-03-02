package AccessLogTabify;
import java.io.IOException;
import java.io.File;

//Example for calling ApacheLogToCSV on specific file.
//This really should call something to have you navigate to the directory and file,
//and then ask you what you would like to call the output,
//but I haven't done that yet.

public class AccessLogTabify{
	
	public static void main(String[] args) throws IOException{
		String directory = "C:/Users/msolbrig/Downloads/logs (5)/httpd";
		//String inputFileName = "access.2015_02_09_00_00_00.log";
		String outputFileName = "FormattedLogs.txt";
		File f = new File(directory);
		String[] filelist = f.list();
		Boolean first = true;
		for(int i = 0; i < filelist.length; i++){
			if(filelist[i].matches("access.[\\d_]*.log")){
				System.out.println("adding: " + filelist[i]);
				try {
					ApacheLogToCSV logTaber = new ApacheLogToCSV(directory);
					logTaber.writeToFile(filelist[i], outputFileName, !first);
				}
				catch (IOException e) {
					System.out.println(e.getMessage());
				}
				first = false;
			}
			else{
				System.out.println("skipping: " + filelist[i]);
			}
		};
	}
