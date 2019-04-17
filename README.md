1. After building the project the listener jar should be copied to <ISKM_HOME>/repository/components/dropins

 
 2. The following configuration should be added to <ISKM_HOME>/repository/conf/identity/identity.xml under <EventListeners> tag
```
<EventListeners>
......
<EventListener type="org.wso2.carbon.identity.oauth.event.OAuthEventInterceptor"
                       name="<event listener class with package name>"
                       orderId="11" enable="true" />                    
........    
</EventListeners>
```
 

For example for the *sample* provided 

```
<EventListeners>
.......
<EventListener type="org.wso2.carbon.identity.oauth.event.OAuthEventInterceptor"
                       name="org.wso2.revocation.RevocationListener"
                       orderId="11" enable="true" />                    
........    
</EventListeners>
```

3. Edit the <ISKM_HOME>/repository/conf/api-manager.xml APIGateway Environment <ServerURL> this url should contain the server url of your Gateway.
```
    <APIGateway>
        <Environments>

            <Environment ....>
             ........
                <ServerURL>https://<GATEWAY_IP>/services/</ServerURL>
             ..........
            </Environment>

        </Environments>
    </APIGateway>
```