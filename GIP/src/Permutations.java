

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Permutations<T> {
    
        
 
    public Collection<List<T>> permute(Collection<T> input) {
        Collection<List<T>> output = new ArrayList<List<T>>();
        if (input.isEmpty()) {
            output.add(new ArrayList<T>());
            return output;
        }
        List<T> list = new ArrayList<T>(input);
        T head = list.get(0);
        List<T> rest = list.subList(1, list.size());
        for (List<T> permutations : permute(rest)) {
            List<List<T>> subLists = new ArrayList<List<T>>();
            for (int i = 0; i <= permutations.size(); i++) {
                List<T> subList = new ArrayList<T>();
                subList.addAll(permutations);
                subList.add(i, head);
                subLists.add(subList);
            }
            output.addAll(subLists);
        }
        return output;
    }
}


//output: 
//P(3,3) : Count (6) :- [[1, 2, 3], [2, 3, 1], [3, 2, 1], [3, 1, 2], [2, 1, 3], [1, 3,  
//2]]
//P(3,2) : Count (6) :- [[3, 1], [2, 1], [3, 2], [1, 3], [2, 3], [1, 2]]
//P(3,1) : Count (3) :- [[3], [1], [2]]
//P(3,0) : Count (1) :- [[]]