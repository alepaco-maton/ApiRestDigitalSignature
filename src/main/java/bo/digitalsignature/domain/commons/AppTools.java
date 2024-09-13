package bo.digitalsignature.domain.commons;

import bo.digitalsignature.domain.ports.ILogger;

import java.io.File;

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

}
