# Installation #

VoltDB runs on servers that meet the following [OS and Software Requirements](http://community.voltdb.com/docs/UsingVoltDB/ChapGetStarted):

- Operating System:
    - 64-bit Linux:
        - CentOS 5.6 or later (binary compatible with RHEL 5.6 or later)
        - Ubuntu 10.4 or later
    - Mac OS X 10.6 (Snow Leopard) or later
- JDK 6_20 or later
- NTP (only for versions before VoltDB 3.0)

Download [Enterprise Edition] (http://voltdb.com/products-services/downloads)

Download [Community Edition] (http://community.voltdb.com/downloads)

The following sections provide a quick summary of the installation process.  Read more detail about the installation process in [*Using VoltDB:* 2.2 - Installing VoltDB](http://community.voltdb.com/docs/UsingVoltDB/installDist).

### Installing to the home directory ###
The simplest and most typical way to install VoltDB is install it under the HOME directory of your user account.  This is easy for development and gives you full access to the software without requiring root permission.  This can be done easily with the following command:

    tar -xzvf voltdb-ent-2.8.4.tar.gz -C $HOME
    
### Installing to a standard directory ###
For production use, VoltDB is often installed into a standard folder for software such as /opt/voltdb.

    sudo tar -xzvf voltdb-ent-2.8.4.tar.gz -C /opt


VoltDB extracts to a folder that contains the version number, but we probably don't want to have to remember this version whenever we're going to use it, or update scripts that automate operations with VoltDB every time we upgrade.

One way to solve this is to rename the folder.

    cd /opt
    nsudo mv voltdb-ent-2.8.4 voltdb
    
Another way that might be even better is to instead add a symbolic link, so we keep the folder with the version number, and we have a link we can update when we install a new version.

    cd /opt
    ln -s voltdb-ent-2.8.4 voltdb

### Installing using the Debian package ###
VoltDB Community Edition is also available as a [Debian package] (http://community.voltdb.com/downloads), so for Debian-based systems such as Ubuntu it can be installed using the dpkg command:

    sudo dpkg -i voltdb_2.8.4_amd64.deb

## Post-installation Setup ##

Except when using the Debian package, VoltDB is entirely installed into a single directory, so it's easy to review what is included.  The contents are organized by the following folders:

<table>
    <tr>
        <td>voltdb/</td>
        <td>VoltDB Software jar files and runtimes</td>
    </tr>
    <tr>
        <td>examples/</td>
        <td>Example applications you can run (voter, voltcache, voltkv)</td>
    </tr>
    <tr>
        <td>bin/</td>
        <td>Shell commands: voltcompiler, voltdb, sqlcmd, exporttofile</td>
    </tr>
    <tr>
        <td>doc/</td>
        <td>Product manuals (PDF), javadoc, and tutorials</td>
    </tr>
</table>


Read [*Using VoltDB* section 2.4 - What is Included in the VoltDB Distribution](http://community.voltdb.com/docs/UsingVoltDB/installComponents) for more details.

To make it easier to use and maintain scripts, you may want to do some of the following:

- Install VoltDB into a directory with a fixed name, such as $HOME/voltdb or /opt/voltdb, so that regardless of the version installed, the directory is the same.
- Set this path to an environment variable, e.g. VOLTDB_HOME.
- Add the $VOLTDB_HOME/bin directory to the PATH environment variable.


---------------------------------
Next: [Hands on with VoltDB Interfaces](at03_interfaces.md)
