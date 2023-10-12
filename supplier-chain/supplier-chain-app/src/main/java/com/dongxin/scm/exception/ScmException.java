package com.dongxin.scm.exception;

import org.jeecg.common.exception.JeecgBootException;

public class ScmException extends JeecgBootException {
    public ScmException(String message) {
        super(message);
    }

    public ScmException(Throwable cause) {
        super(cause);
    }

    public ScmException(String message, Throwable cause) {
        super(message, cause);
    }
}
