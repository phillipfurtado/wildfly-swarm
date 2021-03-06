package org.wildfly.swarm.datasources;

import java.io.IOException;
import java.io.StringWriter;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.container.Deployment;
import org.wildfly.swarm.container.util.XmlWriter;

/**
 */
public class DatasourceDeployment implements Deployment {

    private final Datasource ds;

    private final JavaArchive archive;

    public DatasourceDeployment(Container container, Datasource ds) {
        this.ds = ds;
        this.archive = container.create(ds.name() + "-ds.jar", JavaArchive.class);
    }

    public Archive getArchive() {
        return getArchive(false);
    }

    public Archive getArchive(boolean finalize) {

        if (finalize) {

            StringWriter str = new StringWriter();

            try (XmlWriter out = new XmlWriter(str)) {

                XmlWriter.Element datasources = out.element("datasources")
                        .attr("xmlns", "http://www.jboss.org/ironjacamar/schema")
                        .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
                        .attr("xsi:schemaLocation", "http://www.jboss.org/ironjacamar/schema http://docs.jboss.org/ironjacamar/schema/datasources_1_0.xsd");

                XmlWriter.Element datasource = datasources.element("datasource")
                        .attr("jndi-name", this.ds.jndiName())
                        .attr("enabled", "true")
                        .attr("use-java-context", "true")
                        .attr("pool-name", this.ds.name());

                datasource.element("connection-url")
                        .content(this.ds.connectionURL())
                        .end();

                datasource.element("driver")
                        .content(this.ds.driver())
                        .end();

                XmlWriter.Element security = datasource.element("security");

                if (this.ds.userName() != null) {
                    security.element("user-name")
                            .content(this.ds.userName())
                            .end();
                }

                if (this.ds.password() != null) {
                    security.element("password")
                            .content(this.ds.password())
                            .end();

                }
                security.end();
                datasource.end();
                datasources.end();

                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            this.archive.add(new StringAsset(str.toString()), "META-INF/" + this.ds.name() + "-ds.xml");
        }

        return this.archive;
    }
}
