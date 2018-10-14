package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Handle FileSpecs.
 */
public class FileSpecs {

	private FileSpecs() {
		// This class cannot be instantiated.
		throw new AssertionError();
	}

    public static String absoluteCwdAndFile(@Nonnull final String filename) {
        return Paths.get(System.getProperty("user.dir"), filename).toAbsolutePath().normalize().toString();
    }

    /**
     * Process collection of file names.
     * If file name is a file, get its absolute path.
     * If not, assume it is either a directory or a wildcard name, and
     * scan it with DirectoryScanner.
     *
     * @param files A Collection of Strings
     * @return A Collection of Strings
     */
    @Nonnull
    public static Collection<String> getAbsoluteFilenames(
            @Nonnull final Collection<String> files,
            @Nonnull final Path basedir,
            @Nonnull final Log log
            ) {
        Collection<String> allFilenames = new HashSet<>();
        if(!files.isEmpty()) {
            // We have to process away files with an absolute path because
            // DirectoryScanner can't handle them (as they may not be in basedir)
            // So we take away all files that exist.
            for (String fileSpec : files) {
            	if (StringUtils.isBlank(fileSpec)) {
                    log.error(logFileIterationMsg(fileSpec, "is blank. Error in configuration") + "!");
                    continue;
                }
            	File file = new File(fileSpec);
                if (file.exists() && file.isFile()) {
                    log.debug(logFileIterationMsg(fileSpec, "is a file") + ".");
                    allFilenames.add(file.getAbsolutePath()); // If item is already in Set, discarded automatically.
                } else if (file.exists() && file.isDirectory()) {
                    log.debug(logFileIterationMsg(fileSpec, "is a directory") + ".");
                    DirectoryScanner ds = initializeDS(Paths.get(fileSpec));
                    ds.scan();
                    allFilenames.addAll(Arrays.stream(ds.getIncludedFiles()).map(includedFile -> new File(includedFile).getAbsolutePath()).collect(Collectors.toSet()));
                } else if (file.exists()) {
                    log.error(logFileIterationMsg(fileSpec, "is not a file or directory. Do not know what to do") + "!");
                } else {
                    log.debug(logFileIterationMsg(fileSpec, "does not exist. Assume wildcards") + ".");
                    DirectoryScanner ds = initializeDS(basedir);
                    ds.setIncludes(new String[]{fileSpec});
                    ds.scan();
                    Collection<String> foundFiles = Arrays.stream(ds.getIncludedFiles()).map(includedFile -> Paths.get(basedir.toString(), includedFile).toString()).collect(Collectors.toSet());
                    log.debug("    Found files:[");
                    for (final String foundFile : foundFiles) {
                        log.debug("        " + foundFile);
                    }
                    log.debug("    ]");
                    allFilenames.addAll(foundFiles);
                }
            }
        }
        return allFilenames;
    }

    private static DirectoryScanner initializeDS(@Nonnull final Path path) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setCaseSensitive(true);
        ds.setFollowSymlinks(true);
        ds.addDefaultExcludes();
        ds.setBasedir(path.toAbsolutePath().toString());
        return ds;
    }

    @Nonnull
    private static String logFileIterationMsg(@Nonnull final String filename, @Nonnull final String comment) {
        return "File spec '" + filename + "' " + comment;
    }


}
