package m19.core;
import java.io.Serializable;

/** Request Notification class */
public class RequestN implements Serializable, Notification{
    private static final long serialVersionUID = 201912171245L;
    /** description of request notification */
    private String _description;

    /**
     * Create new request notification
     *
     * @param work
     */
    public RequestN(Work work){
        _description = "REQUISIÇÃO: " + work.getDescription();
    }

    /**
     * Get notification description in string format
     *
     * @return notification message -> String
     */
    public String getMessage(){
        return _description;
    }
}
