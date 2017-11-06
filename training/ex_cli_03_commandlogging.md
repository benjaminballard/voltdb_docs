# Command Logging #

Manual snapshots only provide persistence for given points in time.  They do not provide continuous data persistence.  That requires command-logging which is an Enterprise Edition feature.  In this exercise, we will enable command-logging and use it to recover when restarting the database after a simulated failure.

## Enable Command Logging ##

To enable command logging with default settings, you can simply add the "commandlog" tag to the deployment.xml file as shown here:

    <deployment>
        <cluster hostcount="1" sitesperhost="2" kfactor="0" />
        <commandlog enabled="true"/>
    </deployment>

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


## Command Log Snapshots vs. Automated Snapshots ##

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

## Recovery ##

So far, we have only started the database empty, using the "voltdb" command line tool with the "create" action parameter.  Another option is to use the "recover" action parameter which will start the database, load data from the most recent command log snapshot, and then play back the remainder of the command log to restore the database to the very latest transaction.  So we will make a new script for recovering the database after a crash.

recover.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="$HOME/voltdb"
    APP_HOME="$VOLTDB_HOME/examples/voter"
    nohup voltdb recover catalog $APP_HOME/voter.jar deployment $APP_HOME/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>&1 &


Follow these steps to test command log recovery:

1) Make the edits to deployment.xml to enable command logging.  

2) Start the database using the create.sh script.  

3) Run the voter client again to load data.

    cd ~/voltdb/examples/voter
    ./run.sh client

4) In sqlcmd, stop the database:

    voltadmin shutdown

5) Restart the database using the recover.sh script.

6) Open sqlcmd and verify that the data is still there.

    sqlcmd
    1> SELECT COUNT(*) FROM votes;


## Recovery vs. Maintenance ##

It is important to note that command logging provides durability for recovery purposes.  Recovery means restoring the database back to the same configuration and state before it stopped.  VoltDB does not support making configuration changes as part of the recovery process.  If you need to make configuration changes, they should be made using the [Planned Maintenance Window](ex_cli_05_maintenance.md) process, which is a subsequent exercise.  But first, let's look at how we can make some schema and configuration changes to a running database.

---------------------------------

[CLI Exercises](ops_exercises_cli.md) | Next: [Live Catalog Update](ex_cli_04_liveupdate.md)
