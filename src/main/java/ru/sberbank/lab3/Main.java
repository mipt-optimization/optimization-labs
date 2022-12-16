package ru.sberbank.lab3;

import ru.sberbank.lab3.protobuf.Album;
import ru.sberbank.lab3.protobuf.AlbumDemo;
import ru.sberbank.lab3.protobuf.AlbumProtos;

public class Main {
    public static void main(String[] args) {
        final AlbumDemo instance = new AlbumDemo();
        final Album album = instance.generateAlbum();
        final AlbumProtos.Album albumMessage
                = AlbumProtos.Album.newBuilder()
                .setTitle(album.getTitle())
                .addAllArtist(album.getArtists())
                .setReleaseYear(album.getReleaseYear())
                .addAllSongTitle(album.getSongsTitles())
                .build();
        final byte[] binaryAlbum = albumMessage.toByteArray();
        final Album copiedAlbum = instance.instantiateAlbumFromBinary(binaryAlbum);
        System.out.println("BEFORE Album (" + System.identityHashCode(album) + "): " + album);
        System.out.println("AFTER Album (" + System.identityHashCode(copiedAlbum) + "): " + copiedAlbum);
    }
}
