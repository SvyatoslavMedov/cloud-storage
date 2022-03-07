package com.geekbrains.cloud.nio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.file.Files.readAllBytes;

public class Examples {
    public static void main(String[] args) throws IOException {
        //Path
        Path path = Paths.get("server","dir1");
        System.out.println(path);
        System.out.println(path.toAbsolutePath());
        System.out.println(path.resolve("1.txt"));
        Path notNormalise = path.resolve("..").resolve("dir1").resolve("1.txt");
        System.out.println(notNormalise);
        System.out.println(notNormalise.normalize());

        //Files
        path = path.resolve("1.txt");
        byte[] bytes = Files.readAllBytes(path);
        System.out.println(new String(bytes));
        System.out.println(Files.readAllLines(path));


        //write
        Path out = Paths.get("server","dir1","out.txt");
        Files.write(
                path.resolve("..").resolve("out.txt").normalize(),
                "dddd ".getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.WRITE.APPEND
        );

        Files.copy(
                out,
                Paths.get("server","dir1","copy.txt"),
                StandardCopyOption.REPLACE_EXISTING
        );

//        Files.createFile(path);
//        Files.createDirectory(path);


    }
}
