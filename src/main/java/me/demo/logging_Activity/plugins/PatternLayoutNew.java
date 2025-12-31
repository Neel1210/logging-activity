package me.demo.logging_Activity.plugins;

//It's a clone of PattenLayout with an array reference of RegexReplacement
//For Testing purpose only

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.impl.LocationAware;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.Encoder;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.pattern.*;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Plugin(
        name = "PatternLayoutNew",
        category = "Core",
        elementType = "layout",
        printObject = true
)
public class PatternLayoutNew extends AbstractStringLayout {
    public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
    public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %notEmpty{%x }- %m%n";
    public static final String SIMPLE_CONVERSION_PATTERN = "%d [%t] %p %c - %m%n";
    public static final String KEY = "Converter";
    private final String conversionPattern;
    private final PatternSelector patternSelector;
    private final AbstractStringLayout.Serializer eventSerializer;

    private PatternLayoutNew(final Configuration config, final RegexReplacement[] replace, final String eventPattern, final PatternSelector patternSelector, final Charset charset, final boolean alwaysWriteExceptions, final boolean disableAnsi, final boolean noConsoleNoAnsi, final String headerPattern, final String footerPattern) {
        super(config, charset, newSerializerBuilder().setConfiguration(config).setReplace(replace).setPatternSelector(patternSelector).setAlwaysWriteExceptions(alwaysWriteExceptions).setDisableAnsi(disableAnsi).setNoConsoleNoAnsi(noConsoleNoAnsi).setPattern(headerPattern).build(), newSerializerBuilder().setConfiguration(config).setReplace(replace).setPatternSelector(patternSelector).setAlwaysWriteExceptions(alwaysWriteExceptions).setDisableAnsi(disableAnsi).setNoConsoleNoAnsi(noConsoleNoAnsi).setPattern(footerPattern).build());
        this.conversionPattern = eventPattern;
        this.patternSelector = patternSelector;
        this.eventSerializer = newSerializerBuilder().setConfiguration(config).setReplace(replace).setPatternSelector(patternSelector).setAlwaysWriteExceptions(alwaysWriteExceptions).setDisableAnsi(disableAnsi).setNoConsoleNoAnsi(noConsoleNoAnsi).setPattern(eventPattern).setDefaultPattern("%m%n").build();
    }

    public static SerializerBuilder newSerializerBuilder() {
        return new SerializerBuilder();
    }

    public boolean requiresLocation() {
        return this.eventSerializer instanceof LocationAware && ((LocationAware) this.eventSerializer).requiresLocation();
    }

    public String getConversionPattern() {
        return this.conversionPattern;
    }

    public Map<String, String> getContentFormat() {
        Map<String, String> result = new HashMap();
        result.put("structured", "false");
        result.put("formatType", "conversion");
        result.put("format", this.conversionPattern);
        return result;
    }

    public String toSerializable(final LogEvent event) {
        return this.eventSerializer.toSerializable(event);
    }

    public void serialize(final LogEvent event, final StringBuilder stringBuilder) {
        this.eventSerializer.toSerializable(event, stringBuilder);
    }

    public void encode(final LogEvent event, final ByteBufferDestination destination) {
        StringBuilder text = this.toText(this.eventSerializer, event, getStringBuilder());
        Encoder<StringBuilder> encoder = this.getStringBuilderEncoder();
        encoder.encode(text, destination);
        trimToMaxSize(text);
    }

    private StringBuilder toText(final AbstractStringLayout.Serializer2 serializer, final LogEvent event, final StringBuilder destination) {
        return serializer.toSerializable(event, destination);
    }

    public static PatternParser createPatternParser(final Configuration config) {
        if (config == null) {
            return new PatternParser(config, "Converter", LogEventPatternConverter.class);
        } else {
            PatternParser parser = config.getComponent("Converter");
            if (parser == null) {
                parser = new PatternParser(config, "Converter", LogEventPatternConverter.class);
                config.addComponent("Converter", parser);
                parser = config.getComponent("Converter");
            }

            return parser;
        }
    }

