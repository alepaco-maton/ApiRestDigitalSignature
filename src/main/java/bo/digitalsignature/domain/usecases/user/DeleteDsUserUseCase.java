package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.user.validator.DeleteDsUserValidator;
import lombok.AllArgsConstructor;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@AllArgsConstructor
public class DeleteDsUserUseCase {

    private IMultiLanguageMessagesService mlms;
    private IDsUserRepository repository;
    private DeleteDsUserValidator validator;
    private IDsDocumentRepository dsDocumentRepository;

    public void delete(int id, String pathFolderByUser) throws DigitalSignatureException, DigitalSignatureNotFoundException {
        ErrorCode errorCode = validator.validate(id);

        if (!errorCode.isSuccessfull()) {
            if (errorCode.getCode().equals(ErrorCode.DELETE_DS_USER_ID_NOT_FOUND.getCode())) {
                throw new DigitalSignatureNotFoundException(errorCode.getCode(),
                        mlms.getMessage(errorCode.getCode()));
            }

            throw new DigitalSignatureException(errorCode.getCode(),
                    mlms.getMessage(errorCode.getCode()));
        }

        this.dsDocumentRepository.deleteAllByDsUserId(id);

        repository.deleteById(id);


        try {
            deleteDirectoryRecursively(Paths.get(pathFolderByUser + File.separator + id));
        } catch (IOException e) {
            throw new DigitalSignatureException(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    e.getMessage(), e);
        }

    }

    public   void deleteDirectoryRecursively(Path path) throws IOException {
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
