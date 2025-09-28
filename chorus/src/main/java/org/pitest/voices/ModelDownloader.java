package org.pitest.voices;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModelDownloader implements ModelFetcher {

    private final URLModelFetcher url;

    public ModelDownloader(URL url) {
        this.url = new URLModelFetcher(url);
    }


    @Override
    public Path fetch() throws IOException {
        var archive = url.fetch();
        var dir = archive.getParent();

        Path tar = decompress(dir, archive);
        untar(dir, tar);

        return dir;
    }

    private Path decompress(Path dir, Path archive) throws IOException {
        Path tar = dir.resolve("model.tar");
        try (InputStream in = Files.newInputStream(archive)) {
            BufferedInputStream bis = new BufferedInputStream(in);
            CompressorInputStream cin = new CompressorStreamFactory().createCompressorInputStream(bis);
            Files.write(tar, cin.readAllBytes());
            return tar;
        } catch (CompressorException e) {
            throw new RuntimeException(e);
        }
    }

    public void untar(Path destination, Path tarPath) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(tarPath))) {
             TarArchiveInputStream tar = new TarArchiveInputStream(inputStream);
            ArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                Path extractTo = destination.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(extractTo);
                } else {
                    Files.copy(tar, extractTo);
                }
            }
        }
    }

}
