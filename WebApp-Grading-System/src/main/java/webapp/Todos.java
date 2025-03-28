package webapp;

import java.util.HashMap;
import java.util.Map;

public class Todos {
    static Map<Integer, Todo> todos = new HashMap();

    static {
        todos.put(1, new Todo(1, "the first todo"));
        todos.put(2, new Todo(2, "the second todo"));
    }

}
