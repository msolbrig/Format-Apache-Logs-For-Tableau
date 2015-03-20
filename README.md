# Format-Apache-Logs-For-Tableau
A Java Class for reformatting Tableau Server Apache/httpd/access logs for easy analysis in Tableau Desktop

Interface is not great yet, I'll fix that. Current steps to use:

1.	Generate a set of Tableau Server logs
2.	Run the code
3.	A file browser will appear. Navigate to the main directory of generated log folder and select OK
3.	Hopefully a new file named “FormattedLogs.txt” will appear in the specified directory.
4.	Open FormattedLogs.txt in Tableau and explor

Next steps:

1.	Figure out how to make this an executable type thing
2.	Create some better example workbooks demonstrating why someone would want to use this.
3.	Use the Extract API to export this directly as a .tde instead of a .txt/.csv file.

Any feedback on the code is welcome.
