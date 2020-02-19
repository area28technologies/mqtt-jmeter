package net.xmeter.samplers.mqtt.paho;

import net.xmeter.Constants;
import net.xmeter.samplers.mqtt.MQTTQoS;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.ArrayList;
import java.util.List;

public class PahoUtil {
    static final List<String> ALLOWED_PROTOCOLS;
    static {
        ALLOWED_PROTOCOLS = new ArrayList<>();
        ALLOWED_PROTOCOLS.add(Constants.TCP_PROTOCOL);
        ALLOWED_PROTOCOLS.add(Constants.SSL_PROTOCOL);
        ALLOWED_PROTOCOLS.add(Constants.WS_PROTOCOL);
        ALLOWED_PROTOCOLS.add(Constants.WSS_PROTOCOL);
    }

    static int getQosInt(MQTTQoS qos) {
        switch (qos) {
            case AT_MOST_ONCE: return 0;
            case AT_LEAST_ONCE: return 1;
            case EXACTLY_ONCE: return 2;
            default: throw new IllegalArgumentException("Unknown QoS: " + qos);
        }
    }

    static int getVersionInt(String mqttVersion) {
        if (Constants.MQTT_VERSION_3_1_1.equals(mqttVersion)) {
            return MqttConnectOptions.MQTT_VERSION_3_1_1;
        } else if (Constants.MQTT_VERSION_3_1.equals(mqttVersion)) {
            return MqttConnectOptions.MQTT_VERSION_3_1;
        }
        throw new IllegalArgumentException("Unknown MQTT version: " + mqttVersion);
    }
}
