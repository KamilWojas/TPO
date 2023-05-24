package com.example.tpo_s23878;

import java.awt.print.Book;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;

@WebServlet(name = "Books", value = "/Books")
public class Books extends HttpServlet {
    private String message;
    private DataSource dataSource;

    public void init() {
        try{
            Context init = new InitialContext();
            Context context = (Context) init.lookup("java:comp/env");
            dataSource = (DataSource) context.lookup("jdbc/books");
        } catch (Exception ignored){}
    }

    public void handleRequest(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        response.setContentType("text/html; chaset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter responseOut = response.getWriter();

        ArrayList<Book> books = new ArrayList<>();
        Connection connection = null;

        try{
            synchronized (dataSource){
                connection = dataSource.getConnection();
            }
            Statement statement = connection.createStatement();

            String query = "select * from books;";
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                books.add(new Book(
                ));
            }

            responseOut.println("<h1> Lista wszystkich książek </h1><br>");
            responseOut.println("<ol>");
            for(Book book : books){
                responseOut.println("<li>");
                responseOut.println(book);
                responseOut.println("</li>");
            }
            responseOut.println("</ol>");
            responseOut.println("<a href=\"index.jsp> Powrót do indeksu</a>");

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