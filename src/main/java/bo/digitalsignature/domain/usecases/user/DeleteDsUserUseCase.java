package bo.digitalsignature.domain.usecases.user;

import bo.digitalsignature.domain.commons.AppTools;
import bo.digitalsignature.domain.commons.DigitalSignatureException;
import bo.digitalsignature.domain.commons.DigitalSignatureNotFoundException;
import bo.digitalsignature.domain.commons.ErrorCode;
import bo.digitalsignature.domain.ports.IDsDocumentRepository;
import bo.digitalsignature.domain.ports.IDsUserRepository;
import bo.digitalsignature.domain.ports.IMultiLanguageMessagesService;
import bo.digitalsignature.domain.usecases.user.validator.DeleteDsUserValidator;
import bo.digitalsignature.infrastructure.api.dto.dsuser.ListDsUserDsDocumentResponse;
import lombok.AllArgsConstructor;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

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

        this.repository.deleteById(id);

        try {
            AppTools.deleteDirectoryRecursively(Paths.get(pathFolderByUser + File.separator + id));
        } catch (IOException e) {
            throw new DigitalSignatureException(ErrorCode.ERROR_PROCESSING_THE_TRANSACTION.getCode(),
                    e.getMessage(), e);
        }

    }
}
