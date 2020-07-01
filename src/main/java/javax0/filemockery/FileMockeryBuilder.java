package javax0.filemockery;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class FileMockeryBuilder implements
    Builder.Start, Builder.Ending, Builder.Markable, Builder.Kramable, Builder.Kramarkable {

    private String cwd = null;
    private final Map<String, FileMockery> fileMap = new HashMap<>();

    private final Map<String, FileMockery> symbolics = new HashMap<>();
    private FileMockery lastParent = null;
    private boolean buildPhase = true;

    public FileMockeryBuilder cwd(String setCwd) {
        if (cwd != null) {
            throw new IllegalArgumentException("You cannot set more than one CWD.\n" +
                "It is already '" + cwd + "'\n" +
                "Second value is '" + setCwd + "'");
        }
        while(setCwd.endsWith("/")){
            setCwd = setCwd.substring(0,setCwd.length()-1);
        }
        cwd = setCwd;
        return this;
    }

    public FileMockeryBuilder mark(String name) {
        symbolics.put(name, lastParent);
        return this;
    }

    public FileMockeryBuilder kram(String name) {
        if (!symbolics.containsKey(name)) {
            throw new IllegalArgumentException("as(\"" + name + "\" was not used cannot cd(\"" + name + "\") to it");
        }
        lastParent = symbolics.get(name);
        return this;
    }

    public FileMockeryBuilder up() {
        if (lastParent == null) {
            throw new IllegalArgumentException("Cannot go up from the root directory");
        }
        lastParent = lastParent.getParentFile();
        return this;
    }

    public FileMockeryBuilder directory(String pathName) {
        return directories(pathName);
    }

    public FileMockeryBuilder directories(String... pathNames) {
        lastParent = register(lastParent, true, pathNames);
        return this;
    }

    public FileMockeryBuilder file(String pathName) {
        return files(pathName);
    }

    public FileMockeryBuilder files(String... pathNames) {
        register(lastParent, false, pathNames);
        return this;
    }

    private FileMockery register(FileMockery parentFile, boolean directory, String... pathNames) {
        FileMockery it = null;
        for (final var pathName : pathNames) {
            it = register(parentFile, directory, pathName);
        }
        return it;
    }

    private FileMockery register(FileMockery parentFile, boolean directory, String pathName) {
        final FileMockery parent;

        if (pathName.startsWith("/")) {
            parent = null;
            pathName = pathName.substring(1);
        } else {
            parent = parentFile;
        }

        if (pathName.contains("/")) {
            return splitPath(directory, pathName, parent);
        } else {
            return createMockery(parentFile, directory, pathName, parent);
        }
    }

    private FileMockery createMockery(FileMockery parentFile, boolean directory, String pathName, FileMockery parent) {
        FileMockery it;
        it = new FileMockery(pathName, parent);
        it.fileIsDirectory = directory;
        it.fileExists = buildPhase;
        fileMap.put(pathName, it);
        if (it.parentFile == null) {
            it.absoluteFile = it;
            it.absoluteFileName = "/" + it.pathname;
        } else {
            if (buildPhase) {
                it.parentFile.fileList.add(pathName);
            }
            it.absoluteFileName = (parentFile.getAbsolutePath() + "/" + pathName).replaceAll("//", "/");
            it.absoluteFile = registerAbsolute(parentFile, directory, it.absoluteFileName);
        }
        it.absoluteFile.fileList = it.fileList;
        return it;
    }

    private FileMockery splitPath(boolean directory, String pathName, FileMockery parent) {
        if (!directory) {
            throw new IllegalArgumentException("File name should not contain '/' character as in '" + pathName + "'");
        }
        for (final var dir : pathName.split("/")) {
            parent = register(parent, true, dir);
        }
        return parent;
    }

    private FileMockery registerAbsolute(FileMockery parentFile, boolean directory, String pathName) {
        final FileMockery it = new FileMockery(pathName, parentFile);
        it.fileIsDirectory = directory;
        it.fileExists = buildPhase;
        fileMap.put(pathName, it);
        it.absoluteFileName = it.parentFile == null ? "/" + it.pathname : it.pathname;
        it.absoluteFile = it;
        it.absoluteFile.fileList = it.fileList;
        return it;
    }

    public FileMockeryBuilder inject(Class<?> targetClass)
        throws NoSuchFieldException, IllegalAccessException {
        return inject(targetClass, "fileProvider");
    }

    public FileMockeryBuilder inject(Class<?> targetClass, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
        return inject(targetClass, null, fieldName);
    }

    public FileMockeryBuilder inject(Class<?> targetClass, Object instance, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
        final var targetField = targetClass.getDeclaredField(fieldName);
        targetField.setAccessible(true);
        targetField.set(instance, build());
        return this;
    }

    public Function<String, File> build() {
        if (cwd == null) {
            throw new IllegalArgumentException("No CWD was set");
        }
        buildPhase = false;
        return this::apply;
    }

    private File apply(String s) {
        if (s.equals(".")) {
            return fileMap.get(cwd);
        }
        if (!fileMap.containsKey(s)) {
            final var currDir = fileMap.get(cwd);
            final FileMockery it;
            if (s.startsWith("/")) {
                it = registerAbsolute(currDir, false, s);
            } else {
                it = register(currDir, false, s);
            }
            it.fileExists = false;
            it.absoluteFile.fileExists = false;
        }
        return fileMap.get(s);
    }
}
