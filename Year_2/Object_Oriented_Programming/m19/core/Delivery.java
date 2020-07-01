package m19.core;
import java.io.Serializable;

/** DeliveryNotification when a work is returned */
public class Delivery implements Serializable, Notification{
    private static final long serialVersionUID = 201912171245L;
    /** Description of returned work */
    private String _description;

    /**
     * create a delivery notification
     *
     * @param work
     */
    public Delivery(Work work){
        _description = "ENTREGA: " + work.getDescription();
    }

    /**
     * @return Delivery notification -> String
     */
    public String getMessage(){
        return _description;
    }
}
