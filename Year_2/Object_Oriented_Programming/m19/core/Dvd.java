package m19.core;

import java.io.Serializable;

/**
 * Type of work -- Book
 */
public class Dvd extends Work implements Serializable{
    private static final long serialVersionUID = 201918112357L;
    /** director of Dvd */
    private String _director;
    /** igac of Dvd */
    private int _igac;

    /**
     * @param id
     * @param price
     * @param numberOfCopies
     * @param title
     * @param category
     * @param director
     * @param igac
     */
    public Dvd(int id, int price, int numberOfCopies, String title, Category category, String director, int igac){
        super(id,price,numberOfCopies,title,category);
        _director = director;
        _igac = igac;
    }

    /**
     * @return director
     */
    public String getDirector(){
        return _director;
    }

    /**
     * @return igac
     */
    public int getIgac(){
        return _igac;
    }

    /**
     * @return type of Work
     */
     @Override
    public String getName(){
        return "DVD";
    }

    /**
     * @return total description of Dvd
     */
    public String getDescription(){
        return super.getDescription()+" - "+_director+" - "+_igac;
    }
}
