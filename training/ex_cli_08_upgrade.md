# Upgrading VoltDB #

Always refer to the release notes when upgrading VoltDB.  Unless otherwise specified, the process for upgrading VoltDB is similar to a planned maintenance window, except that the change made while the database is stopped is to replace the VoltDB files with a new version.

Refer to the [Planned Maintenance Window](ex_cli_05_maintenance.md) process for the specific commands to use, since they are exactly the same.  The overall process for upgrades is:

1. Pause the database
2. Take a manual snapshot
3. Shut down the database
4. Install the new version of VoltDB

For example:

    mv ~/voltdb ~/voltdb_old
    tar -xzvf voltdb-ent-[newversion].tar.gz -C $HOME
    mv ~/voltdb-ent-[newversion] ~/voltdb

5. Restart the database in admin mode
6. Restore the data from the snapshot
7. Resume the database

----------------------------------------

Next: [Enabling Security](ex_cli_09_security.md)
