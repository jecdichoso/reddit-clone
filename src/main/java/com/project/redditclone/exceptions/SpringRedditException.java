package com.project.redditclone.exceptions;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String exMessage, Exception exception) {
        //we create this so to not expose the technicalk info to the user -jecd
        super(exMessage, exception);
    }

    public SpringRedditException(String exMessage) {
        super(exMessage);
    }
}
