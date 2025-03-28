package webapp;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/BasicServlet")
public class BasicServlet extends HttpServlet {

    private static Gson GSON = new GsonBuilder().create();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        String json = GSON.toJson(Todos.todos.values());
        resp.setHeader("Content-Type", "application/json");
        resp.getOutputStream().println(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(201);
        //resp.getOutputStream().println("Hello, World from Post Method");
        resp.setContentType("application/json");

        //BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));

        String input = new BufferedReader(new InputStreamReader(req.getInputStream())).lines().collect(Collectors.joining("\n"));

        Todo todo = GSON.fromJson(input, Todo.class);
        Todos.todos.put(todo.id, todo);
        //resp.getOutputStream().println(in);

    }
}
