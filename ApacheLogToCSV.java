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
    
    private String m_directory;
    private String m_outputFilePath;
    private PrintWriter m_printWriter;
    
    public ApacheLogToCSV(String file_directory, String outputName) {
        m_directory = file_directory;
        m_outputFilePath = m_directory + "/" + outputName;
    }
    
    public void combineLogs(boolean allLogs) throws IOException {
    
        System.out.println("Writing files to:\n" + m_outputFilePath);
        
        FileWriter write = null;
        try {
            write = new FileWriter(m_outputFilePath);
            m_printWriter = new PrintWriter(write);
        } catch (IOException ex) {
        	System.err.println("Problem opening file for output: " + m_outputFilePath);
        }
        
        if (null != m_printWriter)
        {
	        //Creates file and writes headers
	        initializeFile();
	        
	        try {
		        loopOverFile(m_directory, allLogs, "primary");    
	        } catch (IOException ex) {
		        System.err.println("Something bad happened while parsing logs:" + ex.getMessage());
	        } finally {
	        	write.close();
		        m_printWriter.close();
	        }
	        
	        System.out.println("Finished!");
        
        } else {
        	System.out.println("Encountered error. Did nothing.");
        }
    }
    
    public void combineLogs() throws IOException{
        combineLogs(true);
    }
    
    //creates file and writes the first line to the file
    private void initializeFile()  throws IOException{
        System.out.println("writing first line");
        String firstLine =      //First Line of output
                "\"Workername\", \"Remote Host IP\", \"Remote Username\", \"Timestamp\", \"TimeZone\", "
                + "\"port\", \"Content\", \"X-Forwarded-For IP\", \"Last Status\", "
                + "\"Size (bytes)\", \"Elapsed Time\", \"ID\"";
        m_printWriter.println(firstLine);
    }
    
    
    //Looks for httpd and then applies writeFiles to internal Objects
    //If isPrimary and combineLogs, then also loops over subfolders beginning with worker.
    //level = 0 means primary, level = 1 means worker, level = 2 means httpd folder
    private void loopOverFile(String path, Boolean combineLogs, String workername) throws IOException{
        
        File mainDirectory = new File(path);
        String[] fileList = mainDirectory.list();
        
        for(int i=0; i < fileList.length; i++){ 
            if(fileList[i].matches("access.[\\d_]*.log")){
                String fileName = path + "/" + fileList[i];
                System.out.println("adding " + fileName);
                writeFile(fileName, workername);
            } else if(fileList[i].equals("httpd")){
                String folderPath = path + "/" + fileList[i];
                System.out.println("looping over " + folderPath);
                loopOverFile(folderPath, combineLogs, workername);
            } else if (combineLogs && fileList[i].matches("worker\\d*")) {
                String workerPath = path + "/" + fileList[i];
                System.out.println("looping over worker " + workerPath);
                loopOverFile(workerPath, combineLogs, fileList[i]);
            }
        }
    }
    
    
    
    //Takes in fileReader to format and output name and writes a new formatted file
    private void writeFile(String inputFilePath, String workername) throws IOException {
        Boolean verbose = false;
        
        FileReader fr;
        BufferedReader bf;
        try {
	        fr = new FileReader(inputFilePath);
	        bf = new BufferedReader(fr);
	    } catch (IOException ex ){
	    	System.err.println(ex.getMessage());
	    	throw ex;
	    }
	    
	    if(verbose) {
            System.out.println("Reading file " + inputFilePath);
            System.out.println("writing to file " + m_printWriter.toString());
        }
        
	    try {
	        String line;
	        line = bf.readLine();
	        while (line != null) {
	            m_printWriter.printf("%s" + "%n", formatLine(line, workername));
	            line = bf.readLine();
	        }
	    } catch (IOException ex) {
	    	System.err.println("Something bad happened while parsing logs:" + ex.getMessage());
	    } finally {
	        fr.close();
	        bf.close();
	    }
    }
    
    //formats a single line of a log file.
    private String formatLine(String Line, String workername){
        boolean verbose = false;
        
        String pat = "^([^\\s]*)"        //1 Remote Host IP
                + "\\s([^\\s]*)"         //2 Remote Username
                + "\\s[^\\s]*"           // (excluded) Remote logname
                + "\\s\\[([^\\s^:]*)"    //3 Day/month/year
                + ":([^\\s]*)\\s"        //4 hour:minute:second
                + "([^\\s]*)]\\s"        //5 Offset from UST
                + "(\\d*)\\s"            // 6 Port
                + "\"([^\"]*)\"\\s"      //7 Content. Assumes content cannot contain "
                + "\"([^\"]*)\"\\s"      //8 X-Forarded-For IP
                + "(\\d*)\\s"            //9 Last Status
                + "[^\\s]*\\s"           //(excluded) Content Length
                + "\"([\\d-]*)\"\\s"     //11 Size in bytes. 0 displayed as -.
                + "(\\d*)\\s"            //12 time in milliseconds
                + "(.*)$";               //13 ID
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(Line);

        if(matcher.find()){
            String formatString = String.format("%13$2s, %1$2s, %2$2s, %3$2s %4$2s, %5$2s, %6$2s, "
                    + "%7$2s, %8$2s, %9$2s, %10$2s, %11$2s, %12$2s", 
                    matcher.group(1),matcher.group(2),matcher.group(3),
                    matcher.group(4),matcher.group(5),matcher.group(6),
                    matcher.group(7),matcher.group(8),matcher.group(9),
                    matcher.group(10),matcher.group(11),matcher.group(12), workername);
            if(verbose){System.out.println(formatString);};
            return formatString;
        }
        else{
            System.out.println("This line caused an issue:");
            System.out.println(Line);
        }
        
        return null;
    }
}
