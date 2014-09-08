package pl.michalkasza.r4stest.model;

/**
 * Klasa reprezentująca dwie wersje adresu.
 */

public class Link {

    private static int id = 0;
    private String original_URL;
    private String shrinked_URL;

    public Link(){}

    public Link(String original_URL, String shrinked_URL) {
        super();
        this.original_URL = original_URL;
        this.shrinked_URL = shrinked_URL;
        this.id++;
    }

    public int getId() {
        return this.id;
    }

    public String getOriginalURL() {
        return original_URL;
    }
    public void setOriginalURL(String original_URL) {
        this.original_URL = original_URL;
    }
    public String getShrinkedURL() {
        return shrinked_URL;
    }
    public void setShrinkedURL(String shrinked_URL) {
        this.shrinked_URL = shrinked_URL;
    }

    @Override
    public String toString() {
        return "original_URL: " + original_URL + " shrinked_URL: " + shrinked_URL+ "]";
    }
}