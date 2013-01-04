# Starting and Stopping a cluster #
This exercise will show how to start and stop the database manually from the command line and using a simple script.

## Compiling a Catalog file ##
Unlike most relational databases, you do not start VoltDB without a schema.  This is because VoltDB uses a pre-compiled schema which is in the form of a .jar file called a catalog, and it runs only one catalog (or application) per instance.  To learn more about building a schema into a catalog, we recommend reading and doing the exercises in [Welcome to VoltDB, A Tutorial](http://voltdb.com/docs/tutorial/).

In these exercises we will be using the voter example application.  This is provided with VoltDB, but the catalog needs to be compiled.  To do that, we can use the following commands, which leverage the run.sh script provided with the voter example.

    cd ~/voltdb/examples/voter
    ./run.sh catalog
    
To compile the catalog manually, you would use the following commands to first compile the stored procedure java classes, and then compile the schema DDL with the classes to create the catalog.

    cd ~/voltdb/examples/voter
    mkdir -p obj
    javac -classpath $HOME/voltdb/voltdb/voltdb-*.jar -d obj src/voter/procedures/*.java
    voltdb compile --classpath obj -o voter.jar ddl.sql
    
You should see "Successfully created voter.jar" followed by information about the stored procedures and their SQL statements.

## Starting the database ##
Start the database (the voter demo) manually.  

    cd ~/voltdb/examples/voter
    voltdb create catalog voter.jar deployment deployment.xml \
        license ../../voltdb/license.xml host localhost

You should see the VoltDB ascii art logo followed by version and other information, and ending with the message "Server completed initialization" which indicates that the database has started successfully.

Use ctrl-C to stop the database.  Now let's build a script to make this easier.

## Making a script ##
create.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="~/voltdb"
    APP_HOME="$VOLTDB_HOME/examples/voter"
    voltdb create catalog $APP_HOME/voter.jar deployment $APP_HOME/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost

Start the database again, using the create.sh script to test it.  You should see the same results.

Use ctrl-C to stop the dataabase.  Now let's modify the create.sh script so that it runs VoltDB as a background process.

create.sh:

    #!/usr/bin/env bash
    VOLTDB_HOME="$HOME/voltdb"
    APP_HOME="$VOLTDB_HOME/examples/voter"
    nohup voltdb create catalog $APP_HOME/voter.jar deployment $APP_HOME/deployment.xml \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>&1 &

## Stopping the Database ##

Up until now we have been running the database on the console and stopping it by using Ctrl-C to kill the current process.  But running VoltDB in the console is normally something you do only during development or testing.  The last change we made to the create.sh script used nohup to run the database process in the background.  So there is a system procedure called @Shutdown that we can call through any of the interfaces to stop the database.

    sqlcmd
    1> exec @Shutdown;

You should see the message "Connection to database host (localhost/127.0.0.1:21212) was lost before a response was received".  This is normal, because the request was to Shutdown, and the database shut itself down before sending a response, so the connection was severed before a response was sent back to sqlcmd.  You can then exit sqlcmd.

    exit


--------------

[CLI Exercises](ops_exercises_cli.md) | Next: [Taking manual snapshots](ex_cli_02_snapshots.md)
