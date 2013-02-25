# Database Replication #

## Starting Replication ##

1) Start the master database (on server A)

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    nohup voltdb start catalog $VOLTDB_HOME/example/voter/voter.jar deployment $VOLTDB_HOME/example/voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host serverA > /dev/null 2>&1 &
    

2) Create a replica database (on server B)

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    nohup voltdb replica catalog $VOLTDB_HOME/example/voter/voter.jar deployment $VOLTDB_HOME/example/voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host serverA > /dev/null 2>&1 &


3) Start the DR agent (on server B)

    dragent master serverA replica serverB

## Stopping Replication ##

To stop replication, just stop the DR agent process (on server B).  This will result in some error messages logged on the master database.  It will queue completed transactions until the queue is full and then it will abandon replication, delete the queue, ad resume normal operation.

The following process can be used for a more orderly stop to replication.

1) Pause the master database (on Server A)

    voltadmin --host=serverA pause

2) Shutdown the replica (on Server B)

    voltadmin --host=serverB shutdown

3) Resume the master database (on Server A)

    voltadmin --host=serverA resume

## Promoting the Replica when the Master becomes unavailable ##

In the event of a loss of availability of the Master cluster, you can promote the replica cluster and make it the new master using the following command.

    voltadmin --host=serverB promote

The DR agent process won't be able to continue, so it can be stopped.


--------------

[CLI Exercises](ops_exercises_cli.md) | Next: [Upgrading VoltDB](ex_cli_08_upgrade.md)
