package ru.sberbank.lab3.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import ru.sberbank.lab3.protobuf.Album;
import ru.sberbank.lab3.protobuf.AlbumDemo;
import ru.sberbank.lab3.protobuf.AlbumProtos;


@State(Scope.Benchmark)
public class BenchmarkState {
    private ObjectMapper objectMapper;
    private Album album;
    final private String albumJson = "{\"title\":\"Songs from the Big Chair\",\"artists\":[\"Tears For Fears\"]," +
            "\"releaseYear\":\"1985\",\"songsTitles\":[\"Shout\",\"The Working Hour\"," +
            "\"Everybody Wants to Rule the World\",\"Mothers Talk\",\"I Believe\",\"Broken\",\"Head Over Heels\"," +
            "\"Listen\"]}";
    private AlbumProtos.Album albumProto;
    private byte[] albumProtoBytes;

    @Setup(Level.Trial)
    public void setUp() {
        objectMapper = new ObjectMapper();
        album = new Album();
        albumProto = buildAlbumProto();
        albumProtoBytes = albumProto.toByteArray();
    }

    private AlbumProtos.Album buildAlbumProto() {
        AlbumDemo instance = new AlbumDemo();
        Album album = instance.generateAlbum();
        return  AlbumProtos.Album.newBuilder()
                .setTitle(album.getTitle())
                .addAllArtist(album.getArtists())
                .setReleaseYear(album.getReleaseYear())
                .addAllSongTitle(album.getSongsTitles())
                .build();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Album getAlbum() {
        return album;
    }

    public String getAlbumJson() {
        return albumJson;
    }

    public byte[] getAlbumProtoBytes() {
        return albumProtoBytes;
    }
}