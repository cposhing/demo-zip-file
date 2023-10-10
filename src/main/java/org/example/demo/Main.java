package org.example.demo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello world!");

//        createTestFiles();
        var dir = new File("C:\\Users\\Administrator\\IdeaProjects\\demo-zip-file\\.file\\20231009213821");

        //zipDir(dir);

        List<File> files = populateFilesList(dir);
        System.out.println(files);
    }

    private static void zipDir(File sourceDir) {

        if (sourceDir == null || !sourceDir.exists()) {
            throw new RuntimeException("sourceDir cannot be null !");
        }

        if (!sourceDir.isDirectory()) {
            throw new RuntimeException("sourceDir not a directory !");
        }

        var files = populateFilesList(sourceDir);

        var zipFile = new File(sourceDir.getParent(), sourceDir.getName() + ".zip");

        try (var zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File f : files) {
                /*
                sourceDir: C:\Users\Administrator\IdeaProjects\demo-zip-file\.file\20231009213821
                           C:\Users\Administrator\IdeaProjects\demo-zip-file\.file\20231009213821\6-test.txt
                           C:\Users\Administrator\IdeaProjects\demo-zip-file\.file\20231009213821\demo-spring.zip
                           C:\Users\Administrator\IdeaProjects\demo-zip-file\.file\20231009213821\inner-test\inner-test.txt
                 */
                var zipEntryPath = f.getCanonicalPath().substring(sourceDir.getCanonicalPath().length() + 1);
                var entry = new ZipEntry(zipEntryPath);
                zipOut.putNextEntry(entry);
                try (var fis = new FileInputStream(f)) {
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("some error happened !", e);
        }
    }


    private static File createTestFiles() {
        var homeDir = makeHomeDir();
        try {
            for (int i = 0; i < 10; i++) {
                var file = new File(homeDir, i + "-test.txt");
                try (var out = new FileOutputStream(file)) {
                    out.write(String.valueOf(Thread.currentThread().toString()).getBytes());
                }
            }
            return homeDir;
        } catch (Exception e) {
            throw new RuntimeException("some error happened !", e);
        }
    }

    private static File makeHomeDir() {
        var homeDir = new File(BASE_FILE_PATH, getStringNow());
        if (!homeDir.mkdirs()) {
            throw new RuntimeException(String.format("can not create dir : %s", homeDir));
        }
        return homeDir;
    }

    private static final String BASE_FILE_PATH = System.getProperty("user.dir") +
                                                 File.separator + ".file";

    private static String getStringNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private static List<File> populateFilesList(File dir) {

        class PopulateFiles {

            public PopulateFiles() {
                System.out.println("PopulateFiles 构造" + dir.getPath());
            }

            private final List<File> result = new ArrayList<>();

            public List<File> getFiles() {
                return result;
            }

            private void populateFilesList(File dir) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    if (file.isFile()) result.add(file);
                    else populateFilesList(file);
                }
            }
        }

        var populateFiles = new PopulateFiles();
        populateFiles.populateFilesList(dir);
        return populateFiles.getFiles();
    }

}