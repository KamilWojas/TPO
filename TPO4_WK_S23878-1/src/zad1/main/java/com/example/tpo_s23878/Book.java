package com.example.tpo_s23878;

public class Book {
    private int id;
    private String title;
    private String author;
    private String overview;

    public Book(int id, String title, String author, String overview){
        this.id = id;
        this.title = title;
        this.author = author;
        this.overview = overview;
    }

    @Override
    public String toString(){
        return author + " - " + title + ": " + overview + "\n";
    }
}
