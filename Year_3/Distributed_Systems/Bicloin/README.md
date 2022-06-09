# T37-Bicloin

Distributed Systems 2020-2021, 2nd semester project


## Authors

**Group T37**


António Elias [ist193690](mailto:antonio.elias@tecnico.ulisboa.pt)

Bernardo Mota [ist193693](mailto:bernardo.mota@tecnico.ulisboa.pt)

Pavle Arandjelovic [ist193745](mailto:pavle.arandjelovic@tecnico.ulisboa.pt)

### Module leaders

T1 - Pavle Arandjelovic

T2 - António Elias

T3 - Bernardo Mota

### Code identification

In all the source files (including POMs), please replace __CXX__ with your group identifier.  
The group identifier is composed by Campus - A (Alameda) or T (Tagus) - and number - always with two digits.

This change is important for code dependency management, to make sure that your code runs using the correct components and not someone else's.


## Getting Started

The overall system is composed of multiple modules.

See the project statement for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require theservers to be running.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning. 
