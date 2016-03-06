package edu.uw.eduong.musaic;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Song object
 */
public class Song implements Comparable<Song> {
    private long id;
    private String title;
    private String artist;
    private long albumId;
    Bitmap albumArt;
    private String path;

    public Song(long id, String title, String artist, long albumId, Bitmap albumArt, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
        this.albumArt = albumArt;
        this.path = path;
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

    public Long getAlbumId() { return this.albumId; }

    public Bitmap getAlbumArt() { return this.albumArt; }

    public String getPath() { return this.path; }

    @Override
    public int compareTo(Song another) {

        return this.title.compareTo(another.title);
    }
}
