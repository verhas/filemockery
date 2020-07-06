package javax0.filemockery;

import javax0.geci.engine.Geci;
import javax0.geci.fluent.Fluent;
import javax0.geci.fluent.FluentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestFileMockery {
    private static Function<String, File> fileProvider;

    private static File newFile(String name) {
        return fileProvider.apply(name);
    }

    @Test
    void testMockery() throws NoSuchFieldException, IllegalAccessException {
        FileMockeryBuilder.createFS()
            .directory("/Users/localuser/project").mark("proj")
            .directory("locator/")
            .files("a.txt", "b.txt", "c.txt", "d.txt")
            .kram("proj")
            .directory("target/")
            .files("a.txt", "b.txt", "c.txt", "d.txt")
            .file("e.txt")
            .directory("libretto/")
            .files("a.txt", "b.txt", "c.txt", "d.txt")
            .cwd("/Users/localuser/project/target/")
            .inject(TestFileMockery.class);
        final var z = newFile("/Users/localuser/project/locator");
        final var fileList = z.list();
        assertNotNull(fileList);
        assertEquals(4, fileList.length);
        assertEquals("a.txt", fileList[0]);
        assertEquals("b.txt", fileList[1]);
        assertEquals("c.txt", fileList[2]);
        assertEquals("d.txt", fileList[3]);
        final var e = newFile("/Users/localuser/project/target/e.txt");
        assertTrue(e.exists());
        assertTrue(e.isFile());
        final var ee = newFile("err.txt");
        assertFalse(ee.exists());
        assertEquals("/Users/localuser/project/target/err.txt", ee.getAbsolutePath());
        assertEquals("/Users/localuser/project/target/err.txt", ee.getAbsoluteFile().getAbsolutePath());
        assertEquals("err.txt", ee.getAbsoluteFile().getName());
    }

    @Nested
    class NonPrivateFileProviderTestCase {
        private Function<String, File> fileProvider;

        @Test
        void canInjectFileProviderForInstance() {
            try {
                FileMockeryBuilder.createFS()
                    .file("")
                    .cwd("test")
                    .inject(NonPrivateFileProviderTestCase.class, this, "fileProvider");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail();
            }
        }
    }

    @Test
    void generateFluentAPI() throws IOException {
        final var geci = new Geci();
        Assertions.assertFalse(
            geci.register(new Fluent()).generate()
        );
    }

    public static FluentBuilder FileMockeryBuilderGrammar() {
        final var grammar = FluentBuilder.from(FileMockeryBuilder.class).start("createFS").fluentType("MockeryBuilder");
        return grammar.zeroOrMore(grammar.oneOf(grammar.oneOf("file", "files").optional("kram"),
            grammar.oneOf("directory", "directories").optional("mark").optional("kram")))
            .one("cwd").optional("inject").one("build");
    }
}
