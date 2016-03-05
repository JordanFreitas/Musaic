package edu.uw.eduong.musaic;

/**
 * Song object
 */
public class Song {
    private long id;
    private String title;
    private String artist;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public long getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }



}
