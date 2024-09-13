package bo.digitalsignature.infrastructure.api.controller;

import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.entities.DsUser;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.ILogger;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.document.ReadDsDocumentUseCase;
import bo.digitalsignature.domain.usecases.pairKeys.CreatePairKeyUseCase;
import bo.digitalsignature.domain.usecases.user.CreateDsUserUseCase;
import bo.digitalsignature.domain.usecases.user.ReadDsUserUseCase;
import bo.digitalsignature.domain.usecases.user.UpdateDsUserUseCase;
import bo.digitalsignature.domain.usecases.user.validator.CreateDsUserValidator;
import bo.digitalsignature.domain.usecases.user.validator.UpdateDsUserValidator;
import bo.digitalsignature.infrastructure.api.dto.dsuser.*;
import bo.digitalsignature.infrastructure.api.exception.ApiDigitalSignatureExceptionResponse;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Usuarios API", description = "APIs para gestionar los usuarios.")
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.GET})
@RequestMapping(DsUserController.API_RESOURCE)
public class DsUserController {

    public static final String API_RESOURCE = "/api/v1/user";

    @Autowired
    ILogger log;

    @Autowired
    IMultiLanguageMessagesService messagesService;

    @Autowired
    IDsUserRepository repository;

    @Autowired
    IDsDocumentRepository dsDocumentRepository;

    CreateDsUserUseCase createDsUserUseCase;

    ReadDsUserUseCase readDsUserUseCase;

    ReadDsDocumentUseCase readDsDocumentUseCase;

    UpdateDsUserUseCase updateDsUserUseCase;

/*    DeleteCustomerService deleteClientService;*/

    @PostConstruct
    public void init() {
        {
            CreateDsUserValidator validator = new CreateDsUserValidator(repository);
            CreatePairKeyUseCase createPairKeyUseCase = new CreatePairKeyUseCase(log);
            this.createDsUserUseCase = new CreateDsUserUseCase(messagesService, repository,
                validator, createPairKeyUseCase);
        }

        this.readDsUserUseCase = new ReadDsUserUseCase(repository);
        this.readDsDocumentUseCase = new ReadDsDocumentUseCase(dsDocumentRepository);

        {
            UpdateDsUserValidator validator = new UpdateDsUserValidator(repository);
            this.updateDsUserUseCase = new UpdateDsUserUseCase(messagesService, repository,
                    validator);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear usuario",
            description = "Este metodo nos permite crear nuevos usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created - Recurso creado"),
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
    @PostMapping(consumes = "application/json; charset=UTF-8", produces = "application/json; charset=UTF-8")
    public ResponseEntity<CreateDsUserResponse> create(@RequestBody CreateDsUserRequest request) throws URISyntaxException, DigitalSignatureException {
        DsUser dsUser = new DsUser(request.getUserName());
        dsUser = createDsUserUseCase.create(dsUser);

        CreateDsUserResponse response = new CreateDsUserResponse(dsUser.getId(), dsUser.getUserName());

        return ResponseEntity.created(new URI("/" + response.getId())).body(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Listar usuarios",
            description = "Este metodo nos permite listar los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok - Solciitud procesada"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - Error al procesar la solicitud.",
                    content = {
                            @Content(mediaType = "application/json", schema
                                    = @Schema(implementation = ApiDigitalSignatureExceptionResponse.class))})
    })
    @GetMapping(produces = "application/json; charset=UTF-8")
    public List<ListDsUserResponse> list(
            @RequestParam(required = false) @Parameter(name = "userName",
                    description = "Filtrado por el contenido y no sensible a "
                            + "mayúsculas y minúsculas sobre el campo nombre de usuario.",
                    example = "alepaco.maton") String userName
    ) throws DigitalSignatureException {
        return readDsUserUseCase.list(userName).stream()
                .map(model -> new ListDsUserResponse(model.getId(), model.getUserName(),
                        readDsDocumentUseCase.findByDsUserId(model.getId()).stream().
                                map(modelDocument -> new ListDsUserDsDocumentResponse(
                                        modelDocument.getId(),
                                        modelDocument.getFileName())
                                ).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }


    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Actualizar usuario",
            description = "Este metodo nos permite actualizar los datos de los usuarios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok - Recurso actualizado"),
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
    @PutMapping(path = "/{id}", consumes = "application/json; charset=UTF-8", produces = "application/json; charset=UTF-8")
    public ResponseEntity update(@RequestBody UpdateDsUserRequest request,
                                 @Parameter(name = "id",
                                         description = "Identificador unico de usuario.",
                                         example = "1") @PathVariable int id)
            throws DigitalSignatureException, DigitalSignatureNotFoundException {
        updateDsUserUseCase.update(new DsUser(id, request.getUserName()));

        return ResponseEntity.ok().build();
    }
/*
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Eliminar cliente",
            description = "Este metodo nos permite eliminar clientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok - Recurso actualizado"),
            @ApiResponse(responseCode = "404", description = "Not Found - El recurso especificado "
                    + "no se ha encontrado en el servidor.",
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

    @DeleteMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity delete(@Parameter(name = "id",
            description = "Identificador unico de cliente.",
            example = "1") @PathVariable int id) throws DigitalSignatureException, ExceptionNotFoundResponse {
        deleteClientService.delete(id);

        return ResponseEntity.ok().build();
    }
*/

}
