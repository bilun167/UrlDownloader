package downloader;

import com.google.inject.Inject;
import com.jcraft.jsch.*;
import generator.FileNameGenerator;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by taihuynh on 9/7/16.
 */
public class SftpDownloader extends AbstractDownloader {
    private FileNameGenerator fng;

    @Inject
    public SftpDownloader(FileNameGenerator fng) {
        this.fng = fng;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        JSch jsch = new JSch();
        String knownHostsFilename = "/Users/" + uri.getUserInfo() + "/.ssh/known_hosts";
        appendKnownHosts(jsch, knownHostsFilename);

        Session session = null;
        ChannelSftp sftpChannel = null;
        String fileName = fng.generate(uri);
        try {
            session = jsch.getSession(uri.getUserInfo(), uri.getHost());
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);
            session.setPassword(ui.getPassword());
            session.connect();

            Channel channel = session.openChannel(uri.getScheme());
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            try {
                sftpChannel.get(uri.getPath(), fileName);
            } catch (SftpException e) {
                try (InputStream is = sftpChannel.get(uri.getPath());
                        FileOutputStream fos = new FileOutputStream(fileName)) {
                    ReadableByteChannel rbc = Channels.newChannel(is);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
        } catch (JSchException|SftpException e) {
            return null;
        } finally {
            sftpChannel.exit();
            session.disconnect();
        }

        return new File(fileName);
    }

    private void appendKnownHosts(JSch jsch, String knownHostsFilename) {
        try {
            jsch.setKnownHosts(knownHostsFilename);
        } catch (JSchException e) {
            // do nothing
        }
    }

    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
        final GridBagConstraints gbc =
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 0), 0, 0);
        String passwd;
        JTextField passwordField = (JTextField) new JPasswordField(20);
        private Container panel;

        public String getPassword() {
            return passwd;
        }

        public boolean promptYesNo(String str) {
            Object[] options = {"yes", "no"};
            int foo = JOptionPane.showOptionDialog(null,
                    str,
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return foo == 0;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            Object[] ob = {passwordField};
            int result =
                    JOptionPane.showConfirmDialog(null, ob, message,
                            JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                passwd = passwordField.getText();
                return true;
            } else {
                return false;
            }
        }

        public void showMessage(String message) {
            JOptionPane.showMessageDialog(null, message);
        }

        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo) {
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts = new JTextField[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]), gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if (echo[i]) {
                    texts[i] = new JTextField(20);
                } else {
                    texts[i] = new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if (JOptionPane.showConfirmDialog(null, panel,
                    destination + ": " + name,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                    == JOptionPane.OK_OPTION) {
                String[] response = new String[prompt.length];
                for (int i = 0; i < prompt.length; i++) {
                    response[i] = texts[i].getText();
                }
                return response;
            } else {
                return null;  // cancel
            }
        }
    }
}
