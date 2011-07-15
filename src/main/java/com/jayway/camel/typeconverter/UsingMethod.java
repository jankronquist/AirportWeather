/*
 * Copyright 2011 Jan Kronquist.
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
 */
package com.jayway.camel.typeconverter;

import java.lang.reflect.Method;

import org.apache.camel.Converter;
import org.apache.camel.FallbackConverter;
import org.apache.camel.spi.TypeConverterRegistry;

/**
 * Convert to a type if there is a public no-argument method in the value that returns exactly this type.
 */
@Converter
public class UsingMethod {
	@SuppressWarnings("unchecked")
	@FallbackConverter
    public static <T> T convertTo(Class<T> type, Object value, TypeConverterRegistry registry) throws Throwable {
		for (Method m : value.getClass().getMethods()) {
			if (m.getParameterTypes().length == 0 && type.equals(m.getReturnType())) {
				return (T) m.invoke(value);
			}
		}
		return null;
	}
}
