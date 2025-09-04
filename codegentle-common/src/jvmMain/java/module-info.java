module love.forte.codegentle.common {
    requires java.compiler;
    requires kotlin.stdlib;

    exports love.forte.codegentle.common;
    exports love.forte.codegentle.common.code;
    // TODO exports .. to .. Only?
    exports love.forte.codegentle.common.codepoint;
    exports love.forte.codegentle.common.naming;
    exports love.forte.codegentle.common.ref;
    exports love.forte.codegentle.common.spec;
    exports love.forte.codegentle.common.utils;
    exports love.forte.codegentle.common.writer;
}
