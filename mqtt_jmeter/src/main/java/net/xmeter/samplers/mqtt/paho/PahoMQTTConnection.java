package net.xmeter.samplers.mqtt.paho;

import javafx.application.ConditionalFeature;
import net.xmeter.samplers.mqtt.MQTTConnection;
import net.xmeter.samplers.mqtt.MQTTPubResult;
import net.xmeter.samplers.mqtt.MQTTQoS;
import net.xmeter.samplers.mqtt.MQTTSubListener;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PahoMQTTConnection implements MQTTConnection, MqttCallback {
    private static final Logger logger = Logger.getLogger(PahoMQTTConnection.class.getCanonicalName());

    public static final int PUBLISH_TIMEOUT = -1;

    private final MqttAsyncClient client;
    private MQTTSubListener listener;

    public PahoMQTTConnection(MqttAsyncClient client) {
        this.client = client;
        client.setCallback(this);
    }

    @Override
    public boolean isConnectionSucc() {
        return client.isConnected();
    }

    @Override
    public String getClientId() {
        return client.getClientId();
    }

    @Override
    public void disconnect() throws Exception {
        client.disconnect();
    }

    @Override
    public MQTTPubResult publish(String topicName, byte[] message, MQTTQoS qos, boolean retained) {
        try {
            int qosInt = PahoUtil.getQosInt(qos);
            IMqttToken token = client.publish(topicName, message, qosInt, retained);
            token.waitForCompletion(PUBLISH_TIMEOUT);
            return new MQTTPubResult(true);
        } catch (Exception e) {
            return new MQTTPubResult(false, e.getMessage());
        }
    }

    @Override
    public void subscribe(String[] topicNames, MQTTQoS qos, Runnable onSuccess, Consumer<Throwable> onFailure) {
        int[] qosList = new int[topicNames.length];
        Arrays.fill(qosList, PahoUtil.getQosInt(qos));
        try {
            client.subscribe(topicNames, qosList, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    onSuccess.run();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    onFailure.accept(exception);
                }
            });
        } catch (Exception e) {
            onFailure.accept(e);
        }
        onSuccess.run();
    }

    @Override
    public void setSubListener(MQTTSubListener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "PahoMQTTConnection{" +
                "clientId='" + client.getClientId() + '\'' +
                '}';
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.info("Connection lost: client " + client.getClientId() + ", reason: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        listener.accept(topic, message.toString(), () -> {});
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
