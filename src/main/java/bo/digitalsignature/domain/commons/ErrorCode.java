package bo.digitalsignature.domain.commons;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {

    SUCCESSFUL("API_DS-0000"),
    ERROR_PROCESSING_THE_TRANSACTION("API_DS-0001"),
    CREATE_DS_USER_USER_NAME_IS_REQUIRED("API_DS-0002"),
    CREATE_DS_USER_USER_NAME_IS_INVALID("API_DS-0003"),
    CREATE_DS_USER_USER_NAME_ALREADY_EXIST("API_DS-0004");

    private String code;

    public String getCode() {
        return code;
    }

    public boolean isSuccessfull() {
        return code == SUCCESSFUL.getCode();
    }

}
