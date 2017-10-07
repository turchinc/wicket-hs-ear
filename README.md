wicket-ear: Wicket Framework used in a WAR inside an EAR.
=========================================================
Author: Ondrej Zizka <ozizka@redhat.com>, Chris Turchin
Level: Intermediate  
Technologies: Apache Wicket, JPA, Hibernate, Hibernate Search

Summary: Demonstrates how to use the Wicket Framework 7.x with the JBoss server using the Wicket Java EE integration, packaged as an EAR  
Target Product: WildFly  
Source: <https://github.com/wildfly/quickstart/>  

What is it?
-----------

This is an example of how to use Wicket Framework 7.x with WildFly, leveraging features of Java EE 7, using the Wicket Java EE integration.

Features used:

* Injection of `@PersistenceContext`
* Injection of a value from `web.xml` using `@Resource`
* Injection of a stateless session bean using `@EJB`

This is an EAR version, with the following structure:

        -`wicket-ear` - parent module
            - `ejb`: Contains EJB beans and JPA entities. Creates a `.jar` file
            - `war`: Contains the Wicket web application, which uses the EJB beans. Creates a `.war` file
            - `ear`: Packages the EJB JAR and WAR into an EAR. Creates an `.ear` file


System requirements
-------------------

All you need to build this project is Java 8 (Java SDK 1.8) or better, Maven 3.1 or better.

The application this project produces is designed to be run on JBoss WildFly.

 
Configure Maven
---------------

If you have not yet done so, you must [Configure Maven](../README.md#mavenconfiguration) before testing the quickstarts.


Start JBoss WildFly with the Web Profile
-------------------------

1. Open a command line and navigate to the root of the JBoss server directory.
2. The following shows the command line to start the server with the web profile:

        For Linux:   JBOSS_HOME/bin/standalone.sh
        For Windows: JBOSS_HOME\bin\standalone.bat


Build and Deploy the Quickstart
-------------------------

_NOTE: The following build command assumes you have configured your Maven user settings. If you have not, you must include Maven setting arguments on the command line. See [Build and Deploy the Quickstarts](../README.md#buildanddeploy) for complete instructions and additional options._

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. Type this command to build and deploy the archive:

        mvn clean package wildfly:deploy

4. This will deploy `target/wildfly-wicket-ear.ear` to the running instance of the server.


Access the application 
----------------------

The application will be running at the following URL: <http://localhost:8080/wildfly-wicket-ear-war/>.

 * You will see a page with a table listing the existing Contact entities. Initially, this table is empty.
 * Click on the _Insert a new Contact_ link to add a new contact.


Undeploy the Archive
--------------------

1. Make sure you have started the JBoss Server as described above.
2. Open a command line and navigate to the root directory of this quickstart.
3. When you are finished testing, type this command to undeploy the archive:

        mvn wildfly:undeploy


Run the Quickstart in JBoss Developer Studio or Eclipse
-------------------------------------
You can also start the server and deploy the quickstarts from Eclipse using JBoss tools. For more information, see [Use JBoss Developer Studio or Eclipse to Run the Quickstarts](../README.md#useeclipse) 


Debug the Application
---------------------

If you want to debug the source code or look at the Javadocs of any library in the project, run either of the following commands to pull them into your local repository. The IDE should then detect them.

        mvn dependency:sources
        mvn dependency:resolve -Dclassifier=javadoc