    public String toString() {
        return this.patternSelector == null ? this.conversionPattern : this.patternSelector.toString();
    }

    public static PatternLayoutNew createDefaultLayout() {
        return newBuilder().build();
    }

    public static PatternLayoutNew createDefaultLayout(final Configuration configuration) {
        return newBuilder().withConfiguration(configuration).build();
    }

    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    public AbstractStringLayout.Serializer getEventSerializer() {
        return this.eventSerializer;
    }

    @PluginFactory
    @Deprecated
    public static PatternLayoutNew createLayout(@PluginAttribute(value = "pattern", defaultString = "%m%n") final String pattern, @PluginElement("PatternSelector") final PatternSelector patternSelector, @PluginConfiguration final Configuration config, @PluginElement("Replace") final RegexReplacement[] replace, @PluginAttribute("charset") final Charset charset, @PluginAttribute(value = "alwaysWriteExceptions", defaultBoolean = true) final boolean alwaysWriteExceptions, @PluginAttribute("noConsoleNoAnsi") final boolean noConsoleNoAnsi, @PluginAttribute("header") final String headerPattern, @PluginAttribute("footer") final String footerPattern) {
        return newBuilder().withPattern(pattern).withPatternSelector(patternSelector).withConfiguration(config).withRegexReplacement(replace).withCharset(charset).withAlwaysWriteExceptions(alwaysWriteExceptions).withNoConsoleNoAnsi(noConsoleNoAnsi).withHeader(headerPattern).withFooter(footerPattern).build();
    }


    private static final class NoFormatPatternSerializer implements PatternSerializer {
        private final LogEventPatternConverter[] converters;

        private NoFormatPatternSerializer(final PatternFormatter[] formatters) {
            this.converters = new LogEventPatternConverter[formatters.length];

            for (int i = 0; i < formatters.length; ++i) {
                this.converters[i] = formatters[i].getConverter();
            }

        }

        public String toSerializable(final LogEvent event) {
            StringBuilder sb = AbstractStringLayout.getStringBuilder();

            String var3;
            try {
                var3 = this.toSerializable(event, sb).toString();
            } finally {
                AbstractStringLayout.trimToMaxSize(sb);
            }

            return var3;
        }

        public StringBuilder toSerializable(final LogEvent event, final StringBuilder buffer) {
            for (LogEventPatternConverter converter : this.converters) {
                converter.format(event, buffer);
            }

            return buffer;
        }

        public boolean requiresLocation() {
            for (LogEventPatternConverter converter : this.converters) {
                if (converter instanceof LocationAware && ((LocationAware) converter).requiresLocation()) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return super.toString() + "[converters=" + Arrays.toString(this.converters) + "]";
        }
    }

    private static final class PatternFormatterPatternSerializer implements PatternSerializer {
        private final PatternFormatter[] formatters;

        private PatternFormatterPatternSerializer(final PatternFormatter[] formatters) {
            this.formatters = formatters;
        }

        public String toSerializable(final LogEvent event) {
            StringBuilder sb = AbstractStringLayout.getStringBuilder();

            String var3;
            try {
                var3 = this.toSerializable(event, sb).toString();
            } finally {
                AbstractStringLayout.trimToMaxSize(sb);
            }

            return var3;
        }

        public StringBuilder toSerializable(final LogEvent event, final StringBuilder buffer) {
            for (PatternFormatter formatter : this.formatters) {
                formatter.format(event, buffer);
            }

            return buffer;
        }

        public boolean requiresLocation() {
            for (PatternFormatter formatter : this.formatters) {
                if (formatter.requiresLocation()) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            return super.toString() + "[formatters=" + Arrays.toString(this.formatters) + "]";
        }
    }

    private static final class PatternSerializerWithReplacement implements AbstractStringLayout.Serializer, LocationAware {
        private final PatternSerializer delegate;
        private final RegexReplacement[] replace;

