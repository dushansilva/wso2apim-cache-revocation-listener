package org.wso2.revocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.oauth.event.OAuthEventInterceptor;

@Component(name = "org.wso2.revocation",
        immediate = true)
public class RevocationComponent implements BundleActivator {
    private static final Log LOGGER = LogFactory.getLog(RevocationComponent.class);

    public void start(BundleContext bundleContext) throws Exception {
        LOGGER.info("RevocationComponent activated");

        bundleContext.registerService(OAuthEventInterceptor.class, new RevocationListener(), null);
    }

    public void stop(BundleContext bundleContext) throws Exception {

    }
}
