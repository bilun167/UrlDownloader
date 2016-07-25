#!/bin/bash
java -classpath . -Dlogback.configurationFile="./logger.xml" -jar downloader.jar "http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip" "ftp://speedtest.tele2.net/1MB.zip" "sftp://demo:password@test.rebex.net/readme.txt"> "logs/console.log"
