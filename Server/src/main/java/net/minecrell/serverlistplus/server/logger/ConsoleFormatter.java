package net.minecrell.serverlistplus.server.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class ConsoleFormatter extends Formatter {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        StringBuilder formatted = new StringBuilder()
                .append(DATE_FORMAT.format(record.getMillis()))
                .append(" [")
                .append(record.getLevel().getName())
                .append("] ")
                .append(formatMessage(record))
                .append('\n');

        if (record.getThrown() != null) {
            StringWriter trace = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(trace));
            formatted.append(trace);
        }

        return formatted.toString();
    }

}
