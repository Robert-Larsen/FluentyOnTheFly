package no.robert.methodref;

import static java.lang.reflect.Modifier.isFinal;
import static no.robert.methodref.proxy.ProxyFactory.proxy;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class InvocationRegistrar implements MethodInterceptor {

    private final InvocationRegistry registry;

    public InvocationRegistrar(final InvocationRegistry registry) {
        this.registry = registry;
    }

    
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
    	registry.register(method, args);
    	return nextInvocationRegistrarIfPossible(method.getReturnType());
    }

    
    protected Object nextInvocationRegistrarIfPossible(Class<?> returnType) {
        return isFinal(returnType.getModifiers())? null : proxy(returnType, this);
    }
    
}