        private PatternSerializerWithReplacement(final PatternSerializer delegate, final RegexReplacement[] replace) {
            this.delegate = delegate;
            this.replace = replace;
        }

        public String toSerializable(final LogEvent event) {
            StringBuilder sb = AbstractStringLayout.getStringBuilder();

            String var3;
            try {
                var3 = this.toSerializable(event, sb).toString();
            } finally {
                AbstractStringLayout.trimToMaxSize(sb);
            }

            return var3;
        }

        public StringBuilder toSerializable(final LogEvent event, final StringBuilder buf) {
            StringBuilder buffer = this.delegate.toSerializable(event, buf);
            String str = buffer.toString();
            for (RegexReplacement replacement : this.replace) {
                str = replacement.format(str);
            }
            buffer.setLength(0);
            buffer.append(str);
            return buffer;
        }

        public boolean requiresLocation() {
            return this.delegate.requiresLocation();
        }

        public String toString() {
            return super.toString() + "[delegate=" + this.delegate + ", replace=" + this.replace + "]";
        }
    }

    public static class SerializerBuilder implements org.apache.logging.log4j.core.util.Builder<AbstractStringLayout.Serializer> {
        private Configuration configuration;
        private RegexReplacement[] replace;
        private String pattern;
        private String defaultPattern;
        private PatternSelector patternSelector;
        private boolean alwaysWriteExceptions;
        private boolean disableAnsi;
        private boolean noConsoleNoAnsi;

        public SerializerBuilder() {
        }

        public AbstractStringLayout.Serializer build() {
            if (Strings.isEmpty(this.pattern) && Strings.isEmpty(this.defaultPattern)) {
                return null;
            } else if (this.patternSelector == null) {
                try {
                    PatternParser parser = createPatternParser(this.configuration);
                    List<PatternFormatter> list = parser.parse(this.pattern == null ? this.defaultPattern : this.pattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi);
                    PatternFormatter[] formatters = list.toArray(PatternFormatter.EMPTY_ARRAY);
                    boolean hasFormattingInfo = false;

                    for (PatternFormatter formatter : formatters) {
                        FormattingInfo info = formatter.getFormattingInfo();
                        if (info != null && info != FormattingInfo.getDefault()) {
                            hasFormattingInfo = true;
                            break;
                        }
                    }

                    PatternSerializer serializer = hasFormattingInfo ? new PatternFormatterPatternSerializer(formatters) : new NoFormatPatternSerializer(formatters);
                    return this.replace == null ? serializer : new PatternSerializerWithReplacement(serializer, this.replace);
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Cannot parse pattern '" + this.pattern + "'", ex);
                }
            } else {
                return new PatternSelectorSerializer(this.patternSelector, this.replace);
            }
        }

        public SerializerBuilder setConfiguration(final Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public SerializerBuilder setReplace(final RegexReplacement[] replace) {
            this.replace = replace;
            return this;
        }

        public SerializerBuilder setPattern(final String pattern) {
            this.pattern = pattern;
            return this;
        }

        public SerializerBuilder setDefaultPattern(final String defaultPattern) {
            this.defaultPattern = defaultPattern;
            return this;
        }

        public SerializerBuilder setPatternSelector(final PatternSelector patternSelector) {
            this.patternSelector = patternSelector;
            return this;
        }

        public SerializerBuilder setAlwaysWriteExceptions(final boolean alwaysWriteExceptions) {
            this.alwaysWriteExceptions = alwaysWriteExceptions;
            return this;
        }

        public SerializerBuilder setDisableAnsi(final boolean disableAnsi) {
            this.disableAnsi = disableAnsi;
            return this;
        }

        public SerializerBuilder setNoConsoleNoAnsi(final boolean noConsoleNoAnsi) {
            this.noConsoleNoAnsi = noConsoleNoAnsi;
            return this;
        }
    }

    private static final class PatternSelectorSerializer implements AbstractStringLayout.Serializer, LocationAware {
        private final PatternSelector patternSelector;
        private final RegexReplacement[] replace;

