package ru.sberbank.lab3.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;

public class AlbumDemo {
    public Album generateAlbum()
    {
        return new Album.Builder("Songs from the Big Chair", 1985)
                .artist("Tears For Fears")
                .songTitle("Shout")
                .songTitle("The Working Hour")
                .songTitle("Everybody Wants to Rule the World")
                .songTitle("Mothers Talk")
                .songTitle("I Believe")
                .songTitle("Broken")
                .songTitle("Head Over Heels")
                .songTitle("Listen")
                .build();
    }

    /**
     * Generates an instance of Album based on the provided
     * bytes array.
     *
     * @param binaryAlbum Bytes array that should represent an
     *    AlbumProtos.Album based on Google Protocol Buffers
     *    binary format.
     * @return Instance of Album based on the provided binary form
     *    of an Album; may be {@code null} if an error is encountered
     *    while trying to process the provided binary data.
     */
    public Album instantiateAlbumFromBinary(final byte[] binaryAlbum)
    {
        Album album = null;
        try
        {
            final AlbumProtos.Album copiedAlbumProtos = AlbumProtos.Album.parseFrom(binaryAlbum);
            final List<String> copiedArtists = copiedAlbumProtos.getArtistList();
            final List<String> copiedSongsTitles = copiedAlbumProtos.getSongTitleList();
            album = new Album.Builder(
                    copiedAlbumProtos.getTitle(), copiedAlbumProtos.getReleaseYear())
                    .artists(copiedArtists)
                    .songsTitles(copiedSongsTitles)
                    .build();
        }
        catch (InvalidProtocolBufferException ipbe)
        {
            System.out.println("ERROR: Unable to instantiate AlbumProtos.Album instance from provided binary data - "
                    + ipbe);
        }
        return album;
    }
}
