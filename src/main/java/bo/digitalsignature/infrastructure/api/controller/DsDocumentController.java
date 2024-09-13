package bo.digitalsignature.infrastructure.api.controller;

import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.ILogger;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.document.SignDocumentUseCase;
import bo.digitalsignature.domain.usecases.cypher.CreateCertAndPairKeyUseCase;
import bo.digitalsignature.domain.usecases.user.CreateDsUserUseCase;
import bo.digitalsignature.domain.usecases.user.validator.CreateDsUserValidator;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.infrastructure.api.dto.dsdocument.SignedDocumentResponse;
import bo.digitalsignature.infrastructure.api.exception.ApiDigitalSignatureExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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
    IDsUserRepository repository;

    CreateDsUserUseCase createDsUserUseCase;
    SignDocumentUseCase signDocumentUseCase;

    @PostConstruct
    public void init() {
        CreateDsUserValidator validator = new CreateDsUserValidator(repository);
        CreateCertAndPairKeyUseCase createCertAndPairKeyUseCase = new CreateCertAndPairKeyUseCase(log);
        this.createDsUserUseCase = new CreateDsUserUseCase(messagesService, repository,
                validator, createCertAndPairKeyUseCase);
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
            @RequestParam("userId") Long userId) throws DigitalSignatureException, IOException {
        Path filePath = makeFile(file, pathFolderByUser, userId);

        return ResponseEntity.ok(new SignedDocumentResponse(-1, filePath.getFileName().toString()));
        //DsDocument dsDocument = this.signDocumentUseCase.signDocuemnt(filePath, userId);

        //return ResponseEntity.ok(new SignedDocumentResponse(dsDocument.getId(), dsDocument.getFileName()));
    }

    private Path makeFile(MultipartFile file, String pathFolderRoot, Long userId) throws IOException {
        Path uploadPath = Paths.get(pathFolderRoot+ File.separator + userId);
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String uniqueFileName = baseName + "_" + UUID.randomUUID() + "." + extension;

        Path filePath = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath;
    }

}
