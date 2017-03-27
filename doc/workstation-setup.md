Workstation Setup
=================

General System Setup
--------------------

### Mac OS X

* Install Homebrew from [Homebrew Homepage Guide](http://brew.sh/)
    
    ```bash
    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    brew update && brew upgrade && brew tap caskroom/cask
    ```

Java/Scala System Setup
------------------------

- Install [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- In the case of Mac OS X

    ```bash
    $ brew cask install java
    ```
- Configure Java environment variables

    ```bash
    $ echo 'export JAVA_HOME=`/usr/libexec/java_home -v 1.8`' >> ~/.bash_profile && echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bash_profile
    $ export JAVA_HOME=`/usr/libexec/java_home -v 1.8` && export PATH=$JAVA_HOME/bin:$PATH
    ```

SBT Setup
---------

- Install [SBT](http://www.scala-sbt.org/)
- In the case of Mac OS X
    ```bash
    brew install sbt
    echo "-mem 2048" >> /usr/local/etc/sbtopts
    echo "-J-XX:MaxMetaspaceSize=1024m" >> /usr/local/etc/sbtopts
    ```