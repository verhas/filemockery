package javax0.filemockery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class FileMockery extends File {
    final String pathname;
    FileMockery absoluteFile = null;
    String absoluteFileName = null;
    FileMockery parentFile;
    String name = null;
    List<String> fileList = new ArrayList<>();
    boolean fileIsDirectory;
    boolean fileExists;
    File contentFile;
    byte[] content;

    FileMockery(String pathname, FileMockery parentFile) {
        super(pathname);
        this.pathname = pathname;
        this.parentFile = parentFile;
        this.fileIsDirectory = pathname.length()> 0 && pathname.substring(1).contains(".");
    }

    @Override
    public boolean exists(){
        return fileExists;
    }

    @Override
    public boolean isFile() {
        return !fileIsDirectory;
    }

    @Override
    public boolean isDirectory() {
        return !isFile();
    }

    @Override
    public String getName() {
        if (name == null) {
            String choppedName = pathname;
            if (pathname.endsWith("/")) {
                choppedName = pathname.substring(0, pathname.length() - 1);
            }
            int i = choppedName.lastIndexOf('/');
            if (i == -1) {
                name = choppedName;
            } else {
                name = choppedName.substring(i + 1);
            }
        }
        return name;
    }

    @Override
    public String[] list() {
        return fileList.toArray(String[]::new);
    }

    @Override
    public FileMockery getParentFile() {
        return parentFile;
    }

    @Override
    public String getAbsolutePath() {
        if (pathname.startsWith("/")) {
            return pathname;
        }
        if (absoluteFileName == null) {
            absoluteFileName = (parentFile == null ? "/" : parentFile.getAbsolutePath() + pathname);
        }
        return absoluteFileName;
    }

    @Override
    public FileMockery getAbsoluteFile() {
        if (pathname.startsWith("/")) {
            return this;
        }
        if (absoluteFile == null) {
            absoluteFile = new FileMockery(getAbsolutePath(), parentFile);
            absoluteFile.fileList = fileList;
        }
        return absoluteFile;
    }
}
