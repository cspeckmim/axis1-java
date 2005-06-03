/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *  Runtime state of the engine
 */
package org.apache.axis.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.axis.context.MessageContext;

public class DependancyManager {
    private final static String MESSAGE_CONTEXT_INJECTION_METHOD = "init";
    public static void configureBusinussLogicProvider(Object obj, MessageContext msgctx)
        throws AxisFault {
        try {
            Class classToLoad = obj.getClass();
            Method method =
                classToLoad.getMethod(
                    MESSAGE_CONTEXT_INJECTION_METHOD,
                    new Class[] { MessageContext.class });
            if (method != null) {
                method.invoke(obj, new Object[] { msgctx });
            }
        } catch (SecurityException e) {
            throw new AxisFault(e);
        } catch (IllegalArgumentException e) {
            throw new AxisFault(e);
        } catch (NoSuchMethodException e) {
            throw new AxisFault(e);
        } catch (IllegalAccessException e) {
            throw new AxisFault(e);
        } catch (InvocationTargetException e) {
            throw new AxisFault(e);
        }
    }

}