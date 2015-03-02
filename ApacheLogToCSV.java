package AccessLogTabify;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


//Class for converting Tableau Server Access logs into a format easy for Tableau Desktop
//  to read.
//  Initialize with directory name.
//  Access logs can be found in the generated logs in logs/httpd
//  The log format definition is in C:\ProgramData\Tableau\Tableau Server\data\tabsvc\config
//  Translation for the log format definition: http://httpd.apache.org/docs/2.2/mod/mod_log_config.html
public class ApacheLogToCSV {
	
	private String directory;
	private String outputFilePath;
	
	public ApacheLogToCSV(String file_directory, String outputName) {
		directory = file_directory;
		outputFilePath = directory + "/" + outputName;
	}
	
	public void combineLogs(boolean allLogs) throws IOException{
		System.out.println("Writing files to:\n" + outputFilePath);
		FileWriter write = new FileWriter(outputFilePath);
		PrintWriter print_line=new PrintWriter(write);
		
		//Creates file and writes headers
		initializeFile(print_line);
		
		loopOverFile(print_line,directory,allLogs);	
		
		write.close();
		print_line.close();
		System.out.println("Finished!");
	}
	
	public void combineLogs() throws IOException{
		combineLogs(true);
	}
	
	//creates file and writes the first line to the file
	private void initializeFile(PrintWriter write)  throws IOException{
		System.out.println("writing first line");
		String firstLine =      //First Line of output
				"\"Remote Host IP\", \"Remote Username\", \"Timestamp\", \"TimeZone\", "
				+ "\"port\", \"Content\", \"X-Forwarded-For IP\", \"Last Status\", "
				+ "\"Size (bytes)\", \"Elapsed Time\", \"ID\"";
		write.println(firstLine);
	}
	
	
	//Looks for httpd and then applies writeFiles to internal Objects
	//If isPrimary and combineLogs, then also loops over subfolders beginning with worker.
	//level = 0 means primary, level = 1 means worker, level = 2 means httpd folder
	private void loopOverFile(PrintWriter write, String path, 
			Boolean combineLogs) throws IOException{
		
		File mainDirectory = new File(path);
		String[] fileList = mainDirectory.list();
		
		for(int i=0; i < fileList.length; i++){
			if(fileList[i].matches("access.[\\d_]*.log")){
				String fileName = path + "/" + fileList[i];
				System.out.println("adding " + fileName);
				writeFile(fileName, write);
			} else if(fileList[i].equals("httpd")){
				String folderPath = path + "/" + fileList[i];
				System.out.println("looping over " + folderPath);
				loopOverFile(write, folderPath, combineLogs);
			} else if (combineLogs && fileList[i].matches("worker\\d*")) {
				String workerPath = path + "/" + fileList[i];
				System.out.println("looping over worker " + workerPath);
				loopOverFile(write, workerPath, combineLogs);
			}
		}
	}
	
	
	
	//Takes in fileReader to format and output name and writes a new formatted file
	private void writeFile(String inputFilePath, PrintWriter print_line) throws IOException{
		Boolean verbose = false;
		
		FileReader fr = new FileReader(inputFilePath);
		BufferedReader bf = new BufferedReader(fr);
		
		if(verbose){
			System.out.println("Reading file " + inputFilePath);
			System.out.println("writing to file " + print_line.toString());
		}
		
		String line;
		line = bf.readLine();
		while (line != null) {
			print_line.printf("%s" + "%n", formatLine(line));
			line = bf.readLine();
		}
		
		fr.close();
		bf.close();
	}
	
	//formats a single line of a log file.
	private String formatLine(String Line){
		Boolean verbose = false;
		
		String pat = "^([^\\s]*)"		//Remote Host IP
				+ "\\s([^\\s]*)"		//Remote Username
				+ "\\s[^\\s]*"			//(excluded) something useless I forget
				+ "\\s\\[([^\\s^:]*)"	//Day/month/year
				+ ":([^\\s]*)\\s"		//hour:minute:second
				+ "([^\\s]*)]\\s"		//Offset from UST
				+ "(\\d*)\\s"			//Port
				+ "\"([^\"]*)\"\\s"		//Content. Assumes content cannot contain "
				+ "\"([^\"]*)\"\\s"		//X-Forarded-For IP
				+ "(\\d*)\\s"			//Last Status
				+ "[^\\s]*\\s"				//(excluded)something useless I forget
				+ "\"([\\d-]*)\"\\s"	//Size in bytes. 0 displayed as -.
				+ "(\\d*)\\s"			//time in milliseconds
				+ "(.*)$";				//ID
		Pattern pattern = Pattern.compile(pat);
		Matcher matcher = pattern.matcher(Line);

		if(matcher.find()){
			String formatString = String.format("%1$2s, %2$2s, %3$2s %4$2s, %5$2s, %6$2s, "
					+ "%7$2s, %8$2s, %9$2s, %10$2s, %11$2s, %12$2s", 
					matcher.group(1),matcher.group(2),matcher.group(3),
					matcher.group(4),matcher.group(5),matcher.group(6),
					matcher.group(7),matcher.group(8),matcher.group(9),
					matcher.group(10),matcher.group(11),matcher.group(12));
			if(verbose){System.out.println(formatString);};
			return formatString;
		}
		else{
			System.out.println("This line caused an issue:");
			System.out.println(Line);
			return null;
		}
	}
}
