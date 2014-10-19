import controller.InitConnect;
import controller.Report;
import view.InitGUI;

import java.awt.*;
import java.io.IOException;

public class Main {

    public static final Cursor cursorBusy = new Cursor(Cursor.WAIT_CURSOR);
    public static final Cursor cursorDfault = new Cursor(Cursor.DEFAULT_CURSOR);

    private static InitConnect connection;

    public static void main(String[] args) throws IOException {
        final Report report = new Report();
        connection = new InitConnect(report);
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                    InitGUI initGui = new InitGUI(connection);
            }
        });
    }
}
