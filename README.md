# Format-Apache-Logs-For-Tableau
A Java Class for reformatting Tableau Server Apache/httpd/access logs for easy analysis in Tableau Desktop

Interface is not great yet, I'll fix that. Current steps to use:

1.	Generate a set of Tableau Server logs.
2.	Find the file path of the log files. Copy this into line 13 of AccessLogTabify.java
3.	Run the file. Hopefully a new file named “FormattedLogs.txt” will appear in the specified directory.
4.	Open attached workbook.
5.	Right-Click on datasource and select Edit Data Source
6.	Click on the file name under Directory in the far-left pane
7.	Navigate to the FormattedLogs.txt file
8.	Open logs and explore.

Next steps:

1.	There are usually multiple log files in each folder, one for each day or so. Combine all log files in a folder into a single file.
2.	If there are multiple workers, have it combine the logs from all workers into a single file with the ip address or worker name added as a column in the file
3.	Create an interface so that a user is prompted to navigate to the logs folder, check whether they would like to format a single file, all files for a worker, or all files for all workers to make this user-friendly
4.	Create some better example workbooks demonstrating why someone would want to use this.
5.	Use the Extract API to export this directly as a .tde instead of a .txt/.csv file.

Any feedback on the code is welcome.
