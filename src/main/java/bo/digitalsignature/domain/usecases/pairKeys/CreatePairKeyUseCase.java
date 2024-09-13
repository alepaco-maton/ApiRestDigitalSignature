package bo.digitalsignature.domain.usecases.pairKeys;

import bo.digitalsignature.domain.entities.DsUser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import bo.digitalsignature.domain.ports.ILogger;
import lombok.AllArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@AllArgsConstructor
public class CreatePairKeyUseCase {

    public static final int PAIR_KEY_SIZE = 2048;
    public static final String PAIR_KEY_ALGORITHM = "RSA";
    public static final String PATH_FOLDER_PAIR_KEY = "./pairkey";

    private final ILogger log;

    public DsUser create(DsUser dsUser) {
        try {
            final String pathFolder = PATH_FOLDER_PAIR_KEY + File.separator + dsUser.getId();

            dsUser.setPrivateKey(pathFolder + File.separator +
                    "privateKey_" + dsUser.getId() + ".key");
            dsUser.setPublicKey(pathFolder + File.separator +
                    "publicKey_" + dsUser.getId() + ".key");

            createPair(PAIR_KEY_SIZE, PATH_FOLDER_PAIR_KEY, pathFolder,
                    dsUser.getPrivateKey(),
                    dsUser.getPublicKey());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return dsUser;
    }

    public void createPair(int keySize, String outputPath, String pathFolder,
                           String privateKeyPath, String publicKeyPath) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        // Generar un par de claves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(PAIR_KEY_ALGORITHM);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obtener las claves pública y privada
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        createFolderIfNotExist(outputPath);
        createFolderIfNotExist(pathFolder);
        deleteFile(privateKeyPath);
        deleteFile(publicKeyPath);

        // Guardar la clave pública en un archivo
        Files.write(Paths.get(publicKeyPath), publicKey.getEncoded());
        log.info("Clave pública guardada en: " + publicKeyPath);

        // Guardar la clave privada en un archivo
        Files.write(Paths.get(privateKeyPath), privateKey.getEncoded());
        log.info("Clave privada guardada en: " + privateKeyPath);
    }

    private void createFolderIfNotExist(String outputPath) {
        File directory = new File(outputPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Crear el directorio si no existe
        }
    }

    private void deleteFile(String path) {
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
