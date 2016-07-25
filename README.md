URL DOWNLOADER
===================

This project aims to prototype a simple url downloader script, with current support of, but not limit to, <i>http, ftp, sftp</i> protocols.

----------

Dev Environment
-------------

This is a sbt project and was developed in IntelliJ. In order to import to Eclipse, run the following command: 
```
cd UrlDownloader
sbt eclipse
```
The above command requires `sbteclipse` plugin, which can be found [here](https://github.com/typesafehub/sbteclipse).

----------
Execution
-------------
There is a jar package `downloader.jar` prepared for executing this project. To start downloading process, simply execute `run.sh`. Inside `run.sh`, you can specify as many urls as needed.
```
#!/bin/bash
java -classpath . -Dlogback.configurationFile="./logger.xml" -jar downloader.jar "http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip" "ftp://speedtest.tele2.net/1MB.zip" "sftp://demo:password@test.rebex.net/readme.txt"> "logs/console.log"
```

> **Note:**

> - There are configs for tuning performance of `HttpDownloader` as well as configs for storing credentials for `FtpDownloader` and `SftpDownloader`
> - All downloaded files remain in the root directory of this project.
> - This project was designed with fault tolerance. A failed download will be retried 3 times. 
> - At current `demo.DownloadExecutor` package, the executor does not aim to utilize computer capability to the maximum. It is just a demo of how to use the downloaders. Better and more complex service implementation can easily wraps necessary `CompletableFuture` in more capable `CompletionService`, and provide more instances for each downloader.

