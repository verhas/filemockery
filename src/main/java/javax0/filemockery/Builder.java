package javax0.filemockery;

import java.io.File;
import java.util.function.Function;

/**
 * The interfaces that provide the fluent API for the FileMockeryBuilder. The grammar is
 * <p>
 * (register| register mark | register mark kram | register kram mark | up ) + cwd inject* [build]
 */
public interface Builder {
    /**
     * <p>Create a new FileMockeryBuilder and return it. The return type is the interface that contains the methods
     * that
     * can be used at the start of the builder.</p>
     *
     * @return the new FileMockeryBuilder
     */
    static Start createFS() {
        return new FileMockeryBuilder();
    }

    interface Start {
        Start up();

        Kramarkable directories(String... pathNames);
        Kramarkable directory(String pathNames);
        Kramable files(String... pathNames);
        Kramable file(String pathNames);

        Ending cwd(String dir);
    }

    interface Kramarkable extends Start {
        Markable kram(String name);

        Kramable mark(String name);
    }

    interface Kramable extends Start {
        Start kram(String name);
    }

    interface Markable extends Start {
        Start mark(String name);
    }

    interface Ending {
        Ending inject(Class<?> targetClass)
            throws NoSuchFieldException, IllegalAccessException;

        Ending inject(Class<?> targetClass, String fieldName)
            throws NoSuchFieldException, IllegalAccessException;

        Ending inject(Class<?> targetClass, Object instance, String fieldName)
            throws NoSuchFieldException, IllegalAccessException;

        Function<String, File> build();
    }
}
