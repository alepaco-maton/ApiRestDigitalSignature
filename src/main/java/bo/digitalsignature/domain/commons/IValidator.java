package bo.digitalsignature.domain.commons;

public interface IValidator<T> {

    ErrorCode validate(T request);

}