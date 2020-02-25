package net.xmeter.samplers.mqtt.paho;

import net.xmeter.Util;
import net.xmeter.samplers.mqtt.ConnectionParameters;
import net.xmeter.samplers.mqtt.MQTTClient;
import net.xmeter.samplers.mqtt.MQTTClientException;
import net.xmeter.samplers.mqtt.MQTTConnection;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PahoMQTTClient implements MQTTClient {
    private static final Logger logger = Logger.getLogger(PahoMQTTClient.class.getCanonicalName());
    private final ConnectionParameters parameters;
    private final MqttAsyncClient client;
    private final MqttConnectOptions connectOptions;

    PahoMQTTClient(ConnectionParameters parameters) throws Exception {
        this.parameters = parameters;
        connectOptions = createConnectOptions(parameters);
        String hostAddress = Util.getFullAddress(parameters);
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttAsyncClient(hostAddress, parameters.getClientId(), persistence);
    }

    private MqttConnectOptions createConnectOptions(ConnectionParameters parameters)
            throws Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        if (parameters.getReconnectMaxAttempts() > 0) {
            // We'll have to manage the number of attempts manually
            options.setAutomaticReconnect(true);
        }
        if (parameters.isWebSocketProtocol()) {
            if (parameters.getWebsocketAuthHeader() != null && parameters.getWebsocketAuthHeader().length() > 0) {
                Properties props = new Properties();
                props.setProperty("Authorization", parameters.getWebsocketAuthHeader());
                options.setCustomWebSocketHeaders(props);
            }
        }
        options.setMqttVersion(PahoUtil.getVersionInt(parameters.getVersion()));
        options.setConnectionTimeout(parameters.getConnectTimeout());
        options.setCleanSession(parameters.isCleanSession());
        options.setKeepAliveInterval(parameters.getKeepAlive());
        if (parameters.getUsername() != null) {
            options.setUserName(parameters.getUsername());
        }
        if (parameters.getPassword() != null) {
            options.setPassword(parameters.getPassword().toCharArray());
        }
        return options;
    }

    @Override
    public String getClientId() {
        return parameters.getClientId();
    }

    @Override
    public MQTTConnection connect() throws Exception {
        logger.info(() -> "Connecting: client= " + parameters.getClientId() + ", address=" + client.getServerURI());
        IMqttToken token = client.connect(connectOptions);
        token.waitForCompletion(-1);
        logger.info(() -> "Client connected: " + parameters.getClientId());
        return new PahoMQTTConnection(client);
    }
}
