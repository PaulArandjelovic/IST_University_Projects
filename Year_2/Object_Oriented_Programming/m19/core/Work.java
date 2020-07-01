package m19.core;

import java.io.Serializable;
import java.util.Set;
import java.util.LinkedHashSet;

import m19.core.user.User;
/**
 * Works of library
 */
public class Work implements Serializable{
    private static final long serialVersionUID = 201918111350L;

    /** id of work */
    private int _id;
    /** price of work */
    private int _price;
    /** _available copies of work */
    private int _availableCopies;
    /** number of copies of work */
    private int _numberOfCopies;
    /** title of work */
    private String _title;
    /** category of work */
    private Category _category;
    /** all users to notify once work becomes available */
    private Set<User> _usersToNotify;

    /**
     * @param id
     * @param price
     * @param numberOfCopies
     * @param title
     * @param category
     */
    public Work(int id, int price, int numberOfCopies, String title, Category category){
        _id = id;
        _price = price;
        _availableCopies = numberOfCopies;
        _numberOfCopies = numberOfCopies;
        _title = title;
        _category = category;
        _usersToNotify = new LinkedHashSet<>();
    }

    /**
     * @return id of work
     */
    public int getId(){
        return _id;
    }

    /**
     * @return price of work
     */
    public int getPrice(){
        return _price;
    }

    /**
     * @return number of copies of work
     */
    public int getCopies(){
        return _numberOfCopies;
    }

    /**
     * @return number of available copies of work
     */
    public int getAvailableCopies(){
        return _availableCopies;
    }

    /**
     * @return title of work
     */
    public String getTitle(){
        return _title;
    }

    /**
     * @return category of work
     */
    public Category getCategory(){
        return _category;
    }

    /**
     * @return name of work
     */
    public String getName(){
        return "";
    }

    /**
     * increment available copies of work
     *
     * @return void
     */
    public void addCopy(){
        _availableCopies++;
    }

    /**
     * decrement available copies of work
     *
     * @return void
     */
    public void removeCopy(){
        _availableCopies--;
    }

    /**
     * Given a certain category as an argument, return it
     *
     * @param category
     *
     * @return category of work
     */
    public String convertCategory (String category){
        switch(category){
            case "REFERENCE":
                return "Referência";
            case "FICTION":
                return "Ficção";
            case "SCITECH":
                return "Técnica e Científica";
            default:
                return "";
        }
    }

    /**
     * Return full description of the work
     *
     * @return full description of work
     */
    public String getDescription(){
        String className = this.getName();
        return _id+" - "+_availableCopies+" de "+_numberOfCopies+" - "+className+" - "+_title+" - "+_price+" - "+convertCategory(_category.name());
    }

    /**
     * add user to notify when work available
     *
     * @param user
     *
     * @return void
     */
    public void addUserToNotify(User user){
        _usersToNotify.add(user);
    }

    /**
     * Notify all users that work is available
     *
     * @return void
     */
    public void notifyUsers(){
        Delivery notification = new Delivery(this);
        for (User user: _usersToNotify)
            user.addNotification(notification);

        clearUsersToNotify();
    }

    /**
     * Clear all stored users to notify
     *
     * @return void
     */
    public void clearUsersToNotify(){
        _usersToNotify.clear();
    }

}
