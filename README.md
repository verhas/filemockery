# File Mockery

`FileMockery` is a mocking extension of the class `File` and can be used to test applications that use the directory
traversing and file listing functionality of the `File` class. Using this mockery may be an easier alternative than
using Mockito or some other general purpose mocking framework.

To use the mocker `File` instances you have to eliminate the `new File(...)` codes in your application. Instead you
have to use a 

```java
private static Function<String, File> fileProvider = File::new;
```

field (note that this field is NOT `final`) and then you can get a new `File` replacing every `new File(name)` wirh
`fileProvider.apply(name)` for any 'name'.

The next step is to write the test code creating a file mockery using a builder. The package contains a builder equipped
with meaningful fluent API methods, with which you can define the individual files in the file structure, the current
working directory, and you can also inject a factory (file provider) into the field `fileProvider` into the SUT object.

The created and returned `File` objects are limited and not all methods are overridden in the mockery. They cannot be
used to open a file and read data from a file or write into a file. They can be used to list the files in a directory
traverse between directories and so on.

## Example

The best example is the code that we use to test the mockery itself. Here is the code copied (maybe different from the
latest version) annotated with comments.


```java
     @Test
     void testMockery() throws NoSuchFieldException, IllegalAccessException {
         FileMockeryBuilder.createFS()
             // create the directory `/Users/localuser/project`
             // it automatically also creates the directory `/Users` and `/Users/localuser`
             .directory("/Users/localuser/project")
             // mark the current location and assign the symbolic name "proj" to it
             .mark("proj")
             // create the directory `locator` under the actual directory, which is `/Users/localuser/project`
             // after this the actual directory will be `/Users/localuser/project/locator`
             .directory("locator/")
             // create the files `a.txt`, `b.txt` and so on under the actual directory that is `/Users/localuser/project/locator`
             .files("a.txt", "b.txt", "c.txt", "d.txt")
             // reverse `mark()` move back. The actual directory will be the one that was marked using
             // "proj", which is `/Users/localuser/project`
             .kram("proj")
             // create the directory `/Users/localuser/project/target`
             .directory("target/")
             // create the files under the directory `/Users/localuser/project/target`
             .files("a.txt", "b.txt", "c.txt", "d.txt")
             // create another file in this directory
             .file("e.txt")
             // go one directory deeper and create `/Users/localuser/project/target/libretto`
             .directory("libretto/")
             // create files here
             .files("a.txt", "b.txt", "c.txt", "d.txt")
             // the current working directory during the execution of the SUT will be `/Users/localuser/project/target/`
             .cwd("/Users/localuser/project/target/")
             // inject the `File` factory based on this mockery into the class (in this case this is a self contained
             // test so into itself
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
```

# Mocked methods

File mockery does not implement all the `File` methods. What it implements are

* `exists()`
* `isFile()`
* `isDirectory()`
* `getName()`
* `list()`
* `getAbsolutePath()`
* `getAbsoluteFile()`

these methods can be used to test an application code that needs to discover files in a directory hierarchy uning the
"old style" `File` class and it's methods.