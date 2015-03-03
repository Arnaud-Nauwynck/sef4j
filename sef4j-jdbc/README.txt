
JDBC-LOGGER is a SMALL DEVELOPMENT TOOL USED TO MONITOR JDBC CONNECTION ACTIVITIES

The code is FREE, but there is NO GARANTEE OF ANY SORT on the results





1) in the application, change the DataSource configuration to monitor it:

	use 
		URL="jdbc:log:<underlying-jdbc-url>"
		DriverClassName="fr.an.tools.jdbc.proxylogger.DriverLog"

2) in domain/lib, add 
log4j-1.2.8.jar
jdbc-logger.jar

4) in /domain, add
jdbc-logger-config.properties
log4j.xml   (used by log4J)
db-discard.txt  (optional)
db-discard-prefix.txt   (optional)

5) stop/restart your application 
  Log4j is configured to produce a file called "jdbclog.log" in the directory of the application server (in weblogic server)
  
6) exploit results 
  tail -f jdbclog.log
or 
  use  JMX Console + clear stats / dump stats 
 it will generate a CSV file
 use Excel formula for computing ratios.. have fun
or extract stats from log files
  for summing times by type of jdbc requests : 
      java.exe -classpath jdbc-logger.jar fr.an.tools.jdbc.loganalysis.SumTime jdbclog.log
  it will generate a file "jdbclog.csv" for excel (CSV=comma separated values)
  use Excel formula for computing ratios.. have fun
  


