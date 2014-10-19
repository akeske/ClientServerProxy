package controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Report {

    public static Logger lgr;
    private static FileHandler fh;
    public static SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");;

    public Report() throws IOException {
        lgr = Logger.getLogger(Report.class.getName());
    //    fh = new FileHandler("logClient @ " + Report.format.format(System.currentTimeMillis()) + ".html", true);
        fh = new FileHandler("logClient.html", true);
        fh.setFormatter(new MyHtmlFormatter());
        lgr.addHandler(fh);
        lgr.log(Level.INFO, "Start program", "Test");
    }

    public static FileHandler getFh() {
        return fh;
    }
}

class MyHtmlFormatter extends Formatter{
    @Override
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("<tr>\n");
        // colorize any levels >= WARNING in red
        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("\t<td style=\"color:red\">");
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        } else {
            buf.append("\t<td style=\"color:green\">");
            buf.append(rec.getLevel());
        }
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append( Report.format.format(rec.getMillis()) );
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(rec.getSourceClassName());
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(rec.getSourceMethodName());
        buf.append("</td>\n");
        buf.append("\t<td>");
        buf.append(formatMessage(rec));
        buf.append("</td>\n");
        buf.append("</tr>\n");
        return buf.toString();
    }

    @Override
    public String getTail(Handler h) {
        return "</table>\n</body>\n</html>";
    }

    @Override
    public String getHead(Handler h) {
        return "<!DOCTYPE html>\n<head>\n<style "
                + "type=\"text/css\">\n"
                + "table { width: 100%; border:1px solid }\n"
                + "th { font:bold 10pt Tahoma; }\n"
                + "td { font:normal 10pt Tahoma; }\n"
                + "h1 {font:normal 11pt Tahoma;}\n"
                + "</style>\n"
                + "</head>\n"
                + "<body align=\"center\">\n"
                + "<h1>" + (new Date()) + "</h1>\n"
                + "<table border=\"1\" align=\"center\" cellpadding=\"5\" cellspacing=\"1\">\n"
                + "<tr align=\"left\">\n"
                + "\t<th style=\"width:10%\">Loglevel</th>\n"
                + "\t<th style=\"width:20%\">Time</th>\n"
                + "\t<th style=\"width:20%\">Class</th>\n"
                + "\t<th style=\"width:10%\">Function</th>\n"
                + "\t<th style=\"width:40%\">Log Message</th>\n"
                + "</tr>\n";
    }
}