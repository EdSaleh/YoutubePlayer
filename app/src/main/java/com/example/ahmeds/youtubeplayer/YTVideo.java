package com.example.ahmeds.youtubeplayer;

/**
 * Created by 100488982 on 11/21/2015.
 */
public class YTVideo {


    private int _id;
    private String _title;
    private String _author;
    private String _embed;
    private byte[] _image;

    public YTVideo(String _title, String _author, String _embed, byte[] _image) {
        this._title = _title;
        this._author = _author;
        this._embed = _embed;
        this._image = _image;
    }

    public int get_id() {
        return _id;
    }

    public byte[] get_image() {
        return _image;
    }

    public void set_image(byte[] _image) {
        this._image = _image;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_author() {
        return _author;
    }

    public void set_author(String _author) {
        this._author = _author;
    }

    public String get_embed() {
        return _embed;
    }

    public void set_embed(String _embed) {
        this._embed = _embed;
    }


    public YTVideo(){}

}
