/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.curator.x.rest.api;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.x.rest.CuratorRestContext;
import org.apache.curator.x.rest.entities.StatusMessage;

class RestBackgroundCallback implements BackgroundCallback
{
    private final CuratorRestContext context;
    private final String type;
    private final String asyncId;

    RestBackgroundCallback(CuratorRestContext context, String type, String asyncId)
    {
        this.context = context;
        this.type = type;
        this.asyncId = asyncId;
    }

    @Override
    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception
    {
        context.getSession().pushMessage(new StatusMessage(type, asyncId, getMessage(event), getDetails(event)));
    }

    protected String getDetails(CuratorEvent event)
    {
        return Integer.toString(event.getResultCode());
    }

    protected String getMessage(CuratorEvent event)
    {
        return String.valueOf(event.getName());
    }
}