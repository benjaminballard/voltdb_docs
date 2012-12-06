# High Availability #
## Configuration ##
High Availability in VoltDB (also called K-safety) is achieved by having every record of data stored and every transaction processed in two or more places.  This allows the database cluster to continue functioning with no loss of data if one or more servers fail.  Another advantage to this approach is that since the work is done in parallel, the throughput of the database is not affected when a server fails.

Read [*Using VoltDB* - Chapter 11. Availability](http://community.voltdb.com/docs/UsingVoltDB/ChapKSafety) for a detailed description of how this feature works in VoltDB and how to configure it.

High availability is a configurable option that set before starting VoltDB.  If you're starting the database from the command line interface or with scripts, you set this option by editing the deployment.xml file.  For example, the following deployment.xml file has enabled k-safety by setting the kfactor setting to 2.  This allows for any 2 servers in the cluster to fail without loss of availability.


    <?xml version="1.0"?>
    <deployment>
        <cluster hostcount="6" sitesperhost="4" kfactor="2"/>
        ...
    </deployment>

For the purposes of training, often only a minimal number of two servers are available, so the following is an appropriate minimal setting for this exercise:

    <?xml version="1.0"?>
    <deployment>
        <cluster hostcount="2" sitesperhost="2" kfactor="1"/>
        ...
    </deployment>


## Simulate Node Failure and Rejoin ##

To simulate a node failure, connect to one of the servers in the cluster and manually kill the VoltDB java process.  To identify and then kill the process, use the following commands.

    ps -ef | grep voltdb
    
    kill -9 [id]

Connect VoltDB Studio to the surviving node to verify that it is still running.

To rejoin the failed node, we will restart the database process on that server, but with the "LIVE REJOIN" action parameter.

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME\bin"
    SURVIVOR=[hostname of surviving server]
    nohup voltdb live rejoin catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host $SURVIVOR > /dev/null 2>$1 &


---------------------

Next: [Database Replication and failover](ex_cli_07_replication.md)
