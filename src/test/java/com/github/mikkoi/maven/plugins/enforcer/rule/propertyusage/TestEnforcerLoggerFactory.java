package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Supplier;

public final class TestEnforcerLoggerFactory {

    public static @NonNull EnforcerLogger createTestEnforcerLogger()
    {
        return new EnforcerLogger() {

            private final Log log = new SystemStreamLog();

            @Override
            public void warnOrError(CharSequence message) {

            }

            @Override
            public void warnOrError(Supplier<CharSequence> messageSupplier) {

            }

            @Override
            public boolean isDebugEnabled() {
//                return log.isDebugEnabled();
                return false;
            }

            @Override
            public void debug(CharSequence message) {
                if (isDebugEnabled()) {
                    log.debug(message);
                }
            }

            @Override
            public void debug(Supplier<CharSequence> messageSupplier) {
                if (isDebugEnabled()) {
                    log.debug(messageSupplier.get());
                }
            }

            @Override
            public boolean isInfoEnabled() {
//                return log.isInfoEnabled();
                return false;
            }

            @Override
            public void info(CharSequence message) {
                if (log.isInfoEnabled()) {
                    log.info(message);
                }
            }

            @Override
            public void info(Supplier<CharSequence> messageSupplier) {
                if (log.isInfoEnabled()) {
                    log.info(messageSupplier.get());
                }
            }

            @Override
            public boolean isWarnEnabled() {
                return log.isWarnEnabled();
            }

            @Override
            public void warn(CharSequence message) {
                log.warn(message);
            }

            @Override
            public void warn(Supplier<CharSequence> messageSupplier) {
                if (log.isWarnEnabled()) {
                    log.warn(messageSupplier.get());
                }
            }

            @Override
            public boolean isErrorEnabled() {
//                return log.isErrorEnabled();
                return false;
            }

            @Override
            public void error(CharSequence message) {
                log.error(message);
            }

            @Override
            public void error(Supplier<CharSequence> messageSupplier) {
                if (log.isErrorEnabled()) {
                    log.error(messageSupplier.get());
                }
            }
        };
    }
}
