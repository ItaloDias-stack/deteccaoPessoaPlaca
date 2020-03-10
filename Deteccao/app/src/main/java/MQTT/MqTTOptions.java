package MQTT;

import android.content.Context;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqTTOptions {

    static String MQTTSERVER ="tcp://m14.cloudmqtt.com:16962" ;

    static String MQTTUSER="placa1";
    static String MQTTSENHA="123";
    MqttAndroidClient client;
    String msg = "0";

    public MqttAndroidClient conect(final Context context) throws MqttException {

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, MQTTSERVER, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(MQTTUSER);
        options.setPassword(MQTTSENHA.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    sub();
                    Toast.makeText(context, "Conectado", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problemm
                    Toast.makeText(context, "Falha de conexão", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return client;
    }

    public void publica (String topico, String message){
        try {
            client.publish(topico, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sub(){
        try {
            client.subscribe("/casa/presenca", 0);
            client.subscribe("/casa/chuva", 0);
            client.subscribe("/casa/fogo", 0);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

}
