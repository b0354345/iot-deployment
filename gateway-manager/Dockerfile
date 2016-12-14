FROM alisu/java
MAINTAINER s.mohamed@ncl.ac.uk
RUN mkdir /usr/local/deployer
ADD ./pom.xml /usr/local/deployer
ADD ./src/ /usr/local/deployer/src
WORKDIR /usr/local/deployer
RUN  mvn clean package
CMD ["java", "-jar", "/usr/local/deployer/target/KuraMqttRestClient-1.0-SNAPSHOT.jar", "-b 10.4.192.41", "-a conf-v1", "-m put", "-rconfigurations/uk.ac.ncl.cs.cdt.kura.CloudServiceKura", "-p publish.rate:1 temperature.minimum:-3, temperature.maximum:20"]
