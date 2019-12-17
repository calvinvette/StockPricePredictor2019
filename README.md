You can create a file called `classpath` that contains the jar-file dependencies in the Maven repo by using:

    ./buildCLASSPATH.sh

Once that file is built, you can create the appropriate CLASSPATH environment variable by using:

    source ./setCLASSPATH.sh

You can build the project with:

    mvn compile
    
You can package it into an executable JAR file with:

    mvn package
    
You can then execute it with:

    java -jar target/StockPriceProducer-1.0-SNAPSHOT.jar
    
To run the final version, you'll need to add the Tab-separated Value file for the data on the command line:

    java -jar target/StockPriceProducer-1.0-SNAPSHOT.jar mydata.tsv
