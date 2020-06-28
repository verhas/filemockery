# File Mockery

File mockery is a small utility to mock some methods of the java.io.File class. It is mainly intended to test
programs that need to traverse through the file system looking for files, listing and finding files.

The mock comes with a builder class. It should be used to describe the file structures, the names of the directories
and the files in it.