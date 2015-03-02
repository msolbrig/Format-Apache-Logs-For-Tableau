package AccessLogTabify;
import java.io.BufferedReader;
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
	private static final String firstLine =      //First Line of output
			"\"Remote Host IP\", \"Remote Username\", \"Timestamp\", \"TimeZone\", "
			+ "\"port\", \"Content\", \"X-Forwarded-For IP\", \"Last Status\", "
			+ "\"Size (bytes)\", \"Elapsed Time\", \"ID\"";
	
	public ApacheLogToCSV(String file_directory) {
		directory = file_directory;
	}
	
	//Takes in file name to format and output name and writes a new formatted file
	public void writeToFile(String inputFileName, String outputFileName, Boolean append) throws IOException{
		
		String inputPath = directory +"/" + inputFileName;
		String outputPath = directory + "/" + outputFileName;
		
		FileReader fr = new FileReader(inputPath);
		BufferedReader bf = new BufferedReader(fr);
		FileWriter write = new FileWriter(outputPath, append);
		PrintWriter print_line=new PrintWriter(write);
		
		if(!append){print_line.printf("%s" + "%n", firstLine);};
		
		String line;
		line = bf.readLine();
		while (line != null) {
			print_line.printf("%s" + "%n", formatLine(line));
			line = bf.readLine();
		}
		
		fr.close();
		bf.close();
		write.close();
		print_line.close();
		
	}
	
	//formats a single line of a log file.
	//Input: line of log file
	//Output: formatted line of log file
	private String formatLine(String Line){
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
			return formatString;
		}
		else{
			System.out.println("This line caused an issue:");
			System.out.println(Line);
			return null;
		}
	}
}

