package m19.core.rules;

import java.io.Serializable;

import m19.core.user.User;
import m19.core.Work;

public class PriceHigherThan25 implements Serializable, RequestRule{
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
        if(work.getPrice() <= 25)
            return 0;
        return -6;
    }
}
