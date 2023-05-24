package com.example.tpo_s23878;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "BookSearch", value = "/BookSearch")
public class BookSearch extends HttpServlet {
    private DataSource dataSource;

    public void init() {
        try {
            Context init = new InitialContext();
            Context context = (Context) init.lookup("java:comp/env");
            dataSource = (DataSource) context.lookup("jdbc/books");
        } catch (Exception ignored) {
        }

    }

    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        response.setContentType("text/html; chaset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter responseOut = response.getWriter();
        Connection connection = null;

        try {
            synchronized (dataSource) {
                connection = dataSource.getConnection();
            }
            Statement statement = connection.createStatement();

            String title = request.getParameter("title");
            String author = request.getParameter("author");

            ResultSet resultSet = statement.executeQuery(
                    "select * from books" +
                            " where author='" + author + "' or title='" + title + "';");

            if(resultSet.next()){
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("overview"));
                responseOut.println(book);
            } else responseOut.println("Nie znaleziono żadnego rekordu.");

            responseOut.println("<a href=\"index.jsp\"> Powrót do indeksu</a>");

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        responseOut.close();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response);
    }
}
