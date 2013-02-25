# Planned Maintenance Window #

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

    voltadmin pause
    
2) Take a manual snapshot

    voltadmin save /path/to/save/dir snapshot_name

3) Verify snapshot succeeded

    voltadmin show snapshots

4) Shut down the database

    voltadmin shutdown

5) Make changes (update the catalog or deployment files)

6) Restart the database in admin mode (using the create.sh script)

    ./create.sh
    
-or-
    
    VOLTDB_HOME="~/voltdb"
    nohup voltdb create catalog $VOLTDB_HOME/examples/voter/voter.jar deployment $VOLTDB_HOME/examples/voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>&1 &

7) Reload the data from the snapshot

    voltadmin restore /path/to/save/dir snapshot_name

8) Resume the database (allow users to connect)

    voltadmin resume

------------------

[CLI Exercises](ops_exercises_cli.md) | Next: [High Availability](ex_cli_06_high_availability.md)
