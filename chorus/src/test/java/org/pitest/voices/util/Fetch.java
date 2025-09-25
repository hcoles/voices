package org.pitest.voices.util;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public class Fetch {
    private final Path baseDir;

    public Fetch(Path baseDir) {
        this.baseDir = baseDir;
    }

    public Path fetch(URL url, String name) throws IOException {
        Path archive = baseDir.resolve(name);

        // don't keep downloading. delete files to refresh
        if (archive.toFile().exists()) {
            return archive;
        }

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
