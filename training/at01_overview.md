# Overview #

### What is VoltDB? ###
VoltDB is an ACID-compliant SQL RDBMS that is specialized for High Velocity scaling in BigData environments.  It runs on 64-bit Linux and is optimized for today's hardware.  Some of the most important attributes and features are:

- Fast & Scalable
    - In-memory operation
    - Streamlined transaction processing / concurrency management
    - Automatic partitioning / sharding
    - Scale-out on commodity servers
- Reliable
    - Built-in fault tolerance & high availability
    - Built-in durability to disk
    - Database replication

### How does VoltDB Work? ###
For an introduction to the architecture of VoltDB and how it processes transactions at such a high velocity, read [*Using VoltDB*: section 1.3](http://community.voltdb.com/docs/UsingVoltDB/IntroHowVoltDBWorks).  Some important concepts are:

- All data is stored in main memory, so the database cluster must have sufficient memory to store the entire database.
- Persistence to disk is for recovery only.
- Tables are partitioned on a key column, and distributed across "execution engines", essentially across the CPU cores in the cluster.
- Client applications use a library to connect to the database over TCP and call stored procedures.
- Stored procedures are routed to the correct execution engine and executed in the order they were received.
- The database can be configured so that execution engines on separate servers do the same work and store identical data, for synchronous replication.  This enables high availability by allowing the database cluster to survive the failure of one or more servers.

Then, read [*Getting Started*: Chapter 1](http://community.voltdb.com/docs/GettingStarted/IntroChap#HowVoltDBWorks) for an overview of how the schema and stored procedures that make up a VoltDB application are put together.  Some of the important concepts are:

- A VoltDB database cluster runs a single instance or schema
- The schema:
    - contains DDL (table, view and index definitions) in one or more .sql text files.
    - contains stored procedures defined in DDL or as simple java classes.
    - is pre-compiled into a .jar  "catalog" file.
    - does not contain data.

### VoltDB Editions ###

VoltDB Enterprise Edition shares virtually all of the same code as VoltDB Community Edition, but adds additional features.

<table>
  <tr>
      <th>Feature</th>
      <th>Community Edition</th>
      <th>Enterprise Edition</th>
  </tr>
  <tr>
      <td>High Performance</td>
      <td>X</td>
      <td>X</td>
  </tr>
  <tr>
      <td>Multi-node clusters</td>
      <td>X</td>
      <td>X</td>
  </tr>
  <tr>
      <td>High Availability</td>
      <td>X</td>
      <td>X</td>
  </tr>
  <tr>
      <td>Fast Export to CSV</td>
      <td>X</td>
      <td>X</td>
  </tr>
  <tr>
      <td>Command Log</td>
      <td></td>
      <td>X</td>
  </tr>
  <tr>
      <td>VoltDB Enterprise Manager</td>
      <td></td>
      <td>X</td>
  </tr>
  <tr>
      <td>VoltDB Database Replication</td>
      <td></td>
      <td>X</td>
  </tr>
  <tr>
      <td>Live Rejoin</td>
      <td></td>
      <td>X</td>
  </tr>
  <tr>
      <td>Fast Export to Hadoop</td>
      <td></td>
      <td>X</td>
  </tr>
</table>


**VoltDB Enterprise Manager** provides a full graphical web-based interface for administering VoltDB clusters.  It makes it easy to configure and start a cluster, and eliminates the need for manual configuration of xml files, scripting, and pre-installation of VoltDB to all machines in a cluster.

Both editions of VoltDB can be administered from the command line interface.  There are no actions or tasks that can be performed in VoltDB Enterprise Manager that cannot be done alternatively from the command line.  

You can choose the interface that is most comfortable for you to use, or that offers the level of control, ease of use, or customization that you most prefer.  However, VoltDB Enterprise Manager is not currently able to connect to or control a cluster that was started from the command line, so whichever interface you choose should be used consistently.  The exercises in this tutorial are for command line interface.  Read the [Management Guide](http://voltdb.com/docs/MgtGuide/) for instructions on using VoltDB Enterprise Manager.

-----------------------------------------------
Next: [Installation](at02_installation.md)
