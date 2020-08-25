package javax0.filemockery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>FileMockery is a mocking extension of the class {@link File} and can be used to test applications that use the
 * directory traversing and file listing functionality of the {@link File} class. It cannot be used to open a file and
 * read data from a file or write into a file.</p>
 *
 * To get access to a {@code FileMockery} instead of a file you need to structure your code in a way that it does not
 * explicitly uses {@code new File(...)} but uses some factory that can be modified in a way that it returns a {@code
 * FileMockery} instance instead of a genuine {@link File}. To create a {@code FileMockery}. There should be a {@code
 * private static Function<String, File> fileProvider = File::new;} field in the class and the builder can inject to
 * this field the file provider that will return the mock instead of a {@code File} when the tests are running.
 */
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
