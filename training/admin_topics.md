Part 1
------

VoltDB 3.0 has been released.  The release notes and all documentation on the web site have been updated, and you should download the final release version and stop using the earlier copy that I sent you.  Here is a direct link:
http://voltdb.com/downloads/technologies/server/LINUX-voltdb-ent-3.0.tar.gz



[This page](http://voltdb.com/docs/MgtGuide/HostConfigPortOpts.php) in the Management Guide lists all of the network ports used by VoltDB and how to override them.

On JDKs, we support both 6 & 7, and both OpenJDK and the Sun release.  We do a bit more testing on Sun JDK 6, but we do a lot of testing with each, and we find no substantial difference for VoltDB performance or stability.  Your call.

There is a section in the release notes for ["Important Base Platform Considerations"](http://voltdb.com/docs/EnterpriseReleaseNotes/index.php#BasePlatformConsiderations) that 

Part 2
------

New 3.0 features

 - The project.xml file is now deprecated and everything that was configured there can now be declared in DDL:
     - stored procedure declarations and partitioning information
     - table partitioning information
     - export table information
     - security roles
 - Supports more types of online schema changes, including:
     - add/remove tables
     - add/remove indexes (except indexes that introduce a new uniqueness constraint)
   The following schema changes still require a maintenance window:
     - add/remove fields in a table
     - add/remove views
 - New "voltadmin" command line tool (allows direct commands outside of sqlcmd)
     - "voltadmin pause" vs. "exec @Pause;"
     - "voltadmin resume" vs. "exec @Resume;"
     - "voltadmin save ..." vs. "exec @SnapshotSave ..."
     - "voltadmin restore ..." vs. "exec @SnapshotRestore ..."
     - "voltadmin update ..." vs. "exec @UpdateApplicationCatalog ..."
     - "voltadmin shutdown" vs. "exec @Shutdown;"

Scripting considerations:

- Add the voltdb/bin directory to your path for convenience
 - Decide on a folder where you keep operational scripts for VoltDB
 - Decide on a path to the catalog.jar file that should be referenced by the scripts.  The source code for the catalog should ideally be kept under version control, but it's still a good practice to have one place where the current copy of the catalog is maintained so that the catalog isn't inadvertently changed due to an unexpected crash and recovery, or maintenance window.
 - The following scripts simplify and standardize common processes.
    - "create" - start the cluster with no data, used initially and during maintenance. (This script could be run on each server, or it could invoke 'voltdb create ...' command on each server remotely using SSH)
    - "recover" - recover from a crash, loading data from command logs and command log snapshots. (This script could be run on each server, or it could invoke 'voltdb recover ...' command on each server remotely using SSH)
    - "rejoin" - restart a failed node, rejoining the cluster. (run only on the server that needs to be rejoined)
 - Be careful about automating actions that could risk data loss in the event of an error, in particular:
    - Don't automatically take a snapshot and then shutdown without confirming that the snapshot was successful


Monitoring

Nagios Plug-in
 - available on Github [here](https://github.com/VoltDB/voltdb-nagios)

 Contains the following modules:
  
 - check_voltdb_ports
    - Alert if any instance (entire cluster) dies
 - check_voltdb_memory
    - Alert when memory use exceeds warning and critical thresholds
 - check_voltdb_cluster
    - For K-safety clusters
    - Alert if any node goes down
 - check_voltb_replication
    - Alert a warning when replication overflows to disk (falling behind)
    - Alert critical when replication fails

Monitoring by directly calling stored procedures

 - see attached (Monitoring VoltDB Operations) for examples

Managing VoltDB: AppendixA - [A.1 Server Configuration Options](http://voltdb.com/docs/MgtGuide/AppxServerConfig.php)
 - For servers with two network interfaces, assign internalinterface and externalinterface IP addresses.


Calling a stored procedure from SQLCMD or VoltDB Studio.

 - use the syntax "exec <procedure name>;"
 - for procedures that take parameters, they should follow the procedure name and be separated by spaces.  For example:
 
    sqlcmd
    1> exec @SystemInformation OVERVIEW;

 - Another way of using SQLCMD is in a script.  SQLCMD can execute commands passed in from STDIN non-interactively.  Exit SQLCMD and try the following example:

    echo "exec @SystemInformation DEPLOYMENT;" | sqlcmd


Timestamp insert format

 - When inserting a timestamp value from SQLCMD or VoltDB Studio, or using a string input value for comparision with a TIMESTAMP data type, the format should be compatible with the java.sql.Timestamp.valueOf(String s) method, which requires the following format: yyyy-mm-dd hh:mm:ss.fffffffff where the ".fffffffff" are optional.  For example:
 
    sqlcmd
    INSERT INTO mytable VALUES( 1, '2013-01-01 00:00:00');
    SELECT COUNT(*) FROM mytable WHERE time > '2000-01-01 00:00:00';

 
 
