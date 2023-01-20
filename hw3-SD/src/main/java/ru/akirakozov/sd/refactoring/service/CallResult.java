package ru.akirakozov.sd.refactoring.service;

@SuppressWarnings("unused")
public class CallResult<R, E extends Exception> {
    private final R result;
    private final E exception;

    public CallResult(final R result) {
        this.result = result;
        this.exception = null;
    }

    public CallResult(final E exception) {
        this.result = null;
        this.exception = exception;
    }

    public boolean isFailed() {
        return exception != null;
    }

    public boolean isOk() {
        return result != null;
    }

    public E getException() {
        return exception;
    }

    public R getResult() {
        return result;
    }

    public R unpackUnchecked() {
        if (isFailed()) {
            throw new IllegalStateException(exception);
        } else {
            return result;
        }
    }
}
