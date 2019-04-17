/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.revocation;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.dto.Environment;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIAuthenticationAdminClient;
import org.wso2.carbon.identity.oauth.event.AbstractOAuthEventInterceptor;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationResponseDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.model.RefreshTokenValidationDataDO;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RevocationListener extends AbstractOAuthEventInterceptor {

    private static final Log log = LogFactory.getLog(RevocationListener.class);


    @Override
    public void onPostTokenRevocationByClient(OAuthRevocationRequestDTO revokeRequestDTO, OAuthRevocationResponseDTO revokeResponseDTO, AccessTokenDO accessTokenDO, RefreshTokenValidationDataDO refreshTokenDO, Map<String, Object> params) throws IdentityOAuth2Exception {

        log.info("onPostTokenRevocationByClient: ");
        APIManagerConfiguration config = getApiManagerConfiguration();
        Set<String> tokens = new HashSet<String>();
        tokens.add(revokeRequestDTO.getToken());
        if (config.getApiGatewayEnvironments().size() <= 0) {
            log.info("failed");
        }else{
            Map<String, Environment> gatewayEnvs = config.getApiGatewayEnvironments();

            for (Environment environment : gatewayEnvs.values()) {
                if (log.isDebugEnabled()) {
                    log.debug("Going to remove tokens from the cache of the Gateway '" + environment.getName() + "'");
                }
                try {
                    APIAuthenticationAdminClient client = getApiAuthenticationAdminClient(environment);
                    client.invalidateCachedTokens(tokens);

                    log.debug("Removed cached tokens of the Gateway.");
                } catch (AxisFault axisFault) {
                    //log and continue invalidating caches of other Gateways (if any).
                    log.error("Error occurred while invalidating the Gateway Token Cache of Gateway '" +
                            environment.getName() + "'", axisFault);
                }
            }
        }
    }

    protected APIManagerConfiguration getApiManagerConfiguration() {
        return ServiceReferenceHolder.getInstance().
                getAPIManagerConfigurationService().getAPIManagerConfiguration();
    }

    protected APIAuthenticationAdminClient getApiAuthenticationAdminClient(Environment environment) throws AxisFault {
        return new APIAuthenticationAdminClient(environment);
    }

    @Override
    public boolean isEnabled() {

        return true;
    }

}
