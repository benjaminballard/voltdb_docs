# An Introduction to the VoltDB interfaces #
## Command line with the aid of scripts ##
### Start the database ###
Open a terminal window and run the following commands:

    cd ~/voltdb-2.8.4
    cd examples/voter

Compile the catalog

    ./run.sh catalog
    
Start the database
    
    ./run.sh server

You're should see VoltDB starting up in that window.  The last output line should read "Server completed initialization."  The database is running, but no data has been loaded.  

### Run the Client to load data ###
This database application comes with a high performance benchmark client application that loads data and records statistics of how fast the data is loaded.  It also includes a web interface that shows real-time analytics results.

To view the web interface, launch a web browser and open the file "voltdb-*/examples/voter/LiveStats.html".

To run the client, open a new terminal window and run the following commands:

    cd ~/voltdb-*
    cd examples/voter
    ./run.sh client

You should see the output of the client as it connects to the VoltDB database running on the localhost, and runs the simulation that loads data.  By default it is set to run for 60 seconds.  While the client is running, you should see the real-time results updating on the web page.

Once the client finishes, leave the first terminal window that is running the database open.  We'll be connecting to this database with the SQLCMD and VoltDB Studio tools.

## SQLCMD ##
SQLCMD is VoltDB's command-line SQL interface tool.  
Open a new terminal window and type the following commands:

    cd ~/voltdb-*
    cd bin
    sqlcmd
    
You should get a SQL prompt. Try running a query:

    select * from contestants;
    exec Results;

You should get the results formatted as text.

Aside from SQL, you can also call stored procedures and System procedures using the "exec" command.  Try the following commands:

    exec @SystemInformation OVERVIEW;
    exec @SystemInformation DEPLOYMENT;
    
