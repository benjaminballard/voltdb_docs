# Starting and Stopping a cluster #
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
    PATH="$PATH:$VOLTDB_HOME/bin"
    voltdb start catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost

Start the database again, using the start.sh script.

This time, stop the database administratively:

    cd ~/voltdb/bin
    sqlcmd
    1> exec @Shutdown;

Since we can stop the database without using Ctrl-C from the local console, let's modify the start.sh script to run VoltDB as a background process.

start.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    PATH="$PATH:$VOLTDB_HOME/bin"
    nohup voltdb start catalog ../voter/voter.jar deployment ../voter/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>&1 &


--------------

Next: [Taking manual snapshots](ex_cli_02_snapshots.md)
