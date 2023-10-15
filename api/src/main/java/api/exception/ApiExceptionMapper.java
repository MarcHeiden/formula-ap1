package api.exception;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiExceptionMapper {

    ApiExceptionInfo toApiExceptionInfo(ApiException apiException);
}
