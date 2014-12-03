package view;

import controller.InitConnect;
import controller.Report;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.logging.Level;

public class InitGUI extends JFrame {

    Font font = new Font("Verdana", Font.BOLD,11);

    private File selectedFile;

    private Container container;
    private JPanel panelConnection;
    private JPanel panelUpload;
    private JPanel panelListOfUsers;
    private JPanel panelDownload;
    private JPanel panelStatus;
    private JPanel panelMain;
    private JPanel panelInfo;

    private JMenuBar menubar;
    private JMenu menu;
    private JMenuItem menuSettings;
    private JMenuItem close;

    private JLabel labelStatus;
    private JTextField txtColorStatus;

    private JLabel labelUploadFile;
    private JButton btnUploadFile;
    private JButton btnDownloadFile;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JButton btnRefreshList;

    private JProgressBar progressBar;

    private JLabel labelServer;
    private JLabel labelServerInfo;
    private JLabel labelProxy;
    private JLabel labelProxyInfo;
    private JLabel labelClient;
    private JLabel labelClientInfo;

    private JList<String> listFilesForDownload;
    private int intSelectedFileForDownload;
    private String stringSelectedFileForDownload;
    private JList listUsers;

    private Border border;

    private Insets insets;

    private InitConnect connection;

    public InitGUI(InitConnect connection) {
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Report.getFh().flush();
            }
        });

        this.connection = connection;

        insets = new Insets(5, 5, 10, 10);
        container = getContentPane();
        container.setLayout(new BorderLayout());

        setLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);

        panelMain = new JPanel(new GridBagLayout());
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelMain.add(addPanelConnection(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelMain.add(addPanelUpload(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelMain.add(addPanelDownload(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelMain.add(addPanelInfo(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
    //    panelMain.add(addPanelUsers(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        container.add(addPanelStatus(), BorderLayout.SOUTH);

        container.add(panelMain, BorderLayout.CENTER);

        setJMenuBar(generateMenu());

        pack();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ico.png")));
        setBounds(20, 20, 20, 20);
        setResizable(false);
        setSize(750, 400);
        if (InitConnect.getSettingsFromFile().getNickName() != null){
			String message;
			if(InitConnect.getSettingsFromFile().getAnonym()==true){
				message = "enable";
			}else{
				message = "disable";
			}
            setTitle("Client " + InitConnect.getSettingsFromFile().getNickName() + " - " + "anonymous is " +
					message );
        }else {
            setTitle("Client");
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        connection.connectionStatus = InitConnect.DISCONNECTED;
        changeGUIStatus();
        setVisible(true);
    }

    private JPanel addPanelInfo() {
        Font myFont = new Font("Serif", Font.BOLD, 12);
        panelInfo = new JPanel(true);
		panelInfo.setPreferredSize(new Dimension(250, 200));
        panelInfo.setFont(font);
        panelInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Info"));

        panelInfo.setLayout(new GridBagLayout());
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelServer = new JLabel("Server IP");
        panelInfo.add(labelServer, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelProxy = new JLabel("Proxy IP");
        panelInfo.add(labelProxy, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelClient = new JLabel("Client IP");
        panelInfo.add(labelClient, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelServerInfo = new JLabel("Disconnected");
        labelServerInfo.setFont(myFont);
        panelInfo.add(labelServerInfo, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelProxyInfo = new JLabel("Disconnected");
        labelProxyInfo.setFont(myFont);
        panelInfo.add(labelProxyInfo, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        labelClientInfo = new JLabel("Disconnected");
        labelClientInfo.setFont(myFont);
        panelInfo.add(labelClientInfo, gbc);

        return panelInfo;
    }

    private JPanel addPanelConnection(){
        panelConnection = new JPanel(true);
        panelConnection.setFont(font);
        panelConnection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Connection"));

        btnConnect = new JButton("Connect");
        btnConnect.setFont(font);
        btnConnect.addActionListener(new ButtonListener(this));
        panelConnection.add(btnConnect);

        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setFont(font);
        btnDisconnect.addActionListener(new ButtonListener(this));
        panelConnection.add(btnDisconnect);

        return panelConnection;
    }

    private JPanel addPanelUpload(){
        panelUpload = new JPanel(true);
        panelUpload.setFont(font);
        panelUpload.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Upload file..."));

        labelUploadFile = new JLabel();
        labelUploadFile.setFont(font);
        labelUploadFile.setPreferredSize(new Dimension(100,20));
        labelUploadFile.addMouseListener(new LabelListener(this));
        panelUpload.add(labelUploadFile);

        btnUploadFile = new JButton("Upload file");
        btnUploadFile.setFont(font);
        btnUploadFile.addActionListener(new ButtonListener(this));
        panelUpload.add(btnUploadFile);

        return panelUpload;
    }

    private JPanel addPanelDownload(){
        panelDownload = new JPanel(true);
        panelDownload.setFont(font);
		panelDownload.setPreferredSize(new Dimension(300, 200));
        panelDownload.setLayout(new GridBagLayout());
        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
    //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelDownload.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Download file..."));

        listFilesForDownload = new JList<String>();
        listFilesForDownload.setFont(font);
        listFilesForDownload.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listFilesForDownload.setLayoutOrientation(JList.VERTICAL);
        listFilesForDownload.setVisibleRowCount(- 1);
        listFilesForDownload.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                boolean adjust = e.getValueIsAdjusting();
                if(!adjust){
                    btnDownloadFile.setEnabled(true);
                    stringSelectedFileForDownload = listFilesForDownload.getSelectedValue();
                    intSelectedFileForDownload = listFilesForDownload.getSelectedIndex();
                }else{
                    btnDownloadFile.setEnabled(false);
                }
            }
        });
        JScrollPane listScrollBar = new JScrollPane(listFilesForDownload);
        listScrollBar.setPreferredSize(new Dimension(340,100));
        panelDownload.add(listScrollBar, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        btnRefreshList = new JButton("Update List");
        btnRefreshList.setFont(font);
        btnRefreshList.addActionListener(new ButtonListener(this));
        panelDownload.add(btnRefreshList, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
    //    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        btnDownloadFile = new JButton("Donwload file");
        btnDownloadFile.setFont(font);
        btnDownloadFile.addActionListener(new ButtonListener(this));
        panelDownload.add(btnDownloadFile, gbc);

        return panelDownload;
    }

    private JPanel addPanelUsers(){
        panelListOfUsers = new JPanel(true);
        panelListOfUsers.setFont(font);
        panelListOfUsers.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Users online"));

    //    String[] selections = { "green", "red", "orange", "dark blue", "red", "orange", "dark blue", "red", "green",
    //           "red", "orange", "dark blue", "red", "orange", "dark blue", "red", "orange", "dark blue" };
        listUsers = new JList();
        listUsers.setFont(font);
        listUsers.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listUsers.setLayoutOrientation(JList.VERTICAL);
        listUsers.setVisibleRowCount(-1);
        listUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                }else{
                }
            }
        });
        JScrollPane listScrollBar = new JScrollPane(listUsers);
        listScrollBar.setPreferredSize(new Dimension(500, 100));
        panelListOfUsers.add(listScrollBar);

        return panelListOfUsers;
    }

    private JPanel addPanelStatus(){
        panelStatus = new JPanel(true);
        panelStatus.setLayout(new GridBagLayout());

        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = insets;

        gbc.gridx = 0;
        txtColorStatus = new JTextField(1);
        txtColorStatus.setBorder(border);
        txtColorStatus.setEnabled(false);
        panelStatus.add(txtColorStatus, gbc);

        gbc.gridx = 1;
        labelStatus = new JLabel();
        labelStatus.setFont(font);
        labelStatus.setPreferredSize(new Dimension(400, 20));
        labelStatus.setHorizontalAlignment(SwingConstants.LEFT);
        panelStatus.add(labelStatus, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        progressBar = new JProgressBar();
		progressBar.setVisible(false);
        panelStatus.add(progressBar, gbc);

        return panelStatus;
    }

    private JMenuBar generateMenu(){

        menubar = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menubar.add(menu);

        menuSettings = new JMenuItem("Settings");
        menuSettings.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        menuSettings.addActionListener(new MenuListener(this));
        menu.add(menuSettings);
        menu.addSeparator();

        close = new JMenuItem("Exit");
        close.addActionListener(new MenuListener(this));
        close.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.CTRL_DOWN_MASK));
        menu.add(close);
        return menubar;
    }

    public void changeGUIStatus(){
        if(connection.connectionStatus==InitConnect.CONNECTED){
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnRefreshList.setEnabled(true);
            labelUploadFile.setText("Click to select a file for upload...");

            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.CONNECTED]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.CONNECTED]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.CONNECTED]);
            connection.setLabels( labelProxyInfo, labelServerInfo, labelClientInfo );
        }else if(connection.connectionStatus==InitConnect.DISCONNECTED) {
            listFilesForDownload.setModel(new DefaultListModel<String>());
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            btnDownloadFile.setEnabled(false);
            btnUploadFile.setEnabled(false);
            btnRefreshList.setEnabled(false);
            labelUploadFile.setText("Click to select a file for upload...");
            labelUploadFile.setEnabled(false);

            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.DISCONNECTED]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.DISCONNECTED]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.DISCONNECTED]);

            labelProxyInfo.setText("Disconnected");
            labelServerInfo.setText("Disconnected");
            labelClientInfo.setText("Disconnected");
        }else if(connection.connectionStatus==InitConnect.FAIL_CONNECTION) {
            btnConnect.setEnabled(true);
            labelProxyInfo.setText("Disconnected");
            labelServerInfo.setText("Disconnected");
            labelClientInfo.setText("Disconnected");
        }else if(connection.connectionStatus==InitConnect.REFRESHING){
            btnRefreshList.setEnabled(false);
            labelProxyInfo.setText("Disconnected");
            labelServerInfo.setText("Disconnected");
            labelClientInfo.setText("Disconnected");
        }else {
            btnConnect.setEnabled(false);
            btnRefreshList.setEnabled(false);
            btnDisconnect.setEnabled(false);
            btnDownloadFile.setEnabled(false);
            btnUploadFile.setEnabled(false);
            labelUploadFile.setText("Click to select a file for upload...");
            labelUploadFile.setEnabled(false);
            labelProxyInfo.setText("Disconnected");
            labelServerInfo.setText("Disconnected");
            labelClientInfo.setText("Disconnected");
        }
        if(connection.connectionStatus==InitConnect.DISCONNECTING){
            listFilesForDownload.setModel(new DefaultListModel<String>());
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.DISCONNECTING]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.DISCONNECTING]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.DISCONNECTING]);
        }else if(connection.connectionStatus==InitConnect.CONNECTING){
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.CONNECTING]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.CONNECTING]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.CONNECTING]);
        }else if(connection.connectionStatus==InitConnect.FAIL_CONNECTION){
            listFilesForDownload.setModel(new DefaultListModel<String>());
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.FAIL_CONNECTION]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.FAIL_CONNECTION]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.FAIL_CONNECTION]);
        }else if(connection.connectionStatus==InitConnect.DOWNLOADING){
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.DOWNLOADING]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.DOWNLOADING]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.DOWNLOADING]);
        }else if(connection.connectionStatus==InitConnect.UPLOADING){
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.UPLOADING]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.UPLOADING]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.UPLOADING]);
        }else if(connection.connectionStatus==InitConnect.REFRESHING){
            border = BorderFactory.createLineBorder(InitConnect.statusColors[InitConnect.REFRESHING]);
            txtColorStatus.setBackground(InitConnect.statusColors[InitConnect.REFRESHING]);
            labelStatus.setText(InitConnect.statusMessages[InitConnect.REFRESHING]);
        }
    }

    public static void setLookAndFeel(){
        String metalClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
        String motifClassName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        String windowsClassName  = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
        String OS = null;

        try{
            if(OS == null) { OS = System.getProperty("os.name"); }

            if(OS.startsWith("Windows")){
                UIManager.setLookAndFeel(windowsClassName);
            }else{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private class MenuListener extends Component implements ActionListener {

        private InitGUI mainFrame;
        private FrameSettings frameSettings;

        public MenuListener(InitGUI initGUI) {
            mainFrame = initGUI;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==menuSettings){
                if(connection.connectionStatus==InitConnect.DISCONNECTED ||
                        connection.connectionStatus==InitConnect.FAIL_CONNECTION ){
                    frameSettings = new FrameSettings(mainFrame);
                }else{
                    JOptionPane.showMessageDialog(this, "You cannot configure seetings, please disconnect first",
                            "Error with configuration", JOptionPane.ERROR_MESSAGE );
                }
            }else if(e.getSource()==close){
            //    System.out.println("selected "+e.getActionCommand());
                if(connection.connectionStatus==InitConnect.CONNECTED){
                    connection.unSetConnection(mainFrame);
                    Report.lgr.log(Level.INFO, "logout - user: " + InitConnect.getSettingsFromFile().getNickName(), "");
                }else{
                    Report.lgr.log(Level.INFO, "close program - user: " + InitConnect.getSettingsFromFile().getNickName
                            (), "");
                }
                Report.getFh().flush();
                System.exit(0);
            }else{
                System.out.println("wrong input");
            }
        }
    }

    private class ButtonListener implements ActionListener {

        private InitGUI initGUI;

        public ButtonListener(InitGUI initGUI) {
            this.initGUI = initGUI;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==btnUploadFile){
                connection.connectionStatus = InitConnect.UPLOADING;
                changeGUIStatus();
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);

				//    btnUploadFile.setEnabled(false);
                connection.informServerAboutMyRequestUpload(selectedFile);

                // update jlist
                DefaultListModel<String> files = connection.informServerAboutMyRequestGetList();
                files.removeElementAt(files.size()-1);
                listFilesForDownload.setModel(files);

                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
                connection.connectionStatus = InitConnect.CONNECTED;
                changeGUIStatus();
            }else if(e.getSource()==btnDownloadFile){
                connection.connectionStatus = InitConnect.DOWNLOADING;
                progressBar.setVisible(true);
                progressBar.setIndeterminate(true);
                changeGUIStatus();
                connection.informServerAboutMyRequestDownload(stringSelectedFileForDownload);
                connection.connectionStatus = InitConnect.CONNECTED;
                changeGUIStatus();
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
            }else if(e.getSource()==btnRefreshList){
                connection.connectionStatus = InitConnect.REFRESHING;
                changeGUIStatus();
                DefaultListModel<String> files = connection.informServerAboutMyRequestGetList();
                files.removeElementAt(files.size()-1);
                listFilesForDownload.setModel(files);
                connection.connectionStatus = InitConnect.CONNECTED;
                changeGUIStatus();
            }else if(e.getSource()==btnConnect){
				progressBar.setVisible(true);
				progressBar.setIndeterminate(true);

				connection.setConnection(initGUI);
				changeGUIStatus();
				progressBar.setVisible(false);
				progressBar.setIndeterminate(false);
            }else if(e.getSource()==btnDisconnect){
				progressBar.setVisible(true);
				progressBar.setIndeterminate(true);
                connection.unSetConnection(initGUI);
                changeGUIStatus();
				progressBar.setVisible(false);
				progressBar.setIndeterminate(false);
            }
        }
    }

    private class LabelListener implements MouseListener {

        private InitGUI initGUI;

        public LabelListener(InitGUI initGUI) {
            this.initGUI = initGUI;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource()==labelUploadFile) {
                if(connection.connectionStatus==InitConnect.CONNECTED){
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    int result = fileChooser.showOpenDialog(initGUI);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        selectedFile = fileChooser.getSelectedFile();
                        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                        labelUploadFile.setText(selectedFile.getName());
                        btnUploadFile.setEnabled(true);
                        Report.lgr.log(Level.INFO, "file selected to upload: " + selectedFile.getAbsolutePath(), "");
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}