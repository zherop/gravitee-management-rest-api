/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.rest.resource;

import io.gravitee.common.data.domain.MetadataPage;
import io.gravitee.common.data.domain.Page;
import io.gravitee.common.http.MediaType;
import io.gravitee.management.model.audit.AuditEntity;
import io.gravitee.management.model.audit.AuditQuery;
import io.gravitee.management.model.permissions.RolePermission;
import io.gravitee.management.model.permissions.RolePermissionAction;
import io.gravitee.management.rest.resource.param.AuditParam;
import io.gravitee.management.rest.security.Permission;
import io.gravitee.management.rest.security.Permissions;
import io.gravitee.management.service.AuditService;
import io.gravitee.repository.management.model.*;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Api(tags = {"Audit"})
@Path("/audit")
public class AuditResource extends AbstractResource  {

    @Context
    private ResourceContext resourceContext;

    @Autowired
    private AuditService auditService;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.MANAGEMENT_AUDIT, acls = RolePermissionAction.READ)
    })
    public MetadataPage<AuditEntity> list(@BeanParam AuditParam param){

        AuditQuery query = new AuditQuery();
        query.setFrom(param.getFrom());
        query.setTo(param.getTo());
        query.setPage(param.getPage());
        query.setSize(param.getSize());
        if (param.isManagementLogsOnly()) {
            query.setManagementLogsOnly(true);
        } else {
            if (param.getApiId() != null) {
                query.setApiIds(Collections.singletonList(param.getApiId()));
            }
            if (param.getApplicationId() != null) {
                query.setApplicationIds(Collections.singletonList(param.getApplicationId()));
            }
        }

        if (param.getEvent() != null) {
            query.setEvents(Collections.singletonList(param.getEvent()));
        }

        return auditService.search(query);
    }

    @Path("/events")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.MANAGEMENT_AUDIT, acls = RolePermissionAction.READ)
    })
    public Response getEvents() {
        List<Audit.AuditEvent> events = new ArrayList<>();
        events.addAll(Arrays.asList(io.gravitee.repository.management.model.Api.AuditEvent.values()));
        events.addAll(Arrays.asList(ApiKey.AuditEvent.values()));
        events.addAll(Arrays.asList(Application.AuditEvent.values()));
        events.addAll(Arrays.asList(Group.AuditEvent.values()));
        events.addAll(Arrays.asList(Membership.AuditEvent.values()));
        events.addAll(Arrays.asList(Metadata.AuditEvent.values()));
        events.addAll(Arrays.asList(io.gravitee.repository.management.model.Page.AuditEvent.values()));
        events.addAll(Arrays.asList(Plan.AuditEvent.values()));
        events.addAll(Arrays.asList(Role.AuditEvent.values()));
        events.addAll(Arrays.asList(Subscription.AuditEvent.values()));
        events.addAll(Arrays.asList(Tag.AuditEvent.values()));
        events.addAll(Arrays.asList(Tenant.AuditEvent.values()));
        events.addAll(Arrays.asList(User.AuditEvent.values()));
        events.addAll(Arrays.asList(View.AuditEvent.values()));

        events.sort(Comparator.comparing(Audit.AuditEvent::name));
        return Response.ok(events).build();
    }
}