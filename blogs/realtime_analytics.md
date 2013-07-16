# Realtime Analytics in VoltDB #

VoltDB was designed for big velocity database workloads--extremely high volumes of writes and/or reads per second.  As an OLTP-optimized in-memory database, it is often used for use cases that require hundreds of thousands or even millions of operations per second.  

What is less known is that it also is capable of supporting realtime analytics on this same data that is changing at a high velocity.  This has powerful implications for many businesses that operate at web scale and need realtime feedback on the current state of operations for uses like:

- alerting management to problems that require immediate attention
- providing instantaneous and ongoing feedback to managers running active promotions, price changes, or campaigns.
- detecting emerging trends or unusual patterns of activity
- tracking realtime key performance indicators for dashboards

## A hard problem ##
You can't get this information from an OLAP system because it can't process updates fast enough.  But you can't run queries in an OLTP system without adversely impacting operations.  In most real situations where the business depends on uptime and fast response times, this is simply not even an option.  You could build a dedicated OLTP system for reporting, but even then the queries would run slowly, and it wouldn't be able to keep pace with constant updates while running the queries.  At best it could only support high latency low volume reads.

This pushes you towards batch processing, where you give up on the expectation of realtime information.  Another reason you might try batch processing is if you have high query rates.  You can store the most recent results in summary tables in an RDBMS or in NoSQL to achieve higher read rates, but only on stale data.  As the data volumes grow, the batch process takes longer and produces increasingly stale results.

## A new approach ##
VoltDB addresses this problem with a powerful approach to realtime analytics.  It enables you to pre-calculate summary data for instanteous access and high read rates, but rather than using batches, you calculate and update on-the-fly without without adversely impacting write operations.  VoltDB does this either using views which are automatically maintained, or summary tables that are maintained in by stored procedures.  Both are maintained in realtime.

Views in VoltDB are by definition immediately-refreshed materialized views.  In a traditional RDBMS, this is something that often cannot be used for high velocity data.  Tom Kyte, in his influential DBA guide *expert one-on-one Oracle* writes, "materialized views are not designed for OLTP Systems", as they will "naturally inhibit concurrency in a high update situation". Instead, he recommends them for "read-only/read-intensive" environments.  This is very true, not just for Oracle but for all similar databases that use disk i/o and multi-version concurrency control.  These are two of the bottlenecks that were identified in Michael Stonebraker's research paper [The End of an Architectural Era: (It's Time for a Complete Rewrite)](http://hstore.cs.brown.edu/papers/hstore-endofera.pdf) that were eliminated in the development of the H-Store prototype and VoltDB.  In VoltDB the immediate updating of one or even several views does not affect concurrency, and has a very minimal impact on the performance of updates.

Views in VoltDB summarize a table using SQL analytics (count, sum) with a group by clause.  These can be defined in various combinations to simultaneously provide summaries grouped in different ways, in either coarse or fine granularity.  Each view need only be defined in the schema, and it will be maintained automatically.

The second technique for maintaining realtime analytic summaries in VoltDB is to modify the java stored procedures used for writing operational data to update summary tables.  This allows more flexibility in the type of summaries that can be built.  For example, the stored procedure could combine the input data with the results of queries and update a denormalized summary, or use conditions to create more complex summaries.  Stored procedures also have extremely fast direct memory access to previously stored data which can be used to update a summary that incorporates previous activity, such as moving averages or pattern matches.

Realtime analytics isn't a silver bullet to solve slow-running OLAP or BI problems.  It is a different type of problem that OLAP systems aren't suitable to address, but which OLTP systems traditionally weren't able to address without impacting operations.  In industry situations from capital markets to digital advertising, large internet operations, or large network operations, realtime analytics in VoltDB can provide immediate feedback and information about rapidly changing data to increase awareness, visibility, and efficiency.
