package edu.uw.eduong.musaic;

/**
 * Song object
 */
public class Song {
    private long id;
    private String title;
    private String artist;
    private long albumId;
    private String path;

    public Song(long id, String title, String artist, long albumId, String path) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumId = albumId;
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

    public String getPath() {
        return this.path;
    }


}
