package no.robert.methodref.exception;

import no.robert.methodref.MethodRef;

public class MustRegisterMethodFirst extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MustRegisterMethodFirst() {
        super("There has not been any method invocation registered with " +
                MethodRef.class.getSimpleName() + ".on(Class).method()");
    }
}
