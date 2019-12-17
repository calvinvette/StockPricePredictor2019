# Dockerfile

# Build up the image from a base OS
# FROM  centos/centos8:8.1.1
FROM debian/stable

# Some schmuck
MAINTAINER  Calvin Vette <calvin.vette@nextgeneducation.com>

# Add to the APT list of sources
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list

# Update the local apt repo registry
RUN apt-get -y update

# Manages APT repo lists - delete this if we're using CentOS or any other non-Debian Linux
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-software-properties software-properties-common

# Set env vars for the build
ENV JAVA_VER 8
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Install Oracle JDK 8 then clean up the mess it leaves
# I'd really recommend OpenJDK 8 or above unless you're using Flight Recorder
RUN echo 'deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
    echo 'deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main' >> /etc/apt/sources.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys C2518248EEA14886 && \
    apt-get update && \
    echo oracle-java${JAVA_VER}-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections && \
    apt-get install -y --force-yes --no-install-recommends oracle-java${JAVA_VER}-installer oracle-java${JAVA_VER}-set-default && \
    apt-get clean && \
    rm -rf /var/cache/oracle-jdk${JAVA_VER}-installer

# Set JDK8 to be the default Java (replacing the built-in OpenJDK 7 that no one should be using)
RUN update-java-alternatives -s java-8-oracle

# Set the env variable for future execution
RUN echo "export JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> ~/.bashrc

# More clean up
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Copy our fat jar onto the container image
COPY target/StockPricePredictor-1.0-SNAPSHOT.jar.jar /opt/StockPricePredictor.jar

### End the build of the image


# Run my Fat Jar - executed at Container instantiation time
CMD ["java -jar /opt/StockPricePredictor/StockPricePredictor.jar"]