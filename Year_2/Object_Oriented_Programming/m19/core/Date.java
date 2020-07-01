package m19.core;

import java.io.Serializable;

/**
 * Library date control.
 */
public class Date implements Serializable{
    private static final long serialVersionUID = 201918112358L;
    /** current date in library */
    private int _currentDate;

    /** inital date = 0 */
    protected Date(){
        _currentDate = 0;
    }

    /**
     * @return current date
     */
    public int getCurrentDate(){
        return _currentDate;
    }

    /** advance date by x days */
    protected void advanceDay(int nDays){
        _currentDate = _currentDate + nDays;
    }
}
