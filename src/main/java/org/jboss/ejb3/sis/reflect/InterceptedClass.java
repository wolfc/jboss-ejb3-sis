/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.sis.reflect;

import org.jboss.ejb3.sis.Interceptor;
import org.jboss.ejb3.sis.NoopInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InterceptedClass<T> {
    private final Class<T> type;
    private Interceptor aroundConstructInterceptor = NoopInterceptor.INSTANCE;
    private Interceptor aroundInvokeInterceptor = NoopInterceptor.INSTANCE;

    public InterceptedClass(final Class<T> type) {
        this.type = type;
    }

    /**
     * Sets the interceptor to be used around intercepted constructor invocations.
     *
     * @param interceptor the around constructor interceptor
     * @return this
     */
    public InterceptedClass<T> aroundConstruct(final Interceptor interceptor) {
        this.aroundConstructInterceptor = interceptor;
        return this;
    }

    /**
     * Sets the interceptor to be used around intercepted method invocations.
     *
     * @param interceptor the method invocation interceptor
     * @return this
     */
    public InterceptedClass<T> aroundInvoke(final Interceptor interceptor) {
        this.aroundInvokeInterceptor = interceptor;
        return this;
    }

    public InterceptedConstructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        final Constructor<T> constructor = type.getConstructor(parameterTypes);
        return new InterceptedConstructor<T>(constructor, aroundConstructInterceptor);
    }

    public InterceptedMethod getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        final Method method = type.getMethod(name, parameterTypes);
        return new InterceptedMethod(method, aroundInvokeInterceptor);
    }
}
