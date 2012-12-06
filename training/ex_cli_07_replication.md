# Database Replication #

## Starting Replication ##

1) Start the master database (on server A)

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    nohup voltdb start catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host serverA > /dev/null 2>$1 &
    

2) Create a replica database (on server B)

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    nohup voltdb replica catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host serverA > /dev/null 2>$1 &


3) Start the DR agent (on server B)

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    dragent master serverA replica serverB

## Stopping Replication ##

To stop replication, just stop the DR agent process (on server B).  This will result in some error messages logged on the master database.  It will queue completed transactions until the queue is ful and then it will abandon replication, delete the queue, ad resume normal operation.

The following process can be used for a more orderly stop to replication, although it involves pausing the master database.

1) Pause the master database (on Server A)

    cd ~/voltdb/bin
    sqlcmd
    1> exec @Pause

2) Shutdown the replica (on Server B)

    cd ~/voltdb/bin
    sqlcmd
    1> exec @Shutdown

3) Resume the master database (on Server A)

    2> exec @Resume

## Promoting the Replica when the Master becomes unavailable ##

1) Stop the DR agent process (on Server B)

2) Invoke the @Promote system procedure on the replica database (on Server B)

    cd ~/voltdb/bin
    sqlcmd
    1> exec @Promote
