package m19.core.user;

import java.util.Comparator;

/** Comparator class which sorts all users by ID */
public class CompareUsers implements Comparator<User>{
    /**
     * UserComparator
     *
     * @param User
     * @param Work
     *
     * @return comparison between both userIDs
     */
    public int compare(User u, User v){
        if (u.getName().compareTo(v.getName())==0)
          return u.getId() - v.getId();
        else
          return u.getName().compareTo(v.getName());
    }
}
