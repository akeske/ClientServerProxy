package view;

import controller.InitConnect;
import controller.Report;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class FrameSettings extends JDialog{

    private Insets insets;
    private Report report;

    private JPanel mainPanel;
    private JPanel panelServer;
    private JPanel panelProxy;
    private JPanel panelClient;
    private JLabel labelIP;
    private JLabel labelPort;
    private JLabel labelNickName;
    private JTextField txtFieldIP;
    private JTextField txtFieldPort;
    private JTextField txtFieldProxyIP;
    private JTextField txtFieldProxyPort;
    private JTextField txtNickName;

    private JButton btnOK;
    private JButton btnCancel;

    private InitGUI mainFrame;

    public FrameSettings(InitGUI mainFrame){
        // trexei to constructor tis JDialog
        // dilwnoume ws parent frame tis arxiki mainFrame tou programmatos
        super(mainFrame);
        this.mainFrame = mainFrame;
        insets = new Insets(10, 10, 10, 10);

        setLayout(new BorderLayout());
        setFont(new Font("Helvetica", Font.PLAIN, 16));
        add(initGUISettings(), BorderLayout.CENTER);

        InitGUI.setLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this);

        getTxtFieldIP().setText(InitConnect.getSettingsFromFile().getServerIP());
        getTxtFieldPort().setText(InitConnect.getSettingsFromFile().getPortNumber());
        getTxtNickName().setText(InitConnect.getSettingsFromFile().getNickName());

        setBounds(20, 20, 20, 20);
        setMinimumSize(new Dimension(500, 250));
      //  setResizable(false);
        setModal(true);
        Point p = mainFrame.getLocation();
        p.x = p.x + mainFrame.getSize().width/2 - this.getSize().width/2;
        p.y = p.y + mainFrame.getSize().height/2 - this.getSize().height/2;
        setLocation(p);
        setTitle("Connection setup");
        pack();
        setVisible(true);
        validate();
    }

    private JPanel initGUISettings(){
        GridBagConstraints gbc;
        mainPanel = new JPanel(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configuration settings"));
        mainPanel.setLayout(new GridBagLayout());

        panelServer = new JPanel(true);
        panelServer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Server settings"));
        panelServer.setLayout(new GridBagLayout());
        labelIP = new JLabel("IP number");
        labelIP.addKeyListener(new keyListenerEnterEscape(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelServer.add(labelIP, gbc);

        txtFieldIP = new JTextField();
        txtFieldIP.addKeyListener(new keyListenerEnterEscape(this));
        txtFieldIP.setToolTipText("Please insert the IP of server");
        txtFieldIP.setPreferredSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelServer.add(txtFieldIP, gbc);

        labelPort = new JLabel("Port number");
        labelPort.addKeyListener(new keyListenerEnterEscape(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelServer.add(labelPort, gbc);

        txtFieldPort = new JTextField();
        txtFieldPort.addKeyListener(new keyListenerEnterEscape(this));
        txtFieldPort.setToolTipText("Please insert the port number of server");
        txtFieldPort.setPreferredSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelServer.add(txtFieldPort, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        mainPanel.add(panelServer, gbc);

        panelProxy = new JPanel(true);
        panelProxy.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Proxy settings"));
        panelProxy.setLayout(new GridBagLayout());
        labelIP = new JLabel("IP number");
        labelIP.addKeyListener(new keyListenerEnterEscape(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelProxy.add(labelIP, gbc);

        txtFieldProxyIP = new JTextField();
        txtFieldProxyIP.addKeyListener(new keyListenerEnterEscape(this));
        txtFieldProxyIP.setToolTipText("Please insert the IP of proxy");
        txtFieldProxyIP.setPreferredSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelProxy.add(txtFieldProxyIP, gbc);

        labelPort = new JLabel("Port number");
        labelPort.addKeyListener(new keyListenerEnterEscape(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelProxy.add(labelPort, gbc);

        txtFieldProxyPort = new JTextField();
        txtFieldProxyPort.addKeyListener(new keyListenerEnterEscape(this));
        txtFieldProxyPort.setToolTipText("Please insert the port number of proxy");
        txtFieldProxyPort.setPreferredSize(new Dimension(70, 20));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelProxy.add(txtFieldProxyPort, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        mainPanel.add(panelProxy, gbc);

        panelClient = new JPanel(true);
        panelClient.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "User settings"));
        panelClient.setLayout(new GridBagLayout());
        labelNickName = new JLabel("Insert a nickname");
        labelNickName.addKeyListener(new keyListenerEnterEscape(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelClient.add(labelNickName, gbc);

        txtNickName = new JTextField();
        txtNickName.addKeyListener(new keyListenerEnterEscape(this));
        txtNickName.setToolTipText("Please insert a nickname");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panelClient.add(txtNickName, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        mainPanel.add(panelClient, gbc);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ButtonLister(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        mainPanel.add(btnCancel, gbc);

        btnOK = new JButton("Save");
        btnOK.addActionListener(new ButtonLister(this));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        mainPanel.add(btnOK, gbc);

        return mainPanel;
    }

    private void saveSettings(){
        File settingsFile = new File("connect.ini");
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(settingsFile));
            output.write("serverip="+txtFieldIP.getText());
            output.newLine();
            output.write("serverport=" + txtFieldPort.getText());
            output.newLine();
            output.write("proxyip="+txtFieldProxyIP.getText());
            output.newLine();
            output.write("proxyport=" + txtFieldProxyPort.getText());
            output.newLine();
            output.write("nickname=" + txtNickName.getText());
            output.close();
            InitConnect.getSettingsFromFile().setServerIP(getTxtFieldIP().getText());
            InitConnect.getSettingsFromFile().setPortNumber(getTxtFieldPort().getText());
            InitConnect.getSettingsFromFile().setNickName(getTxtNickName().getText());
            JOptionPane.showMessageDialog(this, "The settings were saved!", "Inform message",  JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "The settings were NOT saved!", "Problem with saving the settings!",
                    JOptionPane.ERROR_MESSAGE );
        }
    }

    public JTextField getTxtFieldPort() {
        return txtFieldPort;
    }

    public void setTxtFieldPort(JTextField txtFieldPort) {
        this.txtFieldPort = txtFieldPort;
    }

    public JTextField getTxtFieldIP() {
        return txtFieldIP;
    }

    public void setTxtFieldIP(JTextField txtFieldIP) {
        this.txtFieldIP = txtFieldIP;
    }

    public JTextField getTxtNickName() {
        return txtNickName;
    }

    public void setTxtNickName(JTextField txtNickName) {
        this.txtNickName = txtNickName;
    }

    public JTextField getTxtFieldProxyPort() {
        return txtFieldProxyPort;
    }

    public void setTxtFieldProxyPort(JTextField txtFieldProxyPort) {
        this.txtFieldProxyPort = txtFieldProxyPort;
    }

    public JTextField getTxtFieldProxyIP() {
        return txtFieldProxyIP;
    }

    public void setTxtFieldProxyIP(JTextField txtFieldProxyIP) {
        this.txtFieldProxyIP = txtFieldProxyIP;
    }

    private class ButtonLister implements ActionListener {

        private FrameSettings frameSettings;

        public ButtonLister(FrameSettings frameSettings) {
            this.frameSettings = frameSettings;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==btnCancel) {
                frameSettings.dispose();
            }else if(e.getSource()==btnOK){
                frameSettings.saveSettings();
                frameSettings.dispose();
                mainFrame.setTitle("Cleint - " + InitConnect.getSettingsFromFile().getNickName());
            }
        }
    }

    private class keyListenerEnterEscape implements KeyListener {

        private FrameSettings frameSettings;

        public keyListenerEnterEscape(FrameSettings frameSettings) {
            this.frameSettings = frameSettings;
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(e.getKeyCode()==KeyEvent.VK_ENTER){
                frameSettings.saveSettings();
                frameSettings.dispose();
                mainFrame.setTitle(InitConnect.getSettingsFromFile().getNickName());
            }else if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                frameSettings.dispose();
            }
        }
    }
}
