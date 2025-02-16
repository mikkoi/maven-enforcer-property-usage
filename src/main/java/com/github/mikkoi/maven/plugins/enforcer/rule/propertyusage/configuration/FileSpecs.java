package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.StringUtils;

import org.checkerframework.checker.nullness.qual.NonNull;
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

    public static String absoluteCwdAndFile(final @NonNull String filename) {
        return Paths.get(System.getProperty("user.dir"), filename).toAbsolutePath().normalize().toString();
    }

    /**
     * Process collection of file names.
     * If file name is a file, get its absolute path.
     * If not, assume it is either a directory or a wildcard name, and
     * scan it with DirectoryScanner.
     * If a file is discovered multiple times, the additional entries are discarded.
     * This could happen, for instance, if paths withs wildcards overlap.
     *
     * @param files A Collection of Strings
     * @return A Collection of Strings
     */
    public static @NonNull Collection<String> getAbsoluteFilenames(
            final @NonNull Collection<String> files,
            final @NonNull Path basedir,
            final @NonNull EnforcerLogger log
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
                    allFilenames.addAll(Arrays.stream(ds.getIncludedFiles())
                            .map(includedFile -> new File(includedFile).getAbsolutePath())
                            .collect(Collectors.toSet()));
                } else if (file.exists()) {
                    log.error(logFileIterationMsg(fileSpec, "is not a file or directory. Do not know what to do") + "!");
                } else {
                    log.debug(logFileIterationMsg(fileSpec, "does not exist. Assume wildcards") + ".");
                    DirectoryScanner ds = initializeDS(basedir);
                    ds.setIncludes(new String[]{fileSpec});
                    ds.scan();
                    Collection<String> foundFiles = Arrays.stream(ds.getIncludedFiles())
                            .map(includedFile -> Paths.get(basedir.toString(), includedFile).toAbsolutePath().toString())
                            .collect(Collectors.toSet());
                    log.debug("    Found files:[");
                    for (final String foundFile : foundFiles) {
                        log.debug("        " + foundFile);
                    }
                    log.debug("    ]");
                    allFilenames.addAll(foundFiles);
                }
            }
        }
//        log.debug("All discovered files: [\n" + allFilenames.stream().map(fn -> fn + "\n").sorted().collect(Collectors.toList()) + "]");
        log.debug("All discovered files: [");
        for (final String fn : allFilenames) {
            log.debug("    " + fn);
        }
        log.debug("]");
        return allFilenames;
    }

    private static DirectoryScanner initializeDS(final @NonNull Path path) {
        DirectoryScanner ds = new DirectoryScanner();
        ds.setCaseSensitive(true);
        ds.setFollowSymlinks(true);
        ds.addDefaultExcludes();
        ds.setBasedir(path.toAbsolutePath().toString());
        return ds;
    }

    private static @NonNull String logFileIterationMsg(final @NonNull String filename, final @NonNull String comment) {
        return "File spec '" + filename + "' " + comment;
    }


}
