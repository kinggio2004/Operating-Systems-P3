// Using java.nio.file to interact with the OS file system via modern system calls
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileService {

    // CREATE: Create a new empty file
    public void createFile(Path path) throws IOException {
        Files.createFile(path);
    }

    // READ: Get content as a String
    public String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    // UPDATE: Write text to a file
    public void updateFile(Path path, String content) throws IOException {
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // DELETE: Delete file or directory
    public void deletePath(Path path) throws IOException {
        Files.delete(path);
    }

    

        // ... your existing save and delete methods ...

        /**
         * Fetches a fresh list of files from the OS Kernel.
         */
        public List<Path> refreshDirectory(Path rootPath) throws IOException {
            List<Path> files = new ArrayList<>();
            
            // This opens a stream to the NTFS file table
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootPath)) {
                for (Path entry : stream) {
                    files.add(entry);
                }
            }
            return files;
        }
    
 // METADATA: Get file properties (Size, Timestamps, and Permissions)
    public String getFileInfo(Path path) throws IOException {
        // 1. Get basic attributes (Size and Creation Time)
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        
       
     // Checking OS-level permissions to determine if the file is modifiable
        boolean readable = Files.isReadable(path);
        boolean writable = Files.isWritable(path);
    

        // Return everything in one formatted string
        return String.format(
            "Size: %d bytes\n" +
            "Created: %s\n" +
            "Last Modified: %s\n" +
            "Is Directory: %b\n" +
            "Readable: %b\n" +
            "Writable: %b",
            attr.size(), 
            attr.creationTime(), 
            attr.lastModifiedTime(),
            attr.isDirectory(),
            readable, 
            writable
        );
    }
 // Implementing an Atomic operation to prevent race conditions or file corruption
    public void renameFile(Path source, String newName) throws IOException {
        // .resolveSibling(newName) takes the folder path and swaps the filename
        // ATOMIC_MOVE ensures the OS treats this as one single step
        Files.move(source, source.resolveSibling(newName), StandardCopyOption.ATOMIC_MOVE);
    }
}
