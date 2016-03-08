package edu.uw.eduong.musaic;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Song object
 */
public class Song implements Comparable<Song>, Parcelable {
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

    protected Song(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        albumId = in.readLong();
        albumArt = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        path = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeLong(albumId);
        dest.writeValue(albumArt);
        dest.writeString(path);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}