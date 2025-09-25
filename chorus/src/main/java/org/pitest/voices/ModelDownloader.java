package org.pitest.voices;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModelDownloader implements ModelFetcher{

    private final URL url;

    public ModelDownloader(URL url) {
        this.url = url;
    }


    @Override
    public Path fetch() throws IOException {
        Path dir = Files.createTempDirectory("voices-model");
        Path archive = dir.resolve("model.tar.bz2");

        URLConnection urlConn = url.openConnection();
        long size = urlConn.getContentLengthLong();
        ProgressBarBuilder pbb = new ProgressBarBuilder();
        pbb.setTaskName("Fetching ");
        pbb.setInitialMax(size);

       ReadableByteChannel readableByteChannel = Channels
                .newChannel(ProgressBar.wrap(urlConn.getInputStream(), pbb));

        try(FileOutputStream fileOutputStream = new FileOutputStream(archive.toFile())) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }

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
