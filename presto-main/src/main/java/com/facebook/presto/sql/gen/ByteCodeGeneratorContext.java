/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.sql.gen;

import com.facebook.presto.byteCode.ByteCodeNode;
import com.facebook.presto.byteCode.CompilerContext;
import com.facebook.presto.metadata.FunctionInfo;
import com.facebook.presto.metadata.FunctionRegistry;
import com.facebook.presto.sql.relational.RowExpression;

import java.util.List;

import static com.facebook.presto.sql.gen.ByteCodeUtils.generateInvocation;
import static com.google.common.base.Preconditions.checkNotNull;

public class ByteCodeGeneratorContext
{
    private final ByteCodeExpressionVisitor byteCodeGenerator;
    private final CompilerContext context;
    private final CallSiteBinder callSiteBinder;
    private final FunctionRegistry registry;

    public ByteCodeGeneratorContext(
            ByteCodeExpressionVisitor byteCodeGenerator,
            CompilerContext context,
            CallSiteBinder callSiteBinder,
            FunctionRegistry registry)
    {
        checkNotNull(byteCodeGenerator, "byteCodeGenerator is null");
        checkNotNull(context, "context is null");
        checkNotNull(callSiteBinder, "callSiteBinder is null");
        checkNotNull(registry, "registry is null");

        this.byteCodeGenerator = byteCodeGenerator;
        this.context = context;
        this.callSiteBinder = callSiteBinder;
        this.registry = registry;
    }

    public CompilerContext getContext()
    {
        return context;
    }

    public CallSiteBinder getCallSiteBinder()
    {
        return callSiteBinder;
    }

    public ByteCodeNode generate(RowExpression expression)
    {
        return expression.accept(byteCodeGenerator, context);
    }

    public FunctionRegistry getRegistry()
    {
        return registry;
    }

    /**
     * Generates a function call with null handling, automatic binding of session parameter, etc.
     */
    public ByteCodeNode generateCall(FunctionInfo function, List<ByteCodeNode> arguments)
    {
        Binding binding = callSiteBinder.bind(function.getMethodHandle());
        return generateInvocation(context, function, arguments, binding);
    }
}
