package me.demo.logging_Activity.plugins;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.pattern.RegexReplacement;
import org.apache.logging.log4j.message.SimpleMessage;

@Plugin(
        name = "LogWrapper",
        category = "Core",
        printObject = true
)
public class LogWrapper implements RewritePolicy {
    private final RegexReplacement[] replace;

    private LogWrapper(final RegexReplacement[] replace) {
        this.replace = replace;
    }

    @PluginFactory
    public static LogWrapper createPlugin(@PluginElement("Replace") final RegexReplacement[] replace) {
        return new LogWrapper(replace);
    }

    @Override
    public LogEvent rewrite(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        for (RegexReplacement r : replace) {
            message = r.format(message);
        }
        MutableLogEvent newEvent = new MutableLogEvent();
        newEvent.initFrom(event);
        newEvent.setMessage(new SimpleMessage(message));
        return newEvent;
    }
}