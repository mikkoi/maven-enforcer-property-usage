package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Handle Files.
 */
public class Files {

    private final Log log;

    public Files(@Nonnull final Log logger) {
        log = logger;
    }

    static String absoluteCwdAndFile(@Nonnull final String filename) {
        return Paths.get(System.getProperty("user.dir"), filename).toAbsolutePath().normalize().toString();
    }

    @Nonnull
    Collection<String> getAbsoluteFilenames(@Nonnull final Collection<String> files) throws IOException {
        Collection<String> allFilenames = new HashSet<>();
        for (String fileSpec : files) {
            File file = new File(fileSpec);
            if (file.exists()) {
                if (file.isDirectory()) {
                    log.debug("File spec '" + fileSpec + "' is a directory.");
                    Collection<String> filenames = getFilenamesFromDir(file.toPath());
                    allFilenames.addAll(filenames); // If item already in Set, discarded automatically.
                } else if (file.isFile()) {
                    log.debug("File spec '" + fileSpec + "' is a file.");
                    allFilenames.add(file.getAbsolutePath()); // If item already in Set, discarded automatically.
                } else {
                    log.error("File '" + fileSpec + "' is not a file or directory. Do not know what to do!");
                }
            } else {
                log.error("File '" + fileSpec + "' does not exist!");
            }
        }
        return allFilenames;
    }

    @Nonnull
    private Collection<String> getFilenamesFromDir(final Path dir) throws IOException {
        Collection<String> allFiles = new ArrayList<>();
        FileVisitor<Path> fileVisitor = new FileSpecsFileVisitor(
                log,
                allFiles
        );
        Set<FileVisitOption> visitOptions = new LinkedHashSet<>();
        visitOptions.add(FileVisitOption.FOLLOW_LINKS);
        java.nio.file.Files.walkFileTree(dir,
                visitOptions,
                Integer.MAX_VALUE,
                fileVisitor
        );
        return allFiles;
    }

    /**
     * Extended SimpleFileVisitor for walking through the files.
     */
    private static class FileSpecsFileVisitor extends SimpleFileVisitor<Path> {
        @Nonnull
        private final Log log;
        @Nonnull
        private final Collection<String> results;

        /**
         * Constructor.
         *
         * @param pluginLog   Maven Plugin logging channel.
         * @param fileResults Initialized collection to be filled.
         */
        FileSpecsFileVisitor(
                @Nonnull final Log pluginLog,
                @Nonnull final Collection<String> fileResults
        ) {
            log = pluginLog;
            results = fileResults;
        }

        @Override
        public FileVisitResult visitFile(
                final Path aFile, final BasicFileAttributes aAttrs
        ) throws IOException {
            log.debug("Visiting file '" + aFile.toString() + "'.");
            results.add(aFile.toAbsolutePath().toString());
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(
                final Path aDir, final BasicFileAttributes aAttrs
        ) throws IOException {
            log.debug("Visiting directory '" + aDir.toString() + "'.");
            return FileVisitResult.CONTINUE;
        }

    }

}
