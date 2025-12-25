package indi.yoloz.sample.utils;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * 17-5-14
 */
public class FilesAlterationListener implements FileAlterationListener {
    @Override
    public void onStart(FileAlterationObserver observer) {
        //todo something  start observer
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate:" + directory.getAbsolutePath());
    }

    @Override
    public void onDirectoryChange(File directory) {
        //todo something at directory change
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete:" + directory.getAbsolutePath());
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate:" + file.getAbsolutePath());
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange : " + file.getAbsolutePath());
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete :" + file.getAbsolutePath());
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        //todo something  stop observer
    }
}
