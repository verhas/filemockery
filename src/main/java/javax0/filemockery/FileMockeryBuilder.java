package javax0.filemockery;

import javax0.geci.annotations.Geci;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Geci("fluent definedBy='javax0.filemockery.TestFileMockery::FileMockeryBuilderGrammar'")
class FileMockeryBuilder {

    private String cwd = null;
    private final Map<String, FileMockery> fileMap = new HashMap<>();

    private final Map<String, FileMockery> symbolics = new HashMap<>();
    private FileMockery lastParent = null;
    private boolean buildPhase = true;

    private FileMockeryBuilder cwd(String setCwd) {
        if (cwd != null) {
            throw new IllegalArgumentException("You cannot set more than one CWD.\n" +
                "It is already '" + cwd + "'\n" +
                "Second value is '" + setCwd + "'");
        }
        while (setCwd.endsWith("/")) {
            setCwd = setCwd.substring(0, setCwd.length() - 1);
        }
        cwd = setCwd;
        return this;
    }

    private FileMockeryBuilder mark(String name) {
        symbolics.put(name, lastParent);
        return this;
    }

    private FileMockeryBuilder kram(String name) {
        if (!symbolics.containsKey(name)) {
            throw new IllegalArgumentException("as(\"" + name + "\" was not used cannot cd(\"" + name + "\") to it");
        }
        lastParent = symbolics.get(name);
        return this;
    }

    private FileMockeryBuilder up() {
        if (lastParent == null) {
            throw new IllegalArgumentException("Cannot go up from the root directory");
        }
        lastParent = lastParent.getParentFile();
        return this;
    }

    private FileMockeryBuilder directory(String pathName) {
        return directories(pathName);
    }

    private FileMockeryBuilder directories(String... pathNames) {
        lastParent = register(lastParent, true, pathNames);
        return this;
    }

    private FileMockeryBuilder file(String pathName) {
        return files(pathName);
    }

    private FileMockeryBuilder files(String... pathNames) {
        register(lastParent, false, pathNames);
        return this;
    }

    private FileMockery register(FileMockery parentFile, boolean directory, String... pathNames) {
        FileMockery it = null;
        for (final var pathName : pathNames) {
            it = registerOne(parentFile, directory, pathName);
        }
        return it;
    }

    private FileMockery registerOne(FileMockery parentFile, boolean directory, String pathName) {
        return registerIsAbsolute(parentFile, directory, pathName, false);
    }

    private FileMockery registerIsAbsolute(FileMockery parentFile, boolean directory, String pathName, boolean absolute) {
        final FileMockery it;
        FileMockery parent;

        if (pathName.startsWith("/") && !absolute) {
            parent = null;
            pathName = pathName.substring(1);
        } else {
            parent = parentFile;
        }

        if (pathName.contains("/") && !absolute) {
            if (!directory) {
                throw new IllegalArgumentException("File name should not contain '/' character as in '" + pathName + "'");
            }
            for (final var dir : pathName.split("/")) {
                parent = registerOne(parent, true, dir);
            }
            return parent;
        } else {
            it = createMockery(parentFile, directory, pathName, parent);
        }
        return it;
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
            it.absoluteFile = registerIsAbsolute(parentFile, directory, it.absoluteFileName, true);
        }
        it.absoluteFile.fileList = it.fileList;
        return it;
    }

    private FileMockeryBuilder inject(Class<?> targetClass, Object... params)
        throws NoSuchFieldException, IllegalAccessException {
        if (params.length == 1) {
            return inject2(targetClass, (String) params[0]);
        }
        if (params.length > 1) {
            return inject3(targetClass, params[0], (String) params[1]);
        }
        return inject2(targetClass, "fileProvider");
    }

    private FileMockeryBuilder inject2(Class<?> targetClass, String fieldName)
        throws NoSuchFieldException, IllegalAccessException {
        return inject3(targetClass, null, fieldName);
    }

    private FileMockeryBuilder inject3(Class<?> targetClass, Object instance, String fieldName)
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
            final var it = registerIsAbsolute(currDir, false, s, s.startsWith("/"));
            it.fileExists = false;
            it.absoluteFile.fileExists = false;
        }
        return fileMap.get(s);
    }

    //<editor-fold id="fluent" desc="fluent API interfaces and classes">
    public interface MockeryBuilder extends Uhab {}
    public static MockeryBuilder createFS(){
        return new Wrapper();
    }
    public static class Wrapper implements Ukeg,Abok,Efeh,MockeryBuilder,Ujaj,Ogoj,Edak,Acuh,Aduf,Uhab,Ohug{
        private final javax0.filemockery.FileMockeryBuilder that;
        public Wrapper(){
            this.that = new javax0.filemockery.FileMockeryBuilder();
        }
        public Wrapper file(String arg1){
            that.file(arg1);
            return this;
        }
        public Wrapper files(String...  arg1){
            that.files(arg1);
            return this;
        }
        public Wrapper mark(String arg1){
            that.mark(arg1);
            return this;
        }
        public Wrapper directories(String...  arg1){
            that.directories(arg1);
            return this;
        }
        public java.util.function.Function<String,java.io.File> build(){
            return that.build();
        }
        public Wrapper cwd(String arg1){
            that.cwd(arg1);
            return this;
        }
        public Wrapper kram(String arg1){
            that.kram(arg1);
            return this;
        }
        public Wrapper inject(Class<?> arg1, Object...  arg2) throws NoSuchFieldException,IllegalAccessException{
            that.inject(arg1,arg2);
            return this;
        }
        public Wrapper directory(String arg1){
            that.directory(arg1);
            return this;
        }
    }
    public interface Aduf {
        java.util.function.Function<String,java.io.File> build();
    }
    public interface Ukeg extends Aduf {
        Aduf inject(Class<?> arg1, Object...  arg2) throws NoSuchFieldException,IllegalAccessException;
    }
    public interface Ohug {
        Ukeg cwd(String arg1);
    }
    public interface Acuh extends Efeh {
        Efeh kram(String arg1);
    }
    public interface Ujaj extends Acuh {
        Acuh mark(String arg1);
    }
    public interface Ogoj{
        Ujaj directories(String...  arg1);
        Ujaj directory(String arg1);
    }
    public interface Edak extends Efeh {
        Efeh kram(String arg1);
    }
    public interface Abok{
        Edak file(String arg1);
        Edak files(String...  arg1);
    }
    public interface Uhab extends Abok,Ogoj{
    }
    public interface Efeh extends Uhab,Ohug {}

    //</editor-fold>
}
