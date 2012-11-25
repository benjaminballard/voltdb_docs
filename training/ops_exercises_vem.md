# Operational Exercises with VoltDB Enterprise Manager

The following exercises use VoltDB Enterprise Manager to perform typical operations on a VoltDB database.  VoltDB Enterprise Manager is only available in VoltDB Enterprise Edition.

## A. Starting and Stopping a cluster ##

Start the database:

Add some data:

    cd ~/voltdb-*
    cd bin
    sqlcmd
    1> INSERT INTO contestants (contestant_number,contestant_name) VALUES (100,'Homer Simpson');
    (1 row(s) affected)
    2> exit
    


VEM: Read [*Management Guide*: Chapter 5. Starting and Stopping the Database](http://community.voltdb.com/docs/MgtGuide/StartStopChap)


## B. Manual snapshot ##


## C. Recovery ##


## D. Live Catalog Update ##
VEM: Read [*Management Guide*: 7.2. Updating the Application Catalog](http://community.voltdb.com/docs/MgtGuide/UpdateCatalog#UpdateAddVersion)
