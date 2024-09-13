package bo.digitalsignature.domain.usecases.cypher;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.entities.DsUser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import bo.digitalsignature.domain.ports.ILogger;
import lombok.AllArgsConstructor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;

@AllArgsConstructor
public class CreateCertAndPairKeyUseCase {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static final int PAIR_KEY_SIZE = 2048;
    public static final String PAIR_KEY_ALGORITHM = "RSA";

    private final ILogger log;

    public DsUser create(DsUser dsUser, String pathFolderByUser) throws DigitalSignatureException {
        try {
            final String pathFolder = pathFolderByUser + File.separator + dsUser.getId();

            dsUser.setPrivateKey(pathFolder + File.separator +
                    "privateKey_" + dsUser.getId() + ".key");

            dsUser.setPublicKey(pathFolder + File.separator +
                    "publicKey_" + dsUser.getId() + ".key");

            KeyPair keyPair = createPair(PAIR_KEY_SIZE, pathFolderByUser, pathFolder,
                    dsUser.getPrivateKey(),
                    dsUser.getPublicKey());

            dsUser.setCert(pathFolder + File.separator +
                    "cert_" + dsUser.getId() + ".cer");

            createCertificate(dsUser.getCert(), keyPair);
        } catch (Exception e) {
            throw new DigitalSignatureException(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    e.getMessage(), e);
        } finally {
            return dsUser;
        }
    }

    private String createCertificate(String certPath, KeyPair keyPair) throws Exception {
        // Crear el certificado
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal issuer = new X500Principal("CN=Test Certificate");

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(issuer);
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365));
        certGen.setSubjectDN(issuer); // Certificado autofirmado
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        // Firmar el certificado con la clave privada
        X509Certificate cert = certGen.generateX509Certificate(keyPair.getPrivate(), "BC");

        // Guardar el certificado en un archivo .cer
        try (FileOutputStream fos = new FileOutputStream(certPath)) {
            fos.write(cert.getEncoded());
            fos.flush();
        }

        return certPath;
    }

    private KeyPair createPair(int keySize, String outputPath, String pathFolder,
                           String privateKeyPath, String publicKeyPath) throws Exception {
        // Generar un par de claves RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(PAIR_KEY_ALGORITHM);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Obtener las claves pública y privada
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        createFolderIfNotExist(outputPath);
        createFolderIfNotExist(pathFolder);
        AppTools.deleteFile(privateKeyPath, log);
        AppTools.deleteFile(publicKeyPath, log);

        // Guardar la clave pública en un archivo
        Files.write(Paths.get(publicKeyPath), publicKey.getEncoded());
        log.info("Clave pública guardada en: " + publicKeyPath);

        // Guardar la clave privada en un archivo
        Files.write(Paths.get(privateKeyPath), privateKey.getEncoded());
        log.info("Clave privada guardada en: " + privateKeyPath);

        return keyPair;
    }

    private void createFolderIfNotExist(String outputPath) {
        File directory = new File(outputPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Crear el directorio si no existe
        }
    }

}
