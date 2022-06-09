# Silo client


## Authors

Group T37


### Lead developer 

93690 António Elias [ist193690](https://git.rnl.tecnico.ulisboa.pt/ist193690)

### Contributors

93693 Bernardo Mota [ist193693](https://git.rnl.tecnico.ulisboa.pt/ist193693)

93745 Pavle Arandjelovic [ist193745](https://git.rnl.tecnico.ulisboa.pt/ist193745)


## About

This is a gRPC client that performs integration tests on a running server.
The integration tests verify the responses of the server to a set of requests.


## Instructions for using Maven

You must start the servers first.

To compile and run integration tests:

```
mvn verify
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

