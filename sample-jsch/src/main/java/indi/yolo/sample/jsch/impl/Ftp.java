package indi.yolo.sample.jsch.impl;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Ftp extends AbstractFtp {

    private FTPClient ftpClient;


    private boolean passive;
    private boolean useEpsvWithIPv4;
    private int idleTime;

    public Ftp(String username, String password, String host, int port, String localpath, String remotepath,
               String order, String servieId, int soTimeout, boolean passive, boolean useEpsvWithIPv4, int idleTime)
            throws IOException {
        super(username, password, host, port, localpath, remotepath, order, servieId, soTimeout);
        this.passive = passive;
        this.useEpsvWithIPv4 = useEpsvWithIPv4;
        this.idleTime = idleTime;
    }

    @Override
    public void login() throws Exception {
        try {
            ftpClient = new FTPClient();
            ftpClient.setDefaultTimeout(soTimeout);
            ftpClient.connect(host, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new Exception("FTP server[" + host + ":" + port + "] refused connection.");
            }
        } catch (IOException e) {
            logout();
            throw new Exception("Could not connect to server[" + host + ":" + port + "].", e);
        }
        if (!ftpClient.login(username, password)) {
            logout();
            throw new Exception("User[" + username + "] login ftp server failure.");
        }
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        if (passive) ftpClient.enterLocalPassiveMode();
        else ftpClient.enterLocalActiveMode();
        ftpClient.setUseEPSVwithIPv4(useEpsvWithIPv4);
        ftpClient.setControlKeepAliveTimeout(idleTime);
    }

    @Override
    public void logout() throws IOException {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ignore) {
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
            if (!ftpClient.changeWorkingDirectory(s.toString())) {
                ftpClient.makeDirectory(s.toString());
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
        Path sf = Paths.get(srcFile);
        try (InputStream fi = Files.newInputStream(sf);
             BufferedInputStream bi = new BufferedInputStream(fi)) {
            ftpClient.changeWorkingDirectory(targetPath.toString());
            ftpClient.storeFile(sf.getFileName().toString(), bi);
        }
    }

    @Override
    public void download() throws Exception {
        Files.createDirectories(Paths.get(localpath));
        download_r(remotepath);
    }

    private void download_r(String path) throws Exception {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isValid()) {
                String fname = ftpFile.getName();
                if (ftpFile.isDirectory()) {
                    download_r(Paths.get(path, fname).toString());
                } else if (ftpFile.isFile()) {
                    Path filepath = Paths.get(path);
                    if (!path.endsWith(fname)) filepath = Paths.get(path, fname);
                    String fileDir = filepath.getParent().toString();
                    boolean download = true;
                    if ("mtime".equals(order))
                        download = breakPoint.checkDown(fileDir, ftpFile.getTimestamp().getTimeInMillis());
                    if ("fname".equals(order)) download = breakPoint.checkDown(fileDir, fname);
                    if (download) _download(filepath.toString());
                }
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
        try (OutputStream fo = Files.newOutputStream(dstPath);
             BufferedOutputStream bo = new BufferedOutputStream(fo)) {
            ftpClient.retrieveFile(file, bo);
        }
        if (file.endsWith(".tar.gz")) {
            Utils.extractTar_Gz(dstPath.toString(), targetPath.toString());
            Files.delete(dstPath);
        }
    }
}
