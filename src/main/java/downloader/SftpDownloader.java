package downloader;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by taihuynh on 9/7/16.
 */
public class SftpDownloader implements Downloader {
    @Override
    public void download(URI uri) throws IOException {
        JSch jsch = new JSch();
        String knownHostsFilename = "/Users/" + uri.getUserInfo() + "/.ssh/known_hosts";
        appendKnownHosts(jsch, knownHostsFilename);

        Session session = null;
        ChannelSftp sftpChannel = null;
        try {
            session = jsch.getSession(uri.getUserInfo(), uri.getHost());
            // "interactive" version
            // can selectively update specified known_hosts file
            // need to implement UserInfo interface
            // MyUserInfo is a swing implementation provided in
            //  examples/Sftp.java in the JSch dist
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);

            // OR non-interactive version. Relies in host key being in known-hosts file
            session.setPassword(ui.getPassword());

            session.connect();

            Channel channel = session.openChannel(uri.getScheme());
            channel.connect();

            sftpChannel = (ChannelSftp) channel;

            try {
                sftpChannel.get(uri.getPath(), "a");
            } catch (SftpException e) {
                e.printStackTrace();

                InputStream is = sftpChannel.get(uri.getPath());
                // process inputstream as needed
                try (FileOutputStream fos = new FileOutputStream("a")) {
                    ReadableByteChannel rbc = Channels.newChannel(is);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            sftpChannel.exit();
            session.disconnect();
        }
    }

    private void appendKnownHosts(JSch jsch, String knownHostsFilename) {
        try {
            jsch.setKnownHosts(knownHostsFilename);
        } catch (JSchException e) {
            // do nothing
            e.printStackTrace();
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

    public static void main(String[] args) {
        try {
            URI uri = new URI("sftp://taihuynh@tais-mbp://Users/taihuynh/jayeson/workspace/jayeson.portal.admin/app-client/typings.json");
            SftpDownloader dl = new SftpDownloader();
            dl.download(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
