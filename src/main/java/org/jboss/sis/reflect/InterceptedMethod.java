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
public class InterceptedMethod {
    private final Method method;
    private final Interceptor interceptor;

    public InterceptedMethod(final Method method, final Interceptor interceptor) {
        this.method = method;
        this.interceptor = interceptor;
    }

    public Method getMethod() {
        return method;
    }

    public Object invoke(final Object obj, final Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Map<String, Object> contextData = new HashMap<String, Object>();
        InvocationContext context = new InvocationContext() {
            private Object[] parameters = args;

            @Override
            public Constructor getConstructor() {
                return null;
            }

            @Override
            public Map<String, Object> getContextData() {
                return contextData;
            }

            @Override
            public Method getMethod() {
                return method;
            }

            @Override
            public Object[] getParameters() {
                return parameters;
            }

            @Override
            public Object getTarget() {
                return obj;
            }

            @Override
            public Object getTimer() {
                return null;
            }

            @Override
            public Object proceed() throws Exception {
                return method.invoke(obj, parameters);
            }

            @Override
            public void setParameters(Object[] params) {
                this.parameters = params;
            }
        };
        try {
            return interceptor.invoke(context);
        } catch (RuntimeException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
