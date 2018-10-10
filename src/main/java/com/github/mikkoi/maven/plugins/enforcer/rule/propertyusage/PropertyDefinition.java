package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.util.Objects;

import javax.annotation.Nonnull;

public class PropertyDefinition {

	@Nonnull
	private String key;
	@Nonnull
	private String value;
	@Nonnull
	private String filename;
	@Nonnull
	private int linenumber;

	public PropertyDefinition(@Nonnull String key, @Nonnull String value, @Nonnull String filename,
			@Nonnull int linenumber) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		Objects.requireNonNull(filename);
		this.key = key;
		this.value = value;
		this.filename = filename;
		this.linenumber = linenumber;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getFilename() {
		return filename;
	}

	public int getLinenumber() {
		return linenumber;
	}

}
