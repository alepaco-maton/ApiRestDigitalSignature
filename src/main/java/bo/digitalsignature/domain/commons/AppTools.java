package bo.digitalsignature.domain.commons;

import bo.digitalsignature.domain.ports.ILogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class AppTools {

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static void deleteFile(String path, ILogger log) {
        File archivo = new File(path);

        if (archivo.exists()) {
            if (archivo.delete()) {
                log.info("Archivo eliminado correctamente, "+ path);
            } else {
                log.info("No se pudo eliminar el archivo, "+path);
            }
        } else {
            log.info("El archivo no existe, "+path);
        }
    }

    public static  void deleteDirectoryRecursively(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file); // Eliminar archivo
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir); // Eliminar directorio después de eliminar archivos dentro
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
