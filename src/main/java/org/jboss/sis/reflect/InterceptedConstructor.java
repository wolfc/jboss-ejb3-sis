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
package org.jboss.sis.reflect;

import org.jboss.sis.Interceptor;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class InterceptedConstructor<T> {
    private final Constructor<T> constructor;
    private final Interceptor interceptor;

    public InterceptedConstructor(final Constructor<T> constructor, final Interceptor interceptor) {
        this.constructor = constructor;
        this.interceptor = interceptor;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }

    public T newInstance(final Object... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Map<String, Object> contextData = new HashMap<String, Object>();
        InvocationContext context = new InvocationContext() {
            private T target;
            private Object[] parameters = initargs;

            @Override
            public Constructor getConstructor() {
                return constructor;
            }

            @Override
            public Map<String, Object> getContextData() {
                return contextData;
            }

            @Override
            public Method getMethod() {
                return null;
            }

            @Override
            public Object[] getParameters() {
                return parameters;
            }

            @Override
            public Object getTarget() {
                return target;
            }

            @Override
            public Object getTimer() {
                return null;
            }

            @Override
            public Object proceed() throws Exception {
                target = constructor.newInstance(parameters);
                return target;
            }

            @Override
            public void setParameters(Object[] params) {
                this.parameters = params;
            }
        };
        try {
            return (T) interceptor.invoke(context);
        } catch (InstantiationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
