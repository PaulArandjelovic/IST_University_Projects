package m19.core.rules;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

import m19.core.user.User;
import m19.core.Work;
import m19.core.rules.RequestRule;
import m19.app.exception.RuleFailedException;

public class Evaluator implements Serializable{
    private static final long serialVersionUID = 201918111350L;

    /** rules that will be evaluated */
    private List<RequestRule> _rules;

    public Evaluator(){
        _rules = new ArrayList<>();
        _rules.add(new RequestedTwice());
        _rules.add(new UserIsSuspended());
        _rules.add(new AllCopiesRequested());
        _rules.add(new MaxUserWorksRequested());
        _rules.add(new ReferenceWork());
        _rules.add(new PriceHigherThan25());
    }

    /**
     * Method which tests all rules
     *
     * @param User
     * @param Work
     *
     * @throws RuleFailedException
     *
     * @return void
     */
    public void eval(User user, Work work) throws RuleFailedException{
        for(RequestRule rule : _rules){
            int ruled = rule.eval(user,work);
            if(ruled != 0)
                //multiply ruled by -1 to get absolute value
                //rule errors returned as negative values
                throw new RuleFailedException(user.getId(),work.getId(), ruled * (-1));
        }
    }
}
