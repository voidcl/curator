/*
 *
 *  Copyright 2011 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.curator.framework.recipes.locks;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.api.CuratorUnhandledErrorListener;

public interface ClientClosingListener<T> extends CuratorUnhandledErrorListener
{
    /**
     * Called if the client is closed for some reason. If this is called, the lock that was held
     * is no longer held.
     *
     * @param lock the lock that was previously held (it is no longer valid)
     * @param client the client
     */
    public void     notifyClientClosing(T lock, CuratorFramework client);
}
