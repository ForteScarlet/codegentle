package love.forte.codegentle.internal.processor.enumset

import love.forte.codegentle.common.GenEnumSet

/**
 * Test enum class for the EnumSet processor.
 */
@GenEnumSet
enum class TestEnum {
    A, B, C, D, E
}

/**
 * Test enum class with internal visibility for the EnumSet processor.
 */
@GenEnumSet(internal = true)
enum class InternalTestEnum {
    A, B, C
}

/**
 * Test enum class with medium number of entries to test the I64EnumSet implementation.
 */
@GenEnumSet
enum class MediumTestEnum {
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    B1, B2, B3, B4, B5, B6, B7, B8, B9, B10,
    C1, C2, C3, C4, C5, C6, C7, C8, C9, C10,
    D1, D2, D3, D4, D5, D6, D7, D8, D9, D10,
    E1, E2, E3, E4, E5
}

/**
 * Test enum class with many entries to test the BigEnumSet implementation.
 */
@GenEnumSet
enum class BigTestEnum {
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10,
    B1, B2, B3, B4, B5, B6, B7, B8, B9, B10,
    C1, C2, C3, C4, C5, C6, C7, C8, C9, C10,
    D1, D2, D3, D4, D5, D6, D7, D8, D9, D10,
    E1, E2, E3, E4, E5, E6, E7, E8, E9, E10,
    F1, F2, F3, F4, F5, F6, F7, F8, F9, F10,
    G1, G2, G3, G4, G5, G6, G7, G8, G9, G10
}

/**
 * Test enum class with containerName parameter to test the container interface generation.
 */
@GenEnumSet(containerName = "TestEnumBuilderContainer")
enum class ContainerTestEnum {
    ONE, TWO, THREE
}

/**
 * Test enum class with containerName and operatorsName parameters to test the value class generation.
 */
@GenEnumSet(containerName = "OperatorsTestEnumBuilderContainer", operatorsName = "OperatorsTestEnumModifiers")
enum class OperatorsTestEnum {
    ALPHA, BETA, GAMMA
}

/**
 * Test enum class with containerName, operatorsName, containerSingleAdder, and containerMultiAdder parameters.
 */
@GenEnumSet(
    containerName = "CustomAdderTestEnumBuilderContainer", 
    operatorsName = "CustomAdderTestEnumModifiers",
    containerSingleAdder = "addElement",
    containerMultiAdder = "addElements"
)
enum class CustomAdderTestEnum {
    ONE, TWO, THREE
}

/**
 * Test enum class with entries that become Kotlin keywords when lowercase.
 * This tests the backtick escaping for Kotlin keywords in the generated value class.
 */
@GenEnumSet(
    containerName = "KeywordTestEnumBuilderContainer",
    operatorsName = "KeywordTestEnumModifiers"
)
enum class KeywordTestEnum {
    FUN, IN, IS, AS, OBJECT, CLASS, INTERFACE, VAL, VAR, WHEN, IF, ELSE, RETURN
}
