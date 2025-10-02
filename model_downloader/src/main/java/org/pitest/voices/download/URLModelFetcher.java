package org.pitest.voices.download;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class URLModelFetcher implements ModelFetcher {
    private final URL url;


    public URLModelFetcher(URL url) {
        this.url = url;
    }

    public static URLModelFetcher fromString(String url) {
        try {
            return new URLModelFetcher(new URL(url));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path fetch() throws IOException {
        Path dir = Files.createTempDirectory("voices-model");
        Path archive = dir.resolve("model.onnx");

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

        return archive;
    }
}
