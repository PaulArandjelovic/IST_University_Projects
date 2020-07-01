package m19.core;

import java.io.Serializable;

/**
 * Type of work -- Book
 */
public class Book extends Work implements Serializable{
    private static final long serialVersionUID = 201918112359L;
    /** name of auther of Book */
    private String _author;
    /** isbn value for Book */
    private int _isbn;

    /**
     * @param id
     * @param price
     * @param numberOfCopies
     * @param title
     * @param category
     * @param author
     * @param isbn
     */
    public Book(int id, int price, int numberOfCopies, String title, Category category, String author, int isbn){
        super(id,price,numberOfCopies,title,category);
        _author = author;
        _isbn = isbn;
    }

    /**
     * @return author
     */
    public String getAuthor(){
        return _author;
    }

    /**
     * @return message
     */
    public int getIsbn(){
        return _isbn;
    }

    /**
     * @return type of work
     */
    @Override
    public String getName(){
        return "Livro";
    }

    /**
     * @return total description of Book
     */
    public String getDescription(){
        return super.getDescription()+" - "+_author+" - "+_isbn;
    }
}
