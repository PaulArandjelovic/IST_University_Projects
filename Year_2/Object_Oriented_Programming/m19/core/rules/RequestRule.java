package m19.core.rules;

import m19.core.user.User;
import m19.core.Work;

/** Interface for rule evaluation */
public interface RequestRule{
    public int eval(User u, Work w);
}
