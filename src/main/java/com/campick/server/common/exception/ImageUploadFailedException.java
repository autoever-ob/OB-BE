package com.campick.server.common.exception;

import com.campick.server.common.response.ErrorStatus;

public class ImageUploadFailedException extends BaseException {
    public ImageUploadFailedException(ErrorStatus errorStatus) {
        super(errorStatus.getHttpStatus(), errorStatus.getMessage());
    }
}
