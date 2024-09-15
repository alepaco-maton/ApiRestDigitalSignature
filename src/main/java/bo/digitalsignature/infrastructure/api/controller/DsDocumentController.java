package bo.digitalsignature.infrastructure.api.controller;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.entities.DsDocument;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.ILogger;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.document.ReadDsDocumentUseCase;
import bo.digitalsignature.domain.usecases.document.SignDocumentUseCase;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.infrastructure.api.dto.dsdocument.SignedDocumentResponse;
import bo.digitalsignature.infrastructure.api.exception.ApiDigitalSignatureExceptionResponse;
import bo.digitalsignature.infrastructure.persistence.entity.DsDocumentEntity;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.util.*;
import java.util.random.RandomGenerator;

@RestController
@Tag(name = "Documentos API", description = "APIs para gestionar los documentos de los usuarios.")
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.GET})
@RequestMapping(DsDocumentController.API_RESOURCE)
public class DsDocumentController {

    public static final String API_RESOURCE = "/api/v1/document";

    @Value("${path.folder.by.user}")
    private String pathFolderByUser;

    @Autowired
    ILogger log;

    @Autowired
    IMultiLanguageMessagesService messagesService;

    @Autowired
    IDsDocumentRepository repository;

    @Autowired
    IDsUserRepository dsUserRepository;

    ReadDsDocumentUseCase readDsDocumentUseCase;
    SignDocumentUseCase signDocumentUseCase;

