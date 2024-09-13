package bo.digitalsignature.domain.usecases.document;

import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.cypher.CreateCertAndPairKeyUseCase;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@AllArgsConstructor
public class SignDocumentUseCase {

    private IMultiLanguageMessagesService mlms;
    private IDsDocumentRepository repository;
    private CreateCertAndPairKeyUseCase createCertAndPairKeyUseCase;

    private IDsUserRepository dsUserRepository;

  /*  public static void signDocument(String srcPdfPath, String destPdfPath,
                               String certPath, String privateKeyPath,
                               int userId) throws DigitalSignatureException, CertificateException {
        DsUser user = this.dsUserRepository.findById(userId);

        // Leer el certificado .cer
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

        Certificate cert;

        try (FileInputStream fis = new FileInputStream(certPath)) {
            cert = certFactory.generateCertificate(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Leer la clave privada
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        // Abrir el PDF a firmar
        PdfReader reader = new PdfReader(srcPdfPath);
        FileOutputStream os = new FileOutputStream(destPdfPath);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        // Configurar el campo de firma
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason("Firma Digital");
        appearance.setLocation("Localización");
        appearance.setVisibleSignature("signature");  // Nombre del campo de firma

        // Crear el objeto para la firma
        ExternalSignature pks = new PrivateKeySignature(privateKey, "SHA-256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();

        MakeSignature.signDetached(appearance, digest, pks, new Certificate[]{cert}, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
    }
*/
    public static PrivateKey loadPrivateKey(String privateKeyPath) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException
    {
        byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(CreateCertAndPairKeyUseCase.PAIR_KEY_ALGORITHM);
        return keyFactory.generatePrivate(spec);

    }

    public static PublicKey loadPublicKey(String publicKeyPath) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes
                = Files.readAllBytes(Paths.get(publicKeyPath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory
                = KeyFactory.getInstance(CreateCertAndPairKeyUseCase.PAIR_KEY_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }
}
