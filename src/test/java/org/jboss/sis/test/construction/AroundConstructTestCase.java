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
package org.jboss.sis.test.construction;

import org.jboss.sis.Interceptor;
import org.jboss.sis.reflect.InterceptedClass;
import org.jboss.sis.reflect.InterceptedConstructor;
import org.jboss.sis.reflect.InterceptedMethod;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class AroundConstructTestCase {
    public static class Greeter {
        private final String prompt;

        public Greeter(final String prompt) {
            this.prompt = prompt;
        }

        public String say(final String name) {
            return String.format(prompt, name);
        }
    }

    @Test
    public void testConstruct() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final InterceptedClass<Greeter> cls = new InterceptedClass<Greeter>(Greeter.class)
                .aroundConstruct(new Interceptor() {
                    @Override
                    public Object invoke(final InvocationContext context) throws Exception {
                        context.setParameters(new Object[] { "Hi %s" });
                        return context.proceed();
                    }
                })
                .aroundInvoke(new Interceptor() {
                    @Override
                    public Object invoke(final InvocationContext context) throws Exception {
                        return context.proceed() + "!";
                    }
                });
        final InterceptedConstructor<Greeter> constructor = cls.getConstructor(String.class);
        final Greeter greeter = constructor.newInstance("Hello %s");
        final InterceptedMethod method = cls.getMethod("say", String.class);
        final String result = (String) method.invoke(greeter, "testConstruct");
        assertEquals("Hi testConstruct!", result);
    }
}
