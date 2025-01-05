/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package love.forte.codepoet.java


/**
 *
 * see `javax.lang.model.element.Modifier`
 */
public enum class Modifier {

    // See JLS sections 8.1.1, 8.3.1, 8.4.3, 8.8.3, and 9.1.1.
    // java.lang.reflect.Modifier includes INTERFACE, but that's a VMism.

    /** The modifier `public` */
    PUBLIC,
    /** The modifier `protected` */
    PROTECTED,
    /** The modifier `private` */
    PRIVATE,
    /** The modifier `abstract` */
    ABSTRACT,

    /** The modifier `default` */
    DEFAULT,
    /** The modifier `static` */
    STATIC,
    /** The modifier `final` */
    FINAL,
    /** The modifier `transient` */
    TRANSIENT,
    /** The modifier `volatile` */
    VOLATILE,
    /** The modifier `synchronized` */
    SYNCHRONIZED,
    /** The modifier `native` */
    NATIVE,
    /** The modifier `strictfp` */
    STRICTFP;

    override fun toString(): String = name.lowercase()
}
