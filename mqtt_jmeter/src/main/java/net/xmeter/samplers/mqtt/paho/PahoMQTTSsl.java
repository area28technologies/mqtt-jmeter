package net.xmeter.samplers.mqtt.paho;

import net.xmeter.samplers.mqtt.MQTTSsl;

import javax.net.ssl.SSLContext;

public class PahoMQTTSsl implements MQTTSsl {
    private final SSLContext sslContext;

    PahoMQTTSsl(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    SSLContext getSslContext() {
        return sslContext;
    }
}
