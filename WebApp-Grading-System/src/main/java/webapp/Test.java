package webapp;

import webapp.data.GradingSystemDAO;

import java.util.Map;

public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        GradingSystemDAO dao = new GradingSystemDAO();
        Map<String, String> testMap =  dao.getCourseStatistics("C++ Programming");
        for (Map.Entry<String, String> entry : testMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
}