        private PatternSelectorSerializer(final PatternSelector patternSelector, final RegexReplacement[] replace) {
            this.patternSelector = patternSelector;
            this.replace = replace;
        }

        public String toSerializable(final LogEvent event) {
            StringBuilder sb = AbstractStringLayout.getStringBuilder();

            String var3;
            try {
                var3 = this.toSerializable(event, sb).toString();
            } finally {
                AbstractStringLayout.trimToMaxSize(sb);
            }

            return var3;
        }

        public StringBuilder toSerializable(final LogEvent event, final StringBuilder buffer) {
            for (PatternFormatter formatter : this.patternSelector.getFormatters(event)) {
                formatter.format(event, buffer);
            }

            if (this.replace != null) {
                String str = buffer.toString();
                for (RegexReplacement replacement : this.replace) {
                    str = replacement.format(str);
                }
                buffer.setLength(0);
                buffer.append(str);
            }

            return buffer;
        }

        public boolean requiresLocation() {
            return this.patternSelector instanceof LocationAware && ((LocationAware) this.patternSelector).requiresLocation();
        }

        public String toString() {
            String builder = super.toString() +
                    "[patternSelector=" +
                    this.patternSelector +
                    ", replace=" +
                    String.valueOf(this.replace) +
                    "]";
            return builder;
        }
    }

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<PatternLayoutNew> {
        @PluginBuilderAttribute
        private String pattern;
        @PluginElement("PatternSelector")
        private PatternSelector patternSelector;
        @PluginConfiguration
        private Configuration configuration;
        @PluginElement("Replace")
        private RegexReplacement[] regexReplacement;
        @PluginBuilderAttribute
        private Charset charset;
        @PluginBuilderAttribute
        private boolean alwaysWriteExceptions;
        @PluginBuilderAttribute
        private boolean disableAnsi;
        @PluginBuilderAttribute
        private boolean noConsoleNoAnsi;
        @PluginBuilderAttribute
        private String header;
        @PluginBuilderAttribute
        private String footer;

        private Builder() {
            this.pattern = "%m%n";
            this.charset = Charset.defaultCharset();
            this.alwaysWriteExceptions = true;
            this.disableAnsi = !this.useAnsiEscapeCodes();
        }

        private boolean useAnsiEscapeCodes() {
            PropertiesUtil propertiesUtil = PropertiesUtil.getProperties();
            boolean isPlatformSupportsAnsi = !propertiesUtil.isOsWindows();
            boolean isJansiRequested = !propertiesUtil.getBooleanProperty("log4j.skipJansi", true);
            return isPlatformSupportsAnsi || isJansiRequested;
        }

        public Builder withPattern(final String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder withPatternSelector(final PatternSelector patternSelector) {
            this.patternSelector = patternSelector;
            return this;
        }

        public Builder withConfiguration(final Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder withRegexReplacement(final RegexReplacement[] regexReplacement) {
            this.regexReplacement = regexReplacement;
            return this;
        }

        public Builder withCharset(final Charset charset) {
            if (charset != null) {
                this.charset = charset;
            }

            return this;
        }

        public Builder withAlwaysWriteExceptions(final boolean alwaysWriteExceptions) {
            this.alwaysWriteExceptions = alwaysWriteExceptions;
            return this;
        }

        public Builder withDisableAnsi(final boolean disableAnsi) {
            this.disableAnsi = disableAnsi;
            return this;
        }

        public Builder withNoConsoleNoAnsi(final boolean noConsoleNoAnsi) {
            this.noConsoleNoAnsi = noConsoleNoAnsi;
            return this;
        }

        public Builder withHeader(final String header) {
            this.header = header;
            return this;
        }

        public Builder withFooter(final String footer) {
            this.footer = footer;
            return this;
        }

        public PatternLayoutNew build() {
            return new PatternLayoutNew(this.configuration, this.regexReplacement, this.pattern, this.patternSelector, this.charset, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.header, this.footer);
        }
    }

    private interface PatternSerializer extends AbstractStringLayout.Serializer, LocationAware {
    }
}