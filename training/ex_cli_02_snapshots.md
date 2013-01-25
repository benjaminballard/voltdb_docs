# Taking manual snapshots #
This exercise will show how to take a manual snapshot to persist a backup of the database, and then how to restart the database and restore data from this snapshot.

A snapshot is a point-in-time consistent copy of the entire contents of the database.  It is written to local disk at each node in a cluster to distribute the work of persistence.  A snapshot can be taken at any time, whether the database is online and available to users or in admin mode.

Start the database (the voter demo) manually using the start.sh script.

In order to prove we have persisted data, we first need to add some data, so we can run the voter benchmark.

    cd ~/voltdb/examples/voter
    ./run.sh client
    

As a reference, we can check the total number of votes from the results that were printed by the voter client, or also by running a query.

    2> SELECT COUNT(*) FROM votes;
    
Remember this number, and we'll check later to make sure we restored all of the data.

Now let's prepare a directory for a snapshot.  Open a new terminal window:

    mkdir ~/voltdb_snapshots

Next, we will take a snapshot of the database.  But first, we should take other users into consideration.  In a real world situation, we aren't the only ones who may be interacting with the database.  If we did nothing to prevent other users from modifying data during or after the snapshot, their changes would be lost.  To take a snapshot that we know contains the very latest data, we first need to pause the database to put it into administrative mode, preventing users from making any further changes.  Then, we can take the snapshot.  We're going to put the snapshot in the "snapshots" directory we just created.  This can contain multiple snapshots.  We're going to call this one "snapshot_01".  

    3> exec @Pause;
    4> exec @SnapshotSave /home/username/voltdb_snapshots snapshot_01 1;

The final "0" parameter in the last command means this snapshot will block any new transactions, but this is not a problem because we already paused the database.  To take a snapshot while the database is running for backup purposes, set this value to 0. 

Watch for confirmation that the snapshot was taken successfully.  Then we can stop the database and exit sqlcmd:

    4> exec @Shutdown;
    5> exit
    
Before we restart the database, again we want to consider the users.  We don't want users to interact with an empty version of the database, this could cause application errors, or it could prevent us from reloading the snapshot.  So we want to start the database in administrative mode.  To do this, we need to modify the deployment.xml file that we're using.  In our case, that is the example deployment.xml file provided in the examples/voter directory.  Add the following section to the file:

    <deployment>
      ...
      <admin-mode port="21211" adminstartup="true"/>
    </deployment>

Now we need to restart the database using our create.sh script.

    cd ~/voltdb_scripts
    ./create.sh

Once the database is restarted, open a new sqlcmd console and verify that there is no data:

    cd ~/voltdb/bin
    sqlcmd
    1> SELECT COUNT(*) FROM votes;

It should have zero votes.  Now, use the following command to reload the snapshot that was taken earlier:

    2> exec @SnapshotRestore /home/username/voltdb_shapshots snapshot_01;
    
Once the snapshot is successfully loaded, verify that the data has been restored:

    3> SELECT * FROM contestants;

This time, we should see the same number of votes that existed before we shut down the database.

-----------------------------

[CLI Exercises](ops_exercises_cli.md) | Next: [Command Logging](ex_cli_03_commandlogging.md)
