package bo.digitalsignature.domain.usecases.document;

import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.entities.DsDocument;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.ILogger;
import bo.digitalsignature.domain.usecases.cypher.CreateCertAndPairKeyUseCase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import lombok.AllArgsConstructor;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

@AllArgsConstructor
public class SignDocumentUseCase {

    private ILogger log;
    private IDsDocumentRepository repository;
    private IDsUserRepository dsUserRepository;

    private java.security.cert.Certificate loadCertificate(String pathCertificate)
            throws DigitalSignatureException, CertificateException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

        try (FileInputStream fis = new FileInputStream(pathCertificate)) {
            return certFactory.generateCertificate(fis);
        } catch (IOException | CertificateException e) {
            log.error("No se pudo cargar el certificado del usuario.");
            throw new DigitalSignatureException(ErrorCode.SIGN_DOCUMENT_CERTIFICATE_FAIL_OPEN.getCode(),
                    e.getMessage(), e);
        }
    }

    private PdfSignatureAppearance signFeature(Path pathFile, String pathSignedDocument,
                                               int userId) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(pathFile.toAbsolutePath().toString());
        FileOutputStream os = new FileOutputStream(pathSignedDocument);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason("Firma para proteger la integridad del documento, " +
                "firmado por el usuario con el Id : " + userId);
        appearance.setLocation("Api Rest - DigitalSignature by alepaco.maton");
        appearance.setContact("alepaco.maton@gmail.com");
        appearance.setSignDate(Calendar.getInstance());
        appearance.setVisibleSignature(new Rectangle(
                100, 100, 500, 500), 1, "sig");

        BaseFont bf = BaseFont.createFont();
        appearance.getAppearance().setColorFill(BaseColor.BLUE);
        appearance.getAppearance().setFontAndSize(bf, 15);

        //Image img = Image.getInstance("./logo.png");
        //appearance.setImage(img);
        //appearance.getImage().setRotation(45);

        return appearance;
    }

    public DsDocument signDocument(Path pathFile, String pathFolderByUser, int userId) throws DigitalSignatureException, GeneralSecurityException, IOException, DocumentException {
        DsUser user = this.dsUserRepository.findById(userId);

        String pathSignedDocument = pathFolderByUser + File.separator +
                userId + File.separator + pathFile.getFileName().toString();

        java.security.cert.Certificate cert =  loadCertificate(user.getCert());
        PrivateKey privateKey = loadPrivateKey(user.getPrivateKey());

        PdfSignatureAppearance appearance = signFeature(pathFile,
                pathSignedDocument, userId);

        ExternalSignature pks = new PrivateKeySignature(privateKey,
                "SHA256", "BC");
        ExternalDigest digest = new BouncyCastleDigest();

        MakeSignature.signDetached(appearance, digest, pks,
                new java.security.cert.Certificate[]{cert},
                null, null, null, 0,
                MakeSignature.CryptoStandard.CMS);

        return repository.save(new DsDocument(
                null, userId, pathFile.getFileName().toString(),
                pathSignedDocument));
    }

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
