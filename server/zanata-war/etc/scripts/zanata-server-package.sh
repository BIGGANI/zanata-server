#!/bin/bash -e

# Script: zanata-server-package.sh
# Author: camunoz@redhat.com

# This script prepares a Zanata standalone server package
# based on JBoss 5 Community version.
#
# Parameters:
# 1. JBoss 5 zip distribution location.
# 2. Apache maven in the path

# Get the JBoss 5 zip location from parameters
JBOSS_ZIP_LOC=$1
JBOSS_ZIP_PARENT=${JBOSS_ZIP_LOC%/*}

# Zanata War project Dir
ZANATA_WAR_HOME=../..

# Temporary directory to extract and manipulate files
TMP_DIR=${JBOSS_ZIP_PARENT}/jbosstmp

# Ask for parameters
echo -n "Version number to be built? "
read zanata_version

# Extract the JBoss package
echo 'Extracting JBoss package...'
unzip -q $JBOSS_ZIP_LOC -d $TMP_DIR

JBOSS_TMP_DIR=$TMP_DIR/$(ls $TMP_DIR | sort -n | head -1)

# Remove unnecessary files from the package
echo 'Customizing JBoss release...'
rm -rf $JBOSS_TMP_DIR/client
rm -rf $JBOSS_TMP_DIR/docs
rm -rf $JBOSS_TMP_DIR/server/all
rm -rf $JBOSS_TMP_DIR/server/minimal
rm -rf $JBOSS_TMP_DIR/server/standard
rm -rf $JBOSS_TMP_DIR/server/web
rm -rf $JBOSS_TMP_DIR/server/default/deploy/admin-console.war
rm -rf $JBOSS_TMP_DIR/server/default/deploy/jmx-console.war
rm -rf $JBOSS_TMP_DIR/server/default/deploy/ROOT.war

# Rename files
mv $JBOSS_TMP_DIR/server/default $JBOSS_TMP_DIR/server/zanata

# Add Zanata specific files
cp $ZANATA_WAR_HOME/target/zanata-*-internal.war $JBOSS_TMP_DIR/server/zanata/deploy/zanata.war
cp $ZANATA_WAR_HOME/src/etc/zanata-ds.xml $JBOSS_TMP_DIR/server/zanata/deploy

# Get Maven dependencies
mvn dependency:get -DrepoUrl=http://repo1.maven.org -Dartifact=mysql:mysql-connector-java:5.1.18
cp ~/.m2/repository/mysql/mysql-connector-java/5.1.12/mysql-connector-java-5.1.12.jar $JBOSS_TMP_DIR/server/zanata/lib

# Create zanata start scripts

echo "echo \"=========================================================================\"" >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"   _____                 _         \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh  
echo "echo \"  |__  /__ _ _ __   __ _| |_ __ _  \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"    / // _' | '_ \ / _' | __/ _' | \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"   / /| (_| | | | | (_| | || (_| | \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"  /____\__,_|_| |_|\__,_|\__\__,_| \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"                                   \""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"  v$zanata_version Red Hat Inc 2012\""         >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "echo \"=========================================================================\"" >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "./run.sh -c zanata" >> $JBOSS_TMP_DIR/bin/start-zanata.sh
echo "run.bat -c zanata" >> $JBOSS_TMP_DIR/bin/start-zanata.bat

# Rename the JBoss temporary directory
mv $JBOSS_TMP_DIR $TMP_DIR/zanata-server-$zanata_version

# Pack the war again
echo 'Building distributable archive...'
cd $TMP_DIR
zip -rq zanata-server-${zanata_version}.zip zanata-server-$zanata_version
mv zanata-server-${zanata_version}.zip ..

# Remove temp dir
rm -rf $TMP_DIR
