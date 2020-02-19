package net.xmeter.samplers.mqtt.paho;

import net.xmeter.AcceptAllTrustManagerFactory;
import net.xmeter.Util;
import net.xmeter.samplers.AbstractMQTTSampler;
import net.xmeter.samplers.mqtt.ConnectionParameters;
import net.xmeter.samplers.mqtt.MQTTClient;
import net.xmeter.samplers.mqtt.MQTTFactory;
import net.xmeter.samplers.mqtt.MQTTSsl;

import java.util.List;
import java.util.logging.Logger;

import static net.xmeter.Constants.PAHO_MQTT_CLIENT_NAME;

public class PahoMQTTFactory implements MQTTFactory {
    private static final Logger logger = Logger.getLogger(PahoMQTTFactory.class.getCanonicalName());

    @Override
    public String getName() {
        return PAHO_MQTT_CLIENT_NAME;
    }

    @Override
    public List<String> getSupportedProtocols() {
        return PahoUtil.ALLOWED_PROTOCOLS;
    }

    @Override
    public MQTTClient createClient(ConnectionParameters parameters) throws Exception {
        return new PahoMQTTClient(parameters);
    }

    @Override
    public MQTTSsl createSsl(AbstractMQTTSampler sampler) throws Exception {
        return new PahoMQTTSsl(Util.getContext(sampler));
    }
}