System procedures are built-in procedures that provide information about the configuration and operating conditions of a VoltDB cluster.  [Appendix G](http://community.voltdb.com/docs/UsingVoltDB/AppSysProc) of Using VoltDB provides a reference for all of the available System procedures.

You can exit the SQLCMD interface using the exit command:

    exit

Another way of using SQLCMD is in a script.  SQLCMD can execute commands passed in from STDIN non-interactively.  Exit SQLCMD and try the following example:

    exit
    echo "exec Results;" | sqlcmd


## VoltDB Studio ##
Open a web browser and type in the following URL:

[http://localhost:8080/studio](http://localhost:8080/studio)
    
This should bring up the VoltDB Studio web interface and connect it to the database running on the localhost.

On the left side of the screen, you can browse the database schema.

On the right side, you should see a query window labeled "SQLQuery1.sql".  Type the following query in the window, and then click on the red exclamation point button on the toolbar to execute the query.

    select * from contestants;

    
You should see the results of the query.  You can also open new SQL query windows using the "New Query" toolbar button.  Try that, and this time run the following stored procedure call:

    exec Results;
    
Another tool in VoltDB Studio is the Performance Monitor.  Click on the "Open Performance Monitor" toolbar button.  You should see a new tab on the page with running charts that show Latency and Transactions/s.  These are real-time statistics you can monitor to see the current activity in the database.  Thereare pull-down selectors you can use to change each graph to show Memory in GB, or Partition Starvation (a visualization of the distribution of work within the database).

Below the charts on the Performance Monitor is a summary statistics table that shows the number of calls to each of the stored procedures that has been invoked in the database, and the min, avg and max execution times in milliseconds.  

Re-run the client (./run.sh client) in the terminal window and observe real-time measurements in the Performance Monitor.

## Manually starting the database ##
When we started the "voter" database earlier, we took some shortcuts by using the provided run.sh script.  This handled all of the command-line parameters and settings, and used an existing set of configurations.  We're going to stop and restart the database, but this time using manual commands.

Go to the terminal window where you ran the "./run.sh server" command, where VoltDB is still running.  Type Ctrl-C to stop the database process, then type the following command to start the database manually:

    VOLTDB_HOME=`cd ~/voltdb-* && pwd`
    PATH=$PATH:$VOLTDB_HOME/bin
    voltdb start catalog voter.jar deployment deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost

The voltdb command uses the following structure:

    voltdb [create|recover|start] catalog [catalog file] deployment [deployment file] \
        license [license file] host [hostname of leader]

- action
    - create: Start the database with empty tables
    - recover: Start the database and recover (load) the data that was persisted when the dataabase was last running.
    - start: If data was persisted, perform the "recover" action, otherwise perform the "create" action.
- catalog: The pre-compiled .jar file that contains the schema and stored procedures for the database.
- deployment: A configuration file that contains settings that enable or disable various database features and configure parameters for them such as paths and ports to be used.
- license: a valid license .xml file
- host: the hostname of one server in the cluster that will act as the leader (only for the purpose of startup).
    
For more about starting VoltDB from the command line, read [*Using VoltDB* Chapter 6 - Running Your VoltDB Application](http://community.voltdb.com/docs/UsingVoltDB/ChapAppRun).

## Configuration Changes in Deployment.xml file ##

Most of the configuration settings for VoltDB are in the deployment.xml file.  Some of the more common settings are:

- How many servers are in the cluster
- How many execution engines should run on each server
- What level of replication should be used (if any) for high availability
- enable and configure the command log or automatic snapshots for durability
- enable the HTTP server that supports VoltDB Studio and the JSON interface.

Let's try a simple configuration change to reduce the number of execution engines that run on each server to just one.  In the voltdb-*/examples/voter directory, open the deployment.xml file with a text editor.  Change the "sitesperhost" setting to 1:

    <?xml version="1.0"?>
    <deployment>
        <cluster hostcount="1" sitesperhost="1" kfactor="0" />
    ...
    </deployment>

Now restart the database and run the client again.  With only one execution engine running, you will notice a difference in the throughput.

Additional settings will be covered below as we go through some of these features.  This was a very simple change that you can make even on a single-server deployment.  The reference for all of the available configuration settings in the deployment.xml file is [*Using VoltDB* Appendix F](http://community.voltdb.com/docs/UsingVoltDB/AppxConfigFile).

## VoltDB Enterprise Manager ##
VoltDB Enterprise Manager is a web-based management interface for VoltDB administration.  Administrators can use it to configure VoltDB servers or clusters, deploy these to available servers remotely, monitor their performance, and perform a number of maintenance operations.  All of these actions can be performed directly through command-line interfaces, but Enterprise Manager makes them easy and convenient.

To start VoltDB Enterprise Manager for the first time, first stop the Voter database instance that was running in a terminal window.  Then enter the following commands.  If you don't have a $VOLTDB_HOME/management folder, then you installed VoltDB Community Edition.  You will need to download a 30-day trial of Enterprise Edition and install it in order to use VoltDB Enterprise Manager.

    cd $VOLTDB_HOME/management
    ./enterprise_manager.sh -b
    
    VoltDB enterprise manager started in background...
    
The "-b" parameter causes the Enterprise Manager to run in the background.  It can be stopped using the following command:

    ./stop_enterprise_manager.sh
    
For now, leave it running.  Open a browser and go to [http://localhost:9000](http://localhost:9000).  You will be prompted for a user name and passwrod.  Enter the following defaults:

    Username: admin
    Password: voltdb
    
You should then see the following:

![Figure 1: Create New Database form](https://raw.github.com/benjaminballard/voltdb_docs/master/training/img/create_database_filled.png "Enterprise Manager screenshot")

We're going to create the Voter database that we previously started from the command line.  We'll start with a very simple configuration.  

- Enter the information from Figure 2 into the form.  
- Click the "Choose File" button and navigate to $VOLTDB_HOME/examples/voter and upload the voter.jar file.  
- Click Create.

![Figure 2: completed Create New Database form ](https://raw.github.com/benjaminballard/voltdb_docs/master/training/img/create_database_filled.png "Enterprise Manager screenshot")

You should see the Voter database, and that it is currently Offline.  There will be a warning that the number of hosts must be > 0.  This warning is shown because you have only defined the database, but not how it will be deployed.  You need to add a server.

In the "Servers" section, click the *Add* button, then click "Add new server" from the listing.  In the form that pops up, under "IP or Host name *" enter "localhost".  Then click "Create".  You should now see localhost listed in the "Servers" section, and the warning is no longer shown.

Now you can start the database by clicking the "Start Database" button.  This brings up the "Start Database" form.

- Select Action: "Create new database"
- Select Mode: "Start in normal mode"
- Click "Start"

While the database is starting, you can observe the commands that are being executed under the Logs section.  The status of the database will change from Offline to Online once the startup is complete.

Now run the client again from the command line, and observe the monitoring of operating statistics and the table statistics in the Data section.

If the database doesn't start due to an SSH error, there needs to be some additional setup on the machine.  The server or computer where VoltDB Enterprise Manageer will be running needs to allow SSH connection to the other servers that will be used in VoltDB clusters without prompting for password.  The standard "id_rsa" key should be generated as a passwordless key.  If it isn't, another passwordless key can be generated and used, but it must be uploaded when adding each server in VoltDB Enterprise Manager.  More detail about ssh configuration for VoltDB Enterprise Manager is [here](http://community.voltdb.com/docs/MgtGuide/SetUpPrepNodes).


--------------------------------------------------------------------
Next: [Ops Exercises - Command Line Interface](ops_exercises_cli.md)
