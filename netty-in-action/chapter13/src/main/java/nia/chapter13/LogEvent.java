package nia.chapter13;

import java.net.InetSocketAddress;

/**
 * Listing 13.1 LogEvent message
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * 数据通常由POJO表示，除了实际上的消息内容，其还可以包含配置或处理信息。
 *
 * 在这个应用程序中，我们将会把消息作为事件处理，并且由于该数据来自于日志文件，所以我们将它称为LogEvent。
 */
public final class LogEvent {
    public static final byte SEPARATOR = (byte) ':';
    private final InetSocketAddress source;
    private final String logfile;
    private final String msg;
    private final long received;

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received,
        String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceivedTimestamp() {
        return received;
    }
}
