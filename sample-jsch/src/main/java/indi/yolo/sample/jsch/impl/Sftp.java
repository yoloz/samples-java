package indi.yolo.sample.jsch.impl;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Vector;

public class Sftp extends AbstractFtp {


    private ChannelSftp sftp;
    private Session session;

    public Sftp(String username, String password, String host, int port, String localpath, String remotepath,
                String order, String servieId,int soTimeout) throws IOException {
        super(username, password, host, port, localpath, remotepath, order, servieId,soTimeout);
    }


    @Override
    public void login() throws Exception {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, port);
        if (password != null && !password.isEmpty()) {
            session.setPassword(password);
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setTimeout(soTimeout);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
    }

    @Override
    public void logout() throws IOException {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
        if (breakPoint != null) breakPoint.store();
    }

    @Override
    void rmkdir(String dir, int start) throws Exception {
        Path path = Paths.get(dir);
        int count = path.getNameCount();
        StringBuilder s = new StringBuilder("/");
        if (start > 0) {
            for (int i = 0; i < start; i++) {
                s.append(path.getName(i)).append("/");
            }
        }
        for (int i = start; i < count; i++) {
            s.append(path.getName(i));
            try {
                sftp.cd(s.toString());
            } catch (SftpException e) {
                sftp.mkdir(s.toString());
            }
            if (i != count - 1) s.append("/");
        }
    }


    @Override
    void _upload(String srcFile, String relative) throws Exception {
        Path remoteP = Paths.get(remotepath);
        Path targetPath = remoteP;
        if (!relative.isEmpty()) {
            if (relative.startsWith("/")) relative = relative.substring(1);
            targetPath = remoteP.resolve(relative);
            rmkdir(targetPath.toString(), remoteP.getNameCount());
        }
        sftp.put(srcFile, targetPath.toString());
    }

    @Override
    public void download() throws Exception {
        Files.createDirectories(Paths.get(localpath));
        download_r(remotepath);
    }

    private void download_r(String path) throws Exception {
        Vector lsEntries = sftp.ls(path);
        for (Object lsEntry : lsEntries) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) lsEntry;
            String fname = entry.getFilename();
            if (".".equals(fname) || "..".equals(fname)) continue;
            if (entry.getAttrs().isDir()) {
                download_r(Paths.get(path, fname).toString());
            } else if (entry.getAttrs().isReg()) {
                Path filepath = Paths.get(path);
                if (!path.endsWith(fname)) filepath = Paths.get(path, fname);
                String fileDir = filepath.getParent().toString();
                boolean download = true;
                if ("mtime".equals(order)) download = breakPoint.checkDown(fileDir, entry.getAttrs().getMTime());
                if ("fname".equals(order)) download = breakPoint.checkDown(fileDir, fname);
                if (download) _download(filepath.toString());
            }
        }
    }

    private void _download(String file) throws Exception {
        Path path = Paths.get(file);
        String relative = "";
        if (!file.equals(remotepath)) {
            if (remotepath.endsWith("/")) relative = path.getParent().toString().substring(remotepath.length() - 1);
            else relative = path.getParent().toString().substring(remotepath.length());
        }
        Path targetPath = Paths.get(localpath);
        if (!relative.isEmpty()) {
            if (relative.startsWith("/")) relative = relative.substring(1);
            targetPath = Paths.get(localpath).resolve(relative);
            Files.createDirectories(Paths.get(localpath).resolve(relative));
        }
        Path dstPath = targetPath.resolve(path.getFileName());
        sftp.get(file, dstPath.toString());
        if (file.endsWith(".tar.gz")) {
            Utils.extractTar_Gz(dstPath.toString(), targetPath.toString());
            Files.delete(dstPath);
        }
    }
}
