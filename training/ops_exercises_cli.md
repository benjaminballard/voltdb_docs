# Operational Exercises with the Command-line Interface#

The following exercises use the command line interface to perform typical operations on a VoltDB database.  Unless specified otherwise, these commands will work on Community Edition or Enterprise Edition.

The commands can and should be organized into scripts to simplify and standardize the process of database operations.  The exercises will include some basic example scripts, but ultimately it is up to you how you want to write scripts to automate operations.

## Initial Setup ##
Before getting started with these exercises, you must install VoltDB, which was covered in an [earlier section](at02_installation.md).  You can install VoltDB into any folder you wish.  The examples will assume it is installed at ~/voltdb, but the script examples are easy to modify for any other location.

It is convenient to add the bin directory to your PATH so that VoltDB binaries can be executed without a full path.  You can do this many ways, such as adding the following line to your .profile or .bashrc file:

     PATH="$PATH:~/voltdb/bin"

We should create a folder that we will work in when writing and running the example scripts.

    mkdir ~/voltdb_scripts
    
Optionally, if you want to practice these exercises with your own application, you should have a folder for that.  The examples will use the voter example which is provided with VoltDB.

    mkdir ~/my_voltdb_app
    
## Contents ##

- [Starting and stopping a cluster](ex_cli_01_starting.md)
- [Taking manual snapshots](ex_cli_02_snapshots.md)
- [Command Logging](ex_cli_03_commandlogging.md)
- [Live Catalog Update](ex_cli_04_liveupdate.md)
- [Planned Maintenance Window](ex_cli_05_maintenance.md)
- [High Availability](ex_cli_06_high_availability.md)
- [Database Replication and failover](ex_cli_07_replication.md)
- [Upgrading VoltDB](ex_cli_08_upgrade.md)








