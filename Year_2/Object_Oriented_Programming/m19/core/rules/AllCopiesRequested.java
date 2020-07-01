package m19.core.rules;

import java.io.Serializable;

import m19.core.Work;
import m19.core.user.User;

public class AllCopiesRequested implements Serializable, RequestRule{
    private static final long serialVersionUID = 201918111350L;

    /**
     * Test Rule
     *
     * @param User
     * @param Work
     *
     * @return rule failed
     */
    @Override public int eval(User user, Work work){
        if (work.getAvailableCopies() > 0)
            return 0;
        return -3;
    }
}
