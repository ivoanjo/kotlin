/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.config;


import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KotlinCompilerVersion {
    private static final String VERSION;

    // True if the latest stable language version supported by this compiler has not yet been released.
    // Binaries produced by this compiler with that language version (or any future language version) are going to be marked
    // as "pre-release" and will not be loaded by release versions of the compiler.
    // Change this value before and after every major release
    private static final boolean IS_PRE_RELEASE = true;

    public static final String TEST_IS_PRE_RELEASE_SYSTEM_PROPERTY = "kotlin.test.is.pre.release";

    public static boolean isPreRelease() {
        String overridden = System.getProperty(TEST_IS_PRE_RELEASE_SYSTEM_PROPERTY);
        if (overridden != null) {
            return Boolean.parseBoolean(overridden);
        }

        return IS_PRE_RELEASE;
    }

    /**
     * @return version of this compiler
     */
    @NotNull
    public static String getVersion() {
        return VERSION;
    }

    static {
        try {
            BufferedReader versionReader = new BufferedReader(
                    new InputStreamReader(KotlinCompilerVersion.class.getResourceAsStream("/META-INF/compiler.version")));
            VERSION = versionReader.readLine();
            versionReader.close();
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to read compiler version from META-INF/compiler.version");
        }

        if (!VERSION.contains("-") && IS_PRE_RELEASE) {
            throw new IllegalStateException(
                    "IS_PRE_RELEASE cannot be true for a compiler without '-' in its version.\n" +
                    "Please change IS_PRE_RELEASE to false, commit and push this change to master"
            );
        }
    }
}