    @PostConstruct
    public void init() {
        this.readDsDocumentUseCase = new ReadDsDocumentUseCase(repository);
        this.signDocumentUseCase = new SignDocumentUseCase(log, repository, dsUserRepository);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Firmar Documento",
            description = "Este metodo nos permite firmar los docuemntos del usuario.")
    @PostMapping(consumes = "multipart/form-data",
            produces = "application/json; charset=UTF-8")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK - Documento firmado"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Errores generales de solicitud (sintaxis, formato, URL).",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type - Solo soporta adjuntos de tipo PDF.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Errores de validación de datos específicos de la API.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error al procesar la solicitud.",
                    content = {
                            @Content(mediaType = "application/json", schema
                                    = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))})
    })
    public ResponseEntity<SignedDocumentResponse> signature(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") int userId) throws DigitalSignatureException{
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
        }

        try {
            Path filePath = makeTempFile(file, userId);

            DsDocument dsDocument = this.signDocumentUseCase.signDocument(
                    filePath, pathFolderByUser, userId);

            AppTools.deleteDirectoryRecursively(filePath.getParent().toAbsolutePath());

            return ResponseEntity.ok(new SignedDocumentResponse(
                    dsDocument.getId(), dsDocument.getFileName()));
        } catch (Exception e) {
            throw new DigitalSignatureException(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    e.getMessage(), e);
        }
    }

    private Path makeTempFile(MultipartFile file, int userId) throws IOException {
        Path tempDirectory = Files.createTempDirectory(
                "DigitalSignatureTempFolder" +
                        "_" + UUID.randomUUID() + "_" + userId);

        //Path uploadPath = Paths.get(pathFolderRoot+ File.separator + userId);
        //Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String uniqueFileName = baseName + "_" + UUID.randomUUID() + "." + extension;

        Path tempFile = tempDirectory.resolve(uniqueFileName);
        file.transferTo(tempFile.toFile());

        /*String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String uniqueFileName = baseName + "_" + UUID.randomUUID() + "." + extension;

        Path filePath = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath;
        */

        return tempFile;
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Obtener documento firmado",
            description = "Este metodo nos permite obtener el documento firmado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok - Solciitud procesada", content = {
                    @Content(mediaType = "application/pdf")
            }),
            @ApiResponse(responseCode = "404", description = "Not Found - Documento no encontrado.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DigitalSignatureNotFoundException.class))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error al procesar la solicitud.",
                    content = {
                            @Content(mediaType = "application/json", schema
                                    = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))})
    })
    @GetMapping(path = "/download/{id}")
    public ResponseEntity<InputStreamResource> download(@Parameter(name = "id",
            description = "Identificador unico del documento.",
            example = "1") @PathVariable int id) throws DigitalSignatureNotFoundException {
        DsDocumentEntity model = repository.findEntityById(id);

        if (model == null) {
            throw new DigitalSignatureNotFoundException(ErrorCode.DOWNLOAD_DS_DOCUMENT_ID_INVALID.getCode(),
                            messagesService.getMessage(ErrorCode.DOWNLOAD_DS_DOCUMENT_ID_INVALID.getCode()));
        }

        File file = new File(model.getPath());

        if (!file.exists()) {
            throw new DigitalSignatureNotFoundException(ErrorCode.DOWNLOAD_DS_DOCUMENT_PATH_INVALID.getCode(),
                    messagesService.getMessage(ErrorCode.DOWNLOAD_DS_DOCUMENT_PATH_INVALID.getCode()));
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fis);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.attachment().
                    filename(file.getName(), StandardCharsets.UTF_8).build());

            String contentType = Files.probeContentType(Path.of(file.getAbsolutePath()));
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.length())
                    .body(resource);

        } catch (Exception e) {
            throw new DigitalSignatureNotFoundException(
                    ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    messagesService.getMessage(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode()));
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Obtener Informacion de la Firma del Documento",
            description = "Este metodo nos permite obtener informacion de la firma del documento.")
    @PostMapping(path = "/signatureInfo", consumes = "multipart/form-data",
            produces = "application/json; charset=UTF-8")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK - Documento firmado"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Errores generales de solicitud (sintaxis, formato, URL).",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type - Solo soporta adjuntos de tipo PDF.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - Errores de validación de datos específicos de la API.",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error al procesar la solicitud.",
                    content = {
                            @Content(mediaType = "application/json", schema
                                    = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))})
    })
    public ResponseEntity<Map<String, Object>> signatureInfo(
            @RequestParam("file") MultipartFile file) throws DigitalSignatureException{
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
        }

        try {
            Path filePath = makeTempFile(file, RandomGenerator.getDefault().nextInt());

            Map<String, Object> response = new HashMap<>();

            PdfReader reader = new PdfReader(filePath.toFile().getAbsolutePath());
            AcroFields fields = reader.getAcroFields();
            List<String> signatureNames = fields.getSignatureNames();

            if (signatureNames.isEmpty()) {
                response.put("message", "El PDF no está firmado.");
                AppTools.deleteDirectoryRecursively(filePath.getParent().toAbsolutePath());
                return ResponseEntity.ok(response);
            }

            for (String signatureName : signatureNames) {
                PdfPKCS7 pkcs7 = fields.verifySignature(signatureName);
                boolean isSignatureValid = pkcs7.verify();

                // Obtener la cadena de certificados
                Certificate[] signCerts = pkcs7.getSignCertificateChain();

                // Crear un mapa para almacenar la información de la firma
                Map<String, Object> signatureInfo = new HashMap<>();
                signatureInfo.put("SignName", pkcs7.getSignName());
                signatureInfo.put("DigestAlgorithm", pkcs7.getDigestAlgorithm());
                signatureInfo.put("DigestAlgorithmOid", pkcs7.getDigestAlgorithmOid());
                signatureInfo.put("DigestEncryptionAlgorithmOid", pkcs7.getDigestEncryptionAlgorithmOid());
                signatureInfo.put("EncryptionAlgorithm", pkcs7.getEncryptionAlgorithm());
                signatureInfo.put("HashAlgorithm", pkcs7.getHashAlgorithm());
                signatureInfo.put("Location", pkcs7.getLocation());
                signatureInfo.put("Reason", pkcs7.getReason());
                signatureInfo.put("Version", pkcs7.getVersion());
                signatureInfo.put("SigningInfoVersion", pkcs7.getSigningInfoVersion());
                signatureInfo.put("SignDate", pkcs7.getSignDate().getTime());
                signatureInfo.put("isSignatureValid", isSignatureValid);

                // Obtener información del certificado
                Certificate cert = signCerts[0];
                signatureInfo.put("certificate", cert.toString());

                response.put(signatureName, signatureInfo);
            }

            AppTools.deleteDirectoryRecursively(filePath.getParent().toAbsolutePath());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new DigitalSignatureException(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    e.getMessage(), e);
        }
    }



}
