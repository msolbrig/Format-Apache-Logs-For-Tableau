package AccessLogTabify;
import java.io.IOException;

import javax.swing.JFileChooser;

//Example for calling ApacheLogToCSV on specific file.
//This really should call something to have you navigate to the directory and file,
//and then ask you what you would like to call the output,
//but I haven't done that yet.

public class AccessLogTabify{
    
    public static void main(String[] args) throws IOException{
    	
    	JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
    	
    	if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
    		System.out.println("good job choosing a file");
    		System.out.println("The file you chose was: " + fileChooser.getSelectedFile().toString());
    		
    		String masterDirectory = fileChooser.getSelectedFile().toString();
            String outputFileName = "FormattedLogs.txt";

    		ApacheLogToCSV logConverter = new ApacheLogToCSV(masterDirectory,outputFileName);
            
            logConverter.combineLogs();

    	}
    	else{
    		System.err.println("no file was selected");
    	}    
        
    }

}
