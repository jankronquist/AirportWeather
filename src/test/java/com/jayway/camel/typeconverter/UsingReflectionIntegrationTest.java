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

import static org.mockito.Mockito.verify;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.mockito.Mockito;

public class UsingReflectionIntegrationTest extends CamelTestSupport {
    public static class SourceType {
        private final int value;

        public SourceType(int value) {
            this.value = value;
        }

        public TargetTypeForMethod convert() {
            return new TargetTypeForMethod(value);
        }
    }

    public static class TargetType {
        private final int value;

        public TargetType(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TargetType other = (TargetType) obj;
            if (value != other.value)
                return false;
            return true;
        }
    }

    public static class TargetTypeWithConstructor extends TargetType {
        public TargetTypeWithConstructor(int value) {
            super(value);
        }

        public TargetTypeWithConstructor(SourceType source) {
            super(source.value);
        }
    }

    public static class TargetTypeForMethod extends TargetType {
        public TargetTypeForMethod(int value) {
            super(value);
        }
    }

    public static interface Service {
        public void callRequiringConstructorConversion(TargetTypeWithConstructor value);
        public void callRequiringMethodConversion(TargetTypeForMethod value);
    }

    final Service service = Mockito.mock(Service.class);

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:callRequiringConstructorConversion").bean(service, "callRequiringConstructorConversion");
                from("direct:callRequiringMethodConversion").bean(service, "callRequiringMethodConversion");
            }
        };
    }

    @Test
    public void testCamelUsingConstructor() throws Exception {
        template.requestBody("direct:callRequiringConstructorConversion", new SourceType(17));
        verify(service).callRequiringConstructorConversion(new TargetTypeWithConstructor(17));
    }

    @Test
    public void testCamelUsingMetohd() throws Exception {
        template.requestBody("direct:callRequiringMethodConversion", new SourceType(17));
        verify(service).callRequiringMethodConversion(new TargetTypeForMethod(17));
    }
}
