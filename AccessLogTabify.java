package AccessLogTabify;
import java.io.IOException;

//Example for calling ApacheLogToCSV on specific file.
//This really should call something to have you navigate to the directory and file,
//and then ask you what you would like to call the output,
//but I haven't done that yet.

public class AccessLogTabify{
    
    public static void main(String[] args) throws IOException{
        String masterDirectory = "C:/Users/msolbrig/workspace/AccessLogTabify/Example Log Files/Worker X/TableauServerlogs";
        String outputFileName = "FormattedLogs.txt";

        
        ApacheLogToCSV thing = new ApacheLogToCSV(masterDirectory,outputFileName);
        
        thing.combineLogs();
    }

}
