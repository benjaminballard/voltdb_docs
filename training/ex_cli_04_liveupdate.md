# Live Catalog Update #

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
    1> exec @UpdateApplicationCatalog ~/voltdb/examples/voter/voter.jar ~/voltdb/examples/voter/deployment.xml;

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

-------------------------

[CLI Exercises](ops_exercises_cli.md) | Next: [Planned Maintenance Window](ex_cli_05_maintenance.md)
