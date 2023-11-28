package metadata;

import domain.Book;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class MetadataBuilder {
    private String title = "";
    private String author = "";
    private String releaseDate = "";
    private String language = "";
    private String originalPublication = "";

    public Book buildMetadata(Path file, String id) {
        try {
            File f = new File(String.valueOf(file));
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("*** START OF THE PROJECT GUTENBERG")) {
                    break;
                }
                if (line.startsWith("Title: ")) {
                    title = line.replace("Title: ", "");
                }
                if (line.startsWith("Author: ")) {
                    author = line.replace("Author: ", "");
                }
                if (line.startsWith("Release date: ")) {
                    releaseDate = line.replace("Release date: ", "");
                }
                if (line.startsWith("Language: ")) {
                    language = line.replace("Language: ", "");
                }
                if (line.startsWith("Original publication: ")) {
                    originalPublication = line.replace("Original publication: ", "");
                }
            }
            return new Book(Integer.parseInt(id), title, author, Integer.parseInt(releaseDate), language, originalPublication);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
