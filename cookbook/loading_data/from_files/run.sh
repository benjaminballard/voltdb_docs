#!/usr/bin/env bash

. ./env.sh

# remove build artifacts
function clean() {
    rm -rf obj log debugoutput voltdbroot example.jar
}

# compile the source code for procedures and the client
function compile() {
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj \
        *.java
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
    
    voltdb compile --classpath obj -o example.jar ddl.sql
}

function startdb() {
    nohup voltdb create catalog example.jar \
        license $VOLTDB_HOME/voltdb/license.xml host localhost > /dev/null 2>&1 &
}

function stopdb() {
    voltadmin shutdown
}

# run the client that drives the example
function client() {
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	DelimitedFileLoader \
        --filename=data/towns.txt \
        --procedure=TOWNS.insert
}

function help() {
    echo "Usage: ./run.sh {clean|client|help|srccompile}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi
