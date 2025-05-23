/*
 * Copyright 2021 KasukuSakura
 *
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
 *
 */

package gg.damia.mixbukkit.attach;

import java.lang.instrument.Instrumentation;

public class ExternalAgent {
    public static Instrumentation instrumentation;

    public static void premain(String opt, Instrumentation ins) {
        instrumentation = ins;
    }

    public static void agentmain(String opt, Instrumentation ins) {
        instrumentation = ins;
    }
}