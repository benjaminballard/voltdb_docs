# Operational Exercises with the Command-line Interface#

The following exercises use the command line interface to perform typical operations on a VoltDB database.  Unless specified otherwise, these commands will work on Community Edition or Enterprise Edition.

The commands can and should be organized into scripts to simplify and standardize the process of database operations.  Example scripts will be used in this exercise, and are also available for download.

Before getting started, create a directory for your scripts.  Since we'll be application files that are provided in the examples/voter folder, it's convenient to create a directory under the examples folder, like this:

    mkdir ~/voltdb/examples/scripts

## Contents ##

- [Starting and stopping a cluster](#starting)
- [Taking manual snapshots](#snapshots)
- [Command Logging](#command)
- [Live Catalog Update](#liveupdate)
- [Planned Maintenance Window](#maintenance)
- [Enabling High Availability](#ha)
- [Node failure and rejoin](#rejoin)
- [Database Replication and failover](#replication)
- [Upgrading VoltDB](#upgrading)
- [Enabling Security](#security)
- [Support](#support)

## <a id="starting"></a>Starting and Stopping a cluster ##
This exercise will show how to start and stop the database manually from the command line and using a simple script.

Start the database (the voter demo) manually.  

    cd ~/voltdb/examples/voter
    PATH=$PATH:~/voltdb/bin
    voltdb start catalog voter.jar deployment deployment.xml \
        license ~/voltdb/voltdb/license.xml host localhost

Notice the VoltDB logo and look for the message "Server completed initialization" which indicates that the database has started.

Use ctrl-C to stop the database.  Now let's build the start.sh script in the scripts directory to make this easier.

start.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    voltdb start catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost

Start the database again, using the start.sh script.

This time, stop the database administratively:

    cd ~/voltdb/bin
    sqlcmd
    1> exec @Shutdown

Since we can stop the database without using Ctrl-C from the local console, let's modify the start.sh script to run VoltDB as a background process.

start.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    nohup voltdb start catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>$1 &


## <a id="snapshots"></a>Manual snapshot ##
This exercise will show how to take a manual snapshot to persist a backup of the database, and then how to restart the database and restore data from this snapshot.

A snapshot is a point-in-time consistent copy of the entire contents of the database.  It is written to local disk at each node in a cluster to distribute the work of persistence.  A snapshot can be taken at any time, whether the database is online and available to users or in admin mode.

Start the database (the voter demo) manually using the start.sh script.

In order to prove we have persisted data, we first need to add some data.  We can do this using sqlcmd:

    cd ~/voltdb/bin
    sqlcmd
    1> INSERT INTO contestants (contestant_number,contestant_name) VALUES (100,'Homer Simpson');
    (1 row(s) affected)

You can verify that the data was inserted by querying the contestants table from the same sqlcmd window:

    2> SELECT * FROM contestants;
    
Now let's prepare a directory for a snapshot.  Open a new terminal window:

    cd ~/voltdb/examples/scripts
    mkdir snapshots

Next, we will take a snapshot of the database.  But first, we should take other users into consideration.  In a real world situation, we aren't the only ones who may be interacting with the database.  If we did nothing to prevent other users from modifying data during or after the snapshot, their changes would be lost.  To take a snapshot that we know contains the very latest data, we first need to pause the database to put it into administrative mode, preventing users from making any further changes.  Then, we can take the snapshot.  We're going to put the snapshot in the "snapshots" directory we just created.  This can contain multiple snapshots.  We're going to call this one "snapshot_01".  

    3> exec @Pause
    4> exec @SnapshotSave ~/voltdb/examples/scripts/snapshots snapshot_01 0

The final "0" parameter in the last command means this snapshot will be taken asynchronously without blocking any new transactions.  We could have used 1 to make it block, but we already paused the database so we know no other transactions are taking place.  When you take a snapshot to a running database for backup purposes, you don't want to block incoming transactions, so we are using the 0 parameter since it is the most typical use.

Watch for confirmation that the snapshot was taken successfully.  Then we can stop the database and exit sqlcmd:

    4> exec @Shutdown
    5> exit
    
Before we restart the database, again we want to consider the users.  We don't want users to interact with an empty version of the database, this could cause application errors, or it could prevent us from reloading the snapshot.  So we want to start the database in administrative mode.  To do this, we need to modify the deployment.xml file that we're using.  In our case, that is the example deployment.xml file provided in the examples/voter directory.  Add the following section to the file:

    <deployment>
      ...
      <admin-mode port="21211" adminstartup="true"/>
    </deployment>

Now we need to restart the database, but we should not use the start.sh script because it uses the "START" action parameter.  We'll explain more about this in the section on taking a maintenance window, but we want to be explicit about starting with an empty database since we're going to restore all of our data from the snapshot, so we want to start VoltDB using the "CREATE" action parameter.  We can put this into a script called start_empty.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    voltdb create catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost

Once the database is restarted, open a new sqlcmd console and verify that there is no data:

    cd ~/voltdb/bin
    sqlcmd
    1> SELECT * FROM contestants;

Now, use the following command to reload the snapshot that was taken earlier:

    2> exec @SnapshotRestore /voltdb/examples/scripts/shapshots snapshot_01
    
Once the snapshot is successfully loaded, verify that the data has been restored:

    3> SELECT * FROM contestants;


## <a id="command"></a>Command Logging ##

Manual snapshots only provide persistence for given points in time.  They do not provide continuous data persistence.  That requires command-logging which is an Enterprise Edition feature.  In this exercise, we will enable command-logging and use it to recover when restarting the database after a simulated failure.

### Enable Command Logging ###

To enable command logging with default settings, you can simply add the "commandlog" tag to the deployment.xml file as shown here:

    <deployment>
        <cluster hostcount="1" sitesperhost="2" kfactor="0" />
        <commandlog enabled="true"/>
    </deployment

By simply enabling the commandlog, it will use the default settings which include asynchronous logging, a logsize of 1GB, and the default voltdbroot directory will be used for both the command log and command log snapshot paths.  The following example is more explicit and shows how to set the optional configurations for command logging.

    <deployment>
        <cluster hostcount="4" sitesperhost="2" kfactor="1" />
        <commandlog enabled="true" logsize="1024" synchronous="false">
            <frequency time="200" transactions="500"/>
        </commandlog>
        <paths>
            <commandlog path="/faskdisk/voltdblog/" />
            <commandlogsnapshot path="/opt/voltdb/cmdsnaps/" />
        </paths>
    </deployment>


### Command Log Snapshots vs. Automated Snapshots ###

When the command log is enabled, VoltDB will automatically take snapshots whenever the command log reaches the specified size.  Once the snapshot has been taken, the prior command log entries will be truncated or overwritten, so that the command log does not grow indefinitely.  These snapshots are stored in the commandlogsnapshot directory.  When the database recovers, it knows how to reload the latest of these snapshots and then play back the command log to recover to the very latest committed transaction.

VoltDB Community Edition does not include command logging, so it has a feature called **Automatic Snapshots**.  This is described in *Using Voltdb* Section 9.2: Scheduling Automatic Snapshots.  Automatic Snapshots provide only periodic durability because even if configured to take snapshots continuously, once a snapshot begins and subsequent transactions will not be persisted until the next snapshot is taken.  Automatic snapshots are configured in the deployment.xml file with the "snapshot" tag, but this training doesn't cover this because it is focused on Enterprise Edition.  In most Enterprise Edition deployments command logging would be used and automatic snapshots would not be enabled.

If you are following these exercises using Community Edition, you can enable automatic snapshots by adding the following entry to deployment.xml:

    <deployment>
        <cluster hostcount="4" sitesperhost="2" kfactor="1" />
        <snapshot prefix="flightsave" frequency="30m" retain="3"/>
        <paths>
            <snapshots path="/etc/voltdb/autobackup/" />
        </paths>
    </deployment>

### Recovery ###

When the database is started with the START action parameter, it will check if command logging was enabled and if there are snapshots and a command log that can be recovered, and then it will recover them as the database is restarted.  If the command log does not exist, then the database will start with a new empty database, which is the same as when you start with the CREATE action parameter.  You can also explicitly tell the database to recover from the command log by using the RECOVER action parameter.

In our example start.sh script, we are using the START action parameter, so no changes are necessary to recover from the command log once it was enabled.

Follow these steps to test command log recovery:

1) Make the edits to deployment.xml to enable command logging.  

2) Start the database using the start.sh script.  

3) Open sqlcmd, load some data and verify:

    cd ~/voltdb/bin
    sqlcmd
    1> INSERT INTO contestants (contestant_number,contestant_name) VALUES (100,'Homer Simpson');
    2> SELECT * FROM contestants;

4) In sqlcmd, stop the database:

    4> exec @Shutdown
    5> exit

5) Restart the database usint the start.sh script.

6) Open sqlcmd and verify that the data is still there.

    cd ~/voltdb/bin
    sqlcmd
    1> INSERT INTO contestants (contestant_number,contestant_name) VALUES (100,'Homer Simpson');
    2> SELECT * FROM contestants;


### Recovery vs. Maintenance ###

It is important to note that command logging provides durability for recovery purposes.  Recovery means restoring the database back to the same configuration and state before it stopped.  VoltDB does not support making configuration changes as part of the recovery process.  If you need to make configuration changes, they should be made using the [Planned Maintenance Window](#maintenance) process.

## <a id="liveupdate"></a>Live Catalog Update ##

VoltDB requires all DDL and stored procedures to be pre-compiled into a catalog file which is loaded into VoltDB during startup.  

Should you wish to change your database schema or stored procedures once the database is running, VoltDB has a system procedure called [@UpdateApplicationCatalog](http://community.voltdb.com/docs/UsingVoltDB/sysprocupdateappcatalog) that can perform a live update to the catalog of a running database.  

Not all schema changes are supported using the mechanism, some changes require a maintenance window.  *Using VoltDB* provides a [reference page](http://community.voltdb.com/docs/UsingVoltDB/sysprocupdateappcatalog) for @UpdateApplicationCatalog that lists the online database modifications that are currently supported.

First, in order to make changes to an online database, we need to start the database using our start.sh script.

    cd ~/voltdb/examples/scripts
    ./start.sh

For this exercise, we will add a new table to the schema.  Edit the ~/voltdb/examples/voter/ddl.sql file.  At the end of the file, add the following statement:

    CREATE TABLE contestant_info (
      contestant_number     integer     NOT NULL,
      contestant_home_state varchar(2)  NOT NULL,
      contestant_hometown   varchar(50) NOT NULL
    );

Next, we will use the provided run.sh script to recompile the catalog file.

    cd ~/voltdb/examples/voter
    ./run.sh catalog

To recompile the catalog manually, you could use the following commands:

    cd ~/voltdb/examples/voter
    ~/voltdb/bin/voltcompiler obj project.xml voter.jar

Now, we can use the @UpdateApplicationCatalog procedure to update the catalog on our running database.

    cd ~/voltdb/bin
    sqlcmd
    SQL Command :: localhost:21212
    1> exec @UpdateApplicationCatalog ~/voltdb/examples/voter/voter.jar ~/voltdb/examples/voter/deployment.xml

If the command is successful, you will see the following output:

    STATUS 
    -------
    0

    (1 row(s) affected)

If you are trying to make an unsupported online change, the @UpdateApplicationCatalog procedure will detect this and the operation will fail with an error message.

    The requested catalog change is not a supported change at this time.  
    May not ... (details of unsupported change)

Once the catalog is updated, you can verify that the table is there.  If the table is missing, you should get an error.

    cd ~/voltdb/bin
    sqlcmd
    1> SELECT * FROM contestant_info;

One thing to be careful about is keeping track of the correct catalog files.  One of the supported online database modifications is to drop a table, which is done by simply omitting the table definition from the DDL in a catalog update.  Tables can be dropped whether they contain data or not.  Normally a change like this is intentional, but if you used the wrong catalog file it could lead to unintended consequences.  For example, if you ran @UpdateApplicationCatalog with the catalog file from a different application, it probably has none of the same tables or procedures as the existing catalog, and could result in the dropping of all the tables and the creation of new, different tables.

## <a id="maintenance"></a>Planned Maintenance Window ##

This section explains the recommended approach when you need to take the entire database down for maintenance.  While VoltDB supports some catalog changes to be made on the fly as described above, all other changes currently require stopping the database, restarting, and loading a snapshot.  Generally this is needed because the change involves significant redistribution or restructuring of data.  Restarting with an empty database makes it possible to make any change to the configuration, and reloading data from a snapshot allows the database to restructure or redistribute the data into the new configuration as it loads the data from disk into memory.

The engineers at VoltDB are hard at work to eliminate this process, and to support more flexibility in schema and configuration changes that can be made on the fly.  So this section may become obsolete, but until then it is an important process for adminstrators to understand and practice.

One more important note about maintenance is if high availability is enabled, some maintenance can also be performed without stopping the entire cluster.  Individual nodes can be stopped and later rejoined to the cluster in order to add RAM to the server, perform upgrades to the Operating System, Java SDK, or other software, and similar changes.

The process for a planned maintenance window is essentially the same as we practiced in the [Taking manual snapshots](#snapshots) section.  There, we included a few important "extra" steps that took into account users who may be connected to the database.  This time, the command examples will be in a format that makes it easier to combine into scripts.

Prerequisite: you should already have the following line in the deployment.xml file so that any time you start the database it starts in Admin mode.

    <deployment>
      ...
      <admin-mode port="21211" adminstartup="true"/>
    </deployment>


1) Pause the database (disconnect users)

    echo "exec @Pause" | sqlcmd --servers=localhost --port=21211
    
2) Take a manual snapshot

    echo "exec @SnapshotSave /path/to/save/dir snapshot_name 1" | sqlcmd --servers=localhost --port=21211

3) Shut down the database

    echo "exec @Shutdown" | sqlcmd --servers=localhost --port=21211

4) Make changes (update the catalog or deployment files)

5) Restart the database in admin mode (using the start_empty.sh script)

    ./start_empty.sh
    
-or-
    
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    nohup voltdb create catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>$1 &

6) Reload the data from the snapshot

    echo "exec @SnapshotRestore  /path/to/save/dir snapshot_name" | sqlcmd --servers=localhost --port=21211    

7) Resume the database (allow users to connect)

    echo "exec @Resume" | sqlcmd --servers=localhost --port=21211


## <a id="ha"></a>High Availability ##
### Configuration ###
High Availability in VoltDB (also called K-safety) is achieved by having every record of data stored and every transaction processed in two or more places.  This allows the database cluster to continue functioning with no loss of data if one or more servers fail.  Another advantage to this approach is that since the work is done in parallel, the throughput of the database is not affected when a server fails.

Read [*Using VoltDB* - Chapter 11. Availability](http://community.voltdb.com/docs/UsingVoltDB/ChapKSafety) for a detailed description of how this feature works in VoltDB and how to configure it.

High availability is a configurable option that set before starting VoltDB.  If you're starting the database from the command line interface or with scripts, you set this option by editing the deployment.xml file.  For example, the following deployment.xml file has enabled k-safety by setting the kfactor setting to 2.  This allows for any 2 servers in the cluster to fail without loss of availability.


    <?xml version="1.0"?>
    <deployment>
        <cluster hostcount="6" sitesperhost="4" kfactor="2"/>
    </deployment>


### <a id="rejoin"></a>Node Failure and Rejoin ###

TODO

## <a id="replication"></a>Database Replication ##

TODO

# <a id="upgrading"></a>Upgrading VoltDB #
See [Upgrade Notes](http://community.voltdb.com/docs/EnterpriseReleaseNotes/index#UpgradeNotes) from Enterprise Release Notes.

# <a id="security"></a>Security #
Read [*Using VoltDB* Chapter 8](http://community.voltdb.com/docs/UsingVoltDB/ChapSecurity)

TODO

# <a id="support"></a>Support Overview #

TODO
