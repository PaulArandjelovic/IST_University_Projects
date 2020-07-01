package m19.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;

import m19.core.exception.BadEntrySpecificationException;
import m19.core.exception.NameLengthException;


public class Parser {

  private Library _library;

  Parser(Library lib) {
    _library = lib;
  }

  void parseFile(String filename) throws IOException, BadEntrySpecificationException, NameLengthException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;

      while ((line = reader.readLine()) != null)
        parseLine(line);
    }
  }

  private void parseLine(String line) throws BadEntrySpecificationException, NameLengthException {
    String[] components = line.split(":");

    switch(components[0]) {
      case "DVD":
        parseDVD(components, line);
        break;

      case "BOOK":
        parseBook(components, line);
        break;
      case "USER":
        try{
          parseUser(components, line);
        } catch (NameLengthException nle){System.out.println("NameLengthException");}
        break;

      default:
        throw new BadEntrySpecificationException("Invalid type " + components[0] +
                                                " in line " + line);
    }
  }

  private void parseDVD(String[] components, String line) throws BadEntrySpecificationException {
    if (components.length != 7)
      throw new BadEntrySpecificationException("Wrong number of fields (6) in " + line);
    Dvd dvd = new Dvd(_library.getNextWorkId(), Integer.parseInt(components[3]), Integer.parseInt(components[6]),
                      components[1], Category.valueOf(components[4]),components[2],
                      Integer.parseInt(components[5]));
    _library.getWorkHash().put(_library.getNextWorkId(),dvd);
    _library.increaseWorkId();
  }

  private void parseBook(String[] components, String line) throws BadEntrySpecificationException {
    if (components.length != 7)
      throw new BadEntrySpecificationException("Wrong number of fields (6) in " + line);
    Book book = new Book(_library.getNextWorkId(), Integer.parseInt(components[3]), Integer.parseInt(components[6]),
                          components[1], Category.valueOf(components[4]),components[2],
                          Integer.parseInt(components[5]));

    _library.getWorkHash().put(_library.getNextWorkId(),book);
    _library.increaseWorkId();
  }

  private void parseUser(String[] components, String line) throws BadEntrySpecificationException,NameLengthException{
    if (components.length != 3)
      throw new BadEntrySpecificationException("Wrong number of fields (2) in " + line);
    _library.registerUser(components[1],components[2]);
  }
 }
