package org.wildfly.swarm.jaxrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoadException;

/**
 * @author Bob McWhirter
 */
public class FaviconHandler {
    public Response toResponse(NotFoundException e) {
        if (e.getMessage().contains("favicon.ico")) {
            try {
                Module module = Module.getBootModuleLoader().loadModule(ModuleIdentifier.create("org.wildfly.swarm.runtime.undertow"));
                ClassLoader cl = module.getClassLoader();
                final InputStream in = cl.getResourceAsStream("favicon.ico");
                if (in != null) {
                    Response.ResponseBuilder builder = Response.ok();
                    builder.entity( new FaviconEntity(in) );
                    return builder.build();
                }
            } catch (ModuleLoadException e1) {
                throw e;
            }
        }

        // can't handle it, rethrow.
        throw e;
    }
}
