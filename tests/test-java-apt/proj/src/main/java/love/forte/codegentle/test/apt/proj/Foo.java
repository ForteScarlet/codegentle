package love.forte.codegentle.test.apt.proj;

import love.forte.codegentle.test.apt.proc.annotation.IncludeToFactory;

/**
 * @author ForteScarlet
 */
public class Foo {
    final String name;

    private Foo(String name) {
        this.name = name;
    }

    @IncludeToFactory
    public static Foo createFoo(String name) {
        return new Foo(name);
    }
}